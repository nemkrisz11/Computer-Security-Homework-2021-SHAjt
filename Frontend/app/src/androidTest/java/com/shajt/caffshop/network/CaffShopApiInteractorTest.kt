package com.shajt.caffshop.network

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shajt.caffshop.data.models.Error
import com.shajt.caffshop.data.models.User
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
    fun testLogin_successful() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody("{\"token\":\"abcd\",\"expire\":10000,\"isAdmin\":false}")
        mockWebServer.enqueue(mockResponse)

        val userCredentials = UserCredentials("test", "test")
        val result = caffShopApiInteractor.login(userCredentials)

        assertNotNull(result.success)
        assertNull(result.error)

        // TODO change when interactor implementation is correct
        val user = User("test", "abcd")
        assertEquals(user, result.success)
    }

    @Test
    fun testLogin_unsuccessful() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
            .setBody("{\"errorCode\":001,\"errorMessage\":\"Invalid username or password\"}")
        mockWebServer.enqueue(mockResponse)

        val userCredentials = UserCredentials("test", "test")
        val result = caffShopApiInteractor.login(userCredentials)

        assertNull(result.success)
        assertNotNull(result.error)

        // TODO change when interactor implementation is correct
        assertEquals(Error.AUTH_FAILED, result.error)
    }

    @Test
    fun register() {
    }

    @Test
    fun getCaffs() {
    }
}