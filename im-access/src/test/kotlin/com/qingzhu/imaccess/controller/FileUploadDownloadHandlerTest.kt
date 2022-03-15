package com.qingzhu.imaccess.controller

import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyExtractors
import org.springframework.web.reactive.function.client.WebClient
import java.io.IOException
import java.io.InputStream
import java.io.StringWriter
import java.nio.charset.Charset


internal class FileUploadDownloadHandlerTest {

    @Test
    fun testFileName() {
        val (fileName, fileType) = createRandomFileNameByOriginalFilename("test.tar.bz")
        println("$fileName: $fileType")
        assertEquals("tar.bz", fileType)
    }

    @Test
    fun testDataBuffer() {
        val inputStream = readAsInputStream("https://www.qq.com/")
        val writer = StringWriter()
        IOUtils.copy(inputStream, writer, Charset.forName("gb2312"))
        val theString = writer.toString()
        println(theString)
    }

    @Throws(IOException::class)
    private fun readAsInputStream(url: String): InputStream {
        val webClient = WebClient.builder().baseUrl(url).build()
        val response = webClient.get()
            .accept(MediaType.TEXT_HTML)
            .exchange()
            .block()
        val body = response!!.body(BodyExtractors.toDataBuffers())

        return piped(body)
    }
}