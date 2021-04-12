package com.qingzhu.imaccess.controller

import io.minio.*
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.ZeroCopyHttpOutputMessage
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Flux
import java.util.*

val bucketExistsArgs: BucketExistsArgs = BucketExistsArgs.builder().bucket("chat-img").build()
val makeBucketArgs: MakeBucketArgs = MakeBucketArgs.builder().bucket("chat-img").build()

fun createRandomFileNameByOriginalFilename(originalFilename: String): Pair<String, String> {
    val fileName = UUID.randomUUID().toString()
    val fileType = originalFilename.substringAfter(".")
    val saveFileName = "${fileName.replace("-", "")}.$fileType"
    return Pair(saveFileName, fileType)
}

@RestController
class FileUploadDownloadHandler(
        val minioClient: MinioClient
) {
    suspend fun upload(sr: ServerRequest): ServerResponse {
        val response = ServerResponse.ok().build()
        return sr.multipartData().map { it["files"] }
                .flatMapMany { Flux.fromIterable(it) }
                .cast(FilePart::class.java)
                .flatMap { fp ->
                    fp
                            .content()
                            .map {
                                val inputStream = it.asInputStream()
                                // 上传到图片空间
                                if (minioClient.bucketExists(bucketExistsArgs).not()) {
                                    minioClient.makeBucket(makeBucketArgs)
                                }
                                val (fileName, _) = createRandomFileNameByOriginalFilename(fp.filename())
                                val putObjectArgs = PutObjectArgs.builder().bucket("chat-img")
                                        .`object`(fileName)
                                        .stream(inputStream, inputStream.available().toLong(), -1)
                                        .build()
                                minioClient.putObject(putObjectArgs)
                            }
                }
                .flatMap { response }
                .awaitSingle()

    }

    suspend fun download(sr: ServerRequest): ServerResponse {
        val imgName = sr.pathVariable("img")
        val getObjectArgs = GetObjectArgs.builder().bucket("chat-img")
                .`object`(imgName).build()
        return ServerResponse.ok().build { t, _ ->
            val zeroCopyResponse = t.response as ZeroCopyHttpOutputMessage
            zeroCopyResponse.writeWith(DataBufferUtils
                    .readInputStream({ minioClient.getObject(getObjectArgs) },
                            DefaultDataBufferFactory(), 4096))
        }
                .awaitSingle()
    }
}