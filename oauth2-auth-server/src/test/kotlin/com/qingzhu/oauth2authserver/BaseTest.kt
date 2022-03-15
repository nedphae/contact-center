package com.qingzhu.oauth2authserver

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import javax.servlet.http.Cookie


@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
abstract class BaseTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val objectMapper: ObjectMapper = ObjectMapper()

    private lateinit var cookies: Array<out Cookie>

    @Before
    fun init() {
        // 不启动 web_socket 服务器
        mockHandler()
    }

    private fun mockHandler() {
    }

    private fun login(): Array<out Cookie> {
        val result = this.mockMvc.perform(
            MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE).param("username", "admin")
                .param("password", "123456")
        ).andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isFound).andReturn()
        return result.response.cookies
    }

    /**
     * 获取 http 链接结果
     * @param requestBuilder request 参数
     */
    protected fun baseRequest(requestBuilder: MockHttpServletRequestBuilder) {
        this.mockMvc.perform(requestBuilder.cookie(*cookies)).andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print()).andReturn()
    }

    /**
     * 查询接口测试
     *
     * @param url 查询URL
     * @throws Exception 测试异常
     */
    @Throws(Exception::class)
    protected operator fun get(url: String) {
        baseRequest(MockMvcRequestBuilders.get(url))
    }

    /**
     * 增加接口测试
     *
     * @param url 增加url
     * @param t 插入的数据
     * @param <T> 数据类型
     * @throws Exception 测试异常
     */
    @Throws(Exception::class)
    protected fun <T> post(url: String, t: T) {
        baseRequest(
            MockMvcRequestBuilders.post(url).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(t))
        )
    }

    /**
     * 修改接口测试
     *
     * @param url 修改url
     * @param t 修改的数据
     * @param <T> 数据类型
     * @throws Exception 测试异常
     */
    @Throws(Exception::class)
    protected fun <T> put(url: String, t: T) {
        baseRequest(
            MockMvcRequestBuilders.put(url).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(t))
        )
    }

    /**
     * 删除接口测试
     *
     * @param url 删除url
     * @throws Exception 测试异常
     */
    @Throws(Exception::class)
    protected fun delete(url: String) {
        baseRequest(MockMvcRequestBuilders.delete(url))
    }
}