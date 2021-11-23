package com.shajt.caffshop.network

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.Gson
import com.shajt.caffshop.data.enums.ErrorMessage
import com.shajt.caffshop.data.models.Error
import com.shajt.caffshop.data.models.auth.LoginResult
import com.shajt.caffshop.data.models.auth.UserCredentials
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.net.HttpURLConnection

@RunWith(AndroidJUnit4::class)
class CaffShopApiInteractorTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var caffShopApiInteractor: CaffShopApiInteractor

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        caffShopApiInteractor = CaffShopApiInteractor(mockWebServer.url("/"))
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testRegister_successful() = runBlocking {
        val expectedRegisterResult = true
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_CREATED)
        mockWebServer.enqueue(mockResponse)

        val userCredentials = UserCredentials("test", "test")
        val serverResult = caffShopApiInteractor.register(userCredentials)

        assertNull(serverResult.error)
        assertNotNull(serverResult.result)
        assertEquals(expectedRegisterResult, serverResult.result)
    }

    @Test
    fun testRegister_unsuccessful() = runBlocking {
        val error = Error(2, "Username already taken")
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
            .setBody(Gson().toJson(error))
        mockWebServer.enqueue(mockResponse)

        val userCredentials = UserCredentials("test", "test")
        val serverResult = caffShopApiInteractor.register(userCredentials)

        assertNotNull(serverResult.error)
        assertNull(serverResult.result)
        assertEquals(ErrorMessage.REGISTRATION_FAILED, serverResult.error)
    }

    @Test
    fun testLogin_successful() = runBlocking {
        val expectedLoginResponse = LoginResult("abcd", 1000)
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(Gson().toJson(expectedLoginResponse))
        mockWebServer.enqueue(mockResponse)

        val userCredentials = UserCredentials("test", "test")
        val serverResult = caffShopApiInteractor.login(userCredentials)

        assertNull(serverResult.error)
        assertNotNull(serverResult.result)
        assertEquals(expectedLoginResponse, serverResult.result)
    }

    @Test
    fun testLogin_unsuccessful() = runBlocking {
        val error = Error(1, "Invalid username or password")
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
            .setBody(Gson().toJson(error))
        mockWebServer.enqueue(mockResponse)

        val userCredentials = UserCredentials("test", "test")
        val serverResult = caffShopApiInteractor.login(userCredentials)

        assertNotNull(serverResult.error)
        assertNull(serverResult.result)
        assertEquals(ErrorMessage.LOGIN_FAILED, serverResult.error)
    }
}