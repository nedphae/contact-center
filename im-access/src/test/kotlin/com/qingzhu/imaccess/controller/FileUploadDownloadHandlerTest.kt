package com.qingzhu.imaccess.controller

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class FileUploadDownloadHandlerTest {

    @Test
    fun testFileName() {
        val (fileName, fileType) = createRandomFileNameByOriginalFilename("test.tar.bz")
        println("$fileName: $fileType")
        assertEquals("tar.bz", fileType)
    }
}