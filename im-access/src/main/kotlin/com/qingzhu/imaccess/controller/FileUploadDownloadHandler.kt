package com.qingzhu.imaccess.controller

import io.minio.*
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.ZeroCopyHttpOutputMessage
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.InputStream
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.io.SequenceInputStream
import java.util.*

fun createRandomFileNameByOriginalFilename(originalFilename: String): Triple<String, String, String> {
    val fileName = UUID.randomUUID().toString()
    val fileType = originalFilename.substringAfter(".")
    val saveFileName = "${fileName.replace("-", "")}.$fileType"
    return Triple(saveFileName, fileName, fileType)
}

@Deprecated("小文件会卡死，而且没有任何异常，废弃")
fun piped(dataBuffer: Flux<DataBuffer>): InputStream {
    val osPipe = PipedOutputStream()
    val isPipe = PipedInputStream(osPipe)
    DataBufferUtils.write(dataBuffer
        .doOnError { isPipe.close() }
        .doFinally { osPipe.close() }, osPipe
    )
        .subscribe(DataBufferUtils.releaseConsumer())
    return isPipe
}

@RestController
class FileUploadDownloadHandler(
    val minioClient: MinioClient
) {
    suspend fun upload(sr: ServerRequest, bucket: String): ServerResponse {
        return sr.multipartData().flatMap { Mono.justOrEmpty(it["file"]) }
            .flatMapMany { Flux.fromIterable(it) }
            .cast(FilePart::class.java)
            .flatMap { fp ->
                val (saveFileName, _, _) = createRandomFileNameByOriginalFilename(fp.filename())
                // 合并 DataBuffer 的 stream
                fp.content().reduce(object : InputStream() {
                    override fun read() = -1
                }) { s: InputStream, d -> SequenceInputStream(s, d.asInputStream()) }
                    .map { inputStream ->
                        // 上传到图片空间
                        val bucketExistsArgs: BucketExistsArgs = BucketExistsArgs.builder().bucket(bucket).build()
                        if (minioClient.bucketExists(bucketExistsArgs).not()) {
                            val makeBucketArgs: MakeBucketArgs = MakeBucketArgs.builder().bucket(bucket).build()
                            minioClient.makeBucket(makeBucketArgs)
                        }
                        val putObjectArgs = PutObjectArgs.builder().bucket(bucket)
                            .`object`(saveFileName)
                            .stream(inputStream, -1, 10485760)
                            .contentType(fp.headers()["Content-Type"]?.first())
                            .build()
                        minioClient.putObject(putObjectArgs)
                    }
            }
            .collectList()
            .flatMap {
                ServerResponse.ok().bodyValue(it.map { response -> response.`object`() })
            }
            .switchIfEmpty(ServerResponse.ok().build())
            .awaitSingle()

    }

    suspend fun download(sr: ServerRequest, bucket: String): ServerResponse {
        val imgName = sr.pathVariable("fileName")
        val getObjectArgs = GetObjectArgs.builder().bucket(bucket)
            .`object`(imgName).build()
        return ServerResponse.ok().build { t, _ ->
            val zeroCopyResponse = t.response as ZeroCopyHttpOutputMessage
            zeroCopyResponse.writeWith(
                DataBufferUtils
                    .readInputStream(
                        {
                            val inputStream = minioClient.getObject(getObjectArgs)
                            inputStream.headers().names().map {
                                zeroCopyResponse.headers.add(it, inputStream.headers()[it])
                            }
                            inputStream
                        },
                        DefaultDataBufferFactory(), 4096
                    )
            )
        }
            .awaitSingle()
    }
}