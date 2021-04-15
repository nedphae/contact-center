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
import java.io.File
import java.util.*


val bucketExistsArgs: BucketExistsArgs = BucketExistsArgs.builder().bucket("chat-img").build()
val makeBucketArgs: MakeBucketArgs = MakeBucketArgs.builder().bucket("chat-img").build()

fun createRandomFileNameByOriginalFilename(originalFilename: String): Triple<String, String, String> {
    val fileName = UUID.randomUUID().toString()
    val fileType = originalFilename.substringAfter(".")
    val saveFileName = "${fileName.replace("-", "")}.$fileType"
    return Triple(saveFileName, fileName, fileType)
}

@RestController
class FileUploadDownloadHandler(
        val minioClient: MinioClient
) {
    suspend fun upload(sr: ServerRequest): ServerResponse {
        return sr.multipartData().map { it["files"] }
                .flatMapMany { Flux.fromIterable(it) }
                .cast(FilePart::class.java)
                .map { fp ->
                    val (saveFileName, fileName, fileType) = createRandomFileNameByOriginalFilename(fp.filename())
                    val file = File.createTempFile(fileName, fileType)
                    fp.transferTo(file).subscribe()
                    // 上传到图片空间
                    if (minioClient.bucketExists(bucketExistsArgs).not()) {
                        minioClient.makeBucket(makeBucketArgs)
                    }
                    val uploadObjectArgs = UploadObjectArgs.builder()
                            .bucket("chat-img")
                            .`object`(saveFileName)
                            .filename(file.absolutePath)
                            .contentType(fp.headers()["Content-Type"]?.first())
                            .build()
                    minioClient.uploadObject(uploadObjectArgs).also {
                        file.delete()
                    }
                }
                .collectList()
                .flatMap {
                    ServerResponse.ok().bodyValue(it.map { response -> response.`object`() })
                }
                .awaitSingle()

    }

    suspend fun download(sr: ServerRequest): ServerResponse {
        val imgName = sr.pathVariable("img")
        val getObjectArgs = GetObjectArgs.builder().bucket("chat-img")
                .`object`(imgName).build()
        return ServerResponse.ok().build { t, _ ->
            val zeroCopyResponse = t.response as ZeroCopyHttpOutputMessage
            zeroCopyResponse.writeWith(DataBufferUtils
                    .readInputStream({
                        val inputStream = minioClient.getObject(getObjectArgs)
                        inputStream.headers().names().map {
                            zeroCopyResponse.headers.add(it, inputStream.headers()[it])
                        }
                        inputStream
                    },
                            DefaultDataBufferFactory(), 4096))
        }
                .awaitSingle()
    }
}