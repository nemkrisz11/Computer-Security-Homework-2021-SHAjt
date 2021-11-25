package com.shajt.caffshop.network

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.Gson
import com.shajt.caffshop.data.enums.ErrorMessage
import com.shajt.caffshop.data.models.*
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
    fun testGetUsers_successful() = runBlocking {
        val expectedGetUsersResult =
            UserList(listOf(UserData("test", false, 1000)), 1)
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(Gson().toJson(expectedGetUsersResult))
        mockWebServer.enqueue(mockResponse)

        val token = "abcd"
        val serverResult = caffShopApiInteractor.getUsers(token)

        assertNull(serverResult.error)
        assertNotNull(serverResult.result)
        assertEquals(expectedGetUsersResult, serverResult.result)

        val receivedRequest = mockWebServer.takeRequest()

        assertEquals("GET", receivedRequest.method)
        assertEquals("/user?page=1&perpage=20", receivedRequest.path)
        assertEquals("Bearer $token", receivedRequest.getHeader("Authorization"))
    }

    @Test
    fun testGetUsers_unsuccessful() = runBlocking {
        val error = Error(1, "Invalid request")
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
            .setBody(Gson().toJson(error))
        mockWebServer.enqueue(mockResponse)

        val token = "abcd"
        val serverResult = caffShopApiInteractor.getUsers(token)

        assertNotNull(serverResult.error)
        assertNull(serverResult.result)
        assertEquals(ErrorMessage.USER_LIST_REQUEST_FAILED, serverResult.error)

        val receivedRequest = mockWebServer.takeRequest()

        assertEquals("GET", receivedRequest.method)
        assertEquals("/user?page=1&perpage=20", receivedRequest.path)
        assertEquals("Bearer $token", receivedRequest.getHeader("Authorization"))
    }

    @Test
    fun testGetUser_successful() = runBlocking {
        val expectedGetUserResult = UserData("test", false, 1000)
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(Gson().toJson(expectedGetUserResult))
        mockWebServer.enqueue(mockResponse)

        val token = "abcd"
        val serverResult = caffShopApiInteractor.getUser(token, "test")

        assertNull(serverResult.error)
        assertNotNull(serverResult.result)
        assertEquals(expectedGetUserResult, serverResult.result)

        val receivedRequest = mockWebServer.takeRequest()

        assertEquals("GET", receivedRequest.method)
        assertEquals("/user/test", receivedRequest.path)
        assertEquals("Bearer $token", receivedRequest.getHeader("Authorization"))
    }

    @Test
    fun testGetUser_unsuccessful() = runBlocking {
        val error = Error(1, "Invalid request")
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
            .setBody(Gson().toJson(error))
        mockWebServer.enqueue(mockResponse)

        val token = "abcd"
        val serverResult = caffShopApiInteractor.getUser(token, "test")

        assertNotNull(serverResult.error)
        assertNull(serverResult.result)
        assertEquals(ErrorMessage.USER_REQUEST_FAILED, serverResult.error)

        val receivedRequest = mockWebServer.takeRequest()

        assertEquals("GET", receivedRequest.method)
        assertEquals("/user/test", receivedRequest.path)
        assertEquals("Bearer $token", receivedRequest.getHeader("Authorization"))
    }

    @Test
    fun testDeleteUser_successful() = runBlocking {
        val expectedDeleteUserResult = true
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
        mockWebServer.enqueue(mockResponse)

        val token = "abcd"
        val serverResult = caffShopApiInteractor.deleteUser(token, "test")

        assertNull(serverResult.error)
        assertNotNull(serverResult.result)
        assertEquals(expectedDeleteUserResult, serverResult.result)

        val receivedRequest = mockWebServer.takeRequest()

        assertEquals("DELETE", receivedRequest.method)
        assertEquals("/user/test", receivedRequest.path)
        assertEquals("Bearer $token", receivedRequest.getHeader("Authorization"))
    }

    @Test
    fun testDeleteUser_unsuccessful() = runBlocking {
        val error = Error(1, "Invalid request")
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
            .setBody(Gson().toJson(error))
        mockWebServer.enqueue(mockResponse)

        val token = "abcd"
        val serverResult = caffShopApiInteractor.deleteUser(token, "test")

        assertNotNull(serverResult.error)
        assertNull(serverResult.result)
        assertEquals(ErrorMessage.USER_DELETE_FAILED, serverResult.error)

        val receivedRequest = mockWebServer.takeRequest()

        assertEquals("DELETE", receivedRequest.method)
        assertEquals("/user/test", receivedRequest.path)
        assertEquals("Bearer $token", receivedRequest.getHeader("Authorization"))
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

        val receivedRequest = mockWebServer.takeRequest()

        assertEquals("POST", receivedRequest.method)
        assertEquals("/user/register", receivedRequest.path)
        assertEquals(Gson().toJson(userCredentials), receivedRequest.body.readUtf8())
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

        val receivedRequest = mockWebServer.takeRequest()

        assertEquals("POST", receivedRequest.method)
        assertEquals("/user/register", receivedRequest.path)
        assertEquals(Gson().toJson(userCredentials), receivedRequest.body.readUtf8())
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

        val receivedRequest = mockWebServer.takeRequest()

        assertEquals("POST", receivedRequest.method)
        assertEquals("/user/login", receivedRequest.path)
        assertEquals(Gson().toJson(userCredentials), receivedRequest.body.readUtf8())
    }

    @Test
    fun testLogin_unsuccessful() = runBlocking {
        val error = Error(3, "Invalid username or password")
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
            .setBody(Gson().toJson(error))
        mockWebServer.enqueue(mockResponse)

        val userCredentials = UserCredentials("test", "test")
        val serverResult = caffShopApiInteractor.login(userCredentials)

        assertNotNull(serverResult.error)
        assertNull(serverResult.result)
        assertEquals(ErrorMessage.LOGIN_FAILED, serverResult.error)

        val receivedRequest = mockWebServer.takeRequest()

        assertEquals("POST", receivedRequest.method)
        assertEquals("/user/login", receivedRequest.path)
        assertEquals(Gson().toJson(userCredentials), receivedRequest.body.readUtf8())
    }

    @Test
    fun testLogout_successful() = runBlocking {
        val expectedLogoutResponse = true
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
        mockWebServer.enqueue(mockResponse)

        val token = "abcd"
        val serverResult = caffShopApiInteractor.logout(token)

        assertNull(serverResult.error)
        assertNotNull(serverResult.result)
        assertEquals(expectedLogoutResponse, serverResult.result)

        val receivedRequest = mockWebServer.takeRequest()

        assertEquals("POST", receivedRequest.method)
        assertEquals("/user/logout", receivedRequest.path)
        assertEquals("Bearer $token", receivedRequest.getHeader("Authorization"))
    }

    @Test
    fun testLogout_unsuccessful() = runBlocking {
        val error = Error(1, "Invalid request")
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
            .setBody(Gson().toJson(error))
        mockWebServer.enqueue(mockResponse)

        val token = "abcd"
        val serverResult = caffShopApiInteractor.logout(token)

        assertNotNull(serverResult.error)
        assertNull(serverResult.result)
        assertEquals(ErrorMessage.LOGOUT_FAILED, serverResult.error)

        val receivedRequest = mockWebServer.takeRequest()

        assertEquals("POST", receivedRequest.method)
        assertEquals("/user/logout", receivedRequest.path)
        assertEquals("Bearer $token", receivedRequest.getHeader("Authorization"))
    }

    @Test
    fun testGetCaff_successful() = runBlocking {
        val expectedGetCaffResponse = Caff(
            1, "caff", "creator", 1000,
            "creator", 1000, 1,
            CaffAnimationImage(
                1, 1, 1, "caption", emptyList(), emptyList()
            )
        )
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(Gson().toJson(expectedGetCaffResponse))
        mockWebServer.enqueue(mockResponse)

        val token = "abcd"
        val serverResult = caffShopApiInteractor.getCaff(token, 1)

        assertNull(serverResult.error)
        assertNotNull(serverResult.result)
        assertEquals(expectedGetCaffResponse, serverResult.result)

        val receivedRequest = mockWebServer.takeRequest()

        assertEquals("GET", receivedRequest.method)
        assertEquals("/caff/1", receivedRequest.path)
        assertEquals("Bearer $token", receivedRequest.getHeader("Authorization"))
    }

    @Test
    fun testGetCaff_unsuccessful() = runBlocking {
        val error = Error(1, "Invalid request")
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
            .setBody(Gson().toJson(error))
        mockWebServer.enqueue(mockResponse)

        val token = "abcd"
        val serverResult = caffShopApiInteractor.getCaff(token, 1)

        assertNotNull(serverResult.error)
        assertNull(serverResult.result)
        assertEquals(ErrorMessage.CAFF_REQUEST_FAILED, serverResult.error)

        val receivedRequest = mockWebServer.takeRequest()

        assertEquals("GET", receivedRequest.method)
        assertEquals("/caff/1", receivedRequest.path)
        assertEquals("Bearer $token", receivedRequest.getHeader("Authorization"))
    }

    @Test
    fun testDeleteCaff_successful() = runBlocking {
        val expectedDeleteCaffResponse = true
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(Gson().toJson(expectedDeleteCaffResponse))
        mockWebServer.enqueue(mockResponse)

        val token = "abcd"
        val serverResult = caffShopApiInteractor.deleteCaff(token, 1)

        assertNull(serverResult.error)
        assertNotNull(serverResult.result)
        assertEquals(expectedDeleteCaffResponse, serverResult.result)

        val receivedRequest = mockWebServer.takeRequest()

        assertEquals("DELETE", receivedRequest.method)
        assertEquals("/caff/1", receivedRequest.path)
        assertEquals("Bearer $token", receivedRequest.getHeader("Authorization"))
    }

    @Test
    fun testDeleteCaff_unsuccessful() = runBlocking {
        val error = Error(1, "Invalid request")
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
            .setBody(Gson().toJson(error))
        mockWebServer.enqueue(mockResponse)

        val token = "abcd"
        val serverResult = caffShopApiInteractor.deleteCaff(token, 1)

        assertNotNull(serverResult.error)
        assertNull(serverResult.result)
        assertEquals(ErrorMessage.CAFF_DELETE_FAILED, serverResult.error)

        val receivedRequest = mockWebServer.takeRequest()

        assertEquals("DELETE", receivedRequest.method)
        assertEquals("/caff/1", receivedRequest.path)
        assertEquals("Bearer $token", receivedRequest.getHeader("Authorization"))
    }

    @Test
    fun testSearchCaffs_successful() = runBlocking {
        val expectedSearchCaffResponse = CaffList(
            listOf(
                Caff(
                    1, "caff", "creator", 1000,
                    "creator", 1000, 1,
                    CaffAnimationImage(
                        1, 1, 1, "caption", emptyList(), emptyList()
                    )
                )
            ),
            1
        )
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(Gson().toJson(expectedSearchCaffResponse))
        mockWebServer.enqueue(mockResponse)

        val token = "abcd"
        val serverResult = caffShopApiInteractor.searchCaffs(token, "test")

        assertNull(serverResult.error)
        assertNotNull(serverResult.result)
        assertEquals(expectedSearchCaffResponse, serverResult.result)

        val receivedRequest = mockWebServer.takeRequest()

        assertEquals("GET", receivedRequest.method)
        assertEquals("/caff/search?searchTerm=test&page=1&perpage=20", receivedRequest.path)
        assertEquals("Bearer $token", receivedRequest.getHeader("Authorization"))
    }

    @Test
    fun testSearchCaffs_unsuccessful() = runBlocking {
        val error = Error(1, "Invalid request")
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
            .setBody(Gson().toJson(error))
        mockWebServer.enqueue(mockResponse)

        val token = "abcd"
        val serverResult = caffShopApiInteractor.searchCaffs(token, "test")

        assertNotNull(serverResult.error)
        assertNull(serverResult.result)
        assertEquals(ErrorMessage.CAFF_SEARCH_FAILED, serverResult.error)

        val receivedRequest = mockWebServer.takeRequest()

        assertEquals("GET", receivedRequest.method)
        assertEquals("/caff/search?searchTerm=test&page=1&perpage=20", receivedRequest.path)
        assertEquals("Bearer $token", receivedRequest.getHeader("Authorization"))
    }


    // TODO upload caff, download caff


    @Test
    fun testGetComments_successful() = runBlocking {
        val expectedGetCommentsResponse = CommentList(
            listOf(
                Comment(1, 1, "test", "test", 1000)
            ),
            1
        )
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(Gson().toJson(expectedGetCommentsResponse))
        mockWebServer.enqueue(mockResponse)

        val token = "abcd"
        val serverResult = caffShopApiInteractor.getComments(token, 1)

        assertNull(serverResult.error)
        assertNotNull(serverResult.result)
        assertEquals(expectedGetCommentsResponse, serverResult.result)

        val receivedRequest = mockWebServer.takeRequest()

        assertEquals("GET", receivedRequest.method)
        assertEquals("/comment?caffId=1&page=1&perpage=20", receivedRequest.path)
        assertEquals("Bearer $token", receivedRequest.getHeader("Authorization"))
    }

    @Test
    fun testGetComments_unsuccessful() = runBlocking {
        val error = Error(1, "Invalid request")
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
            .setBody(Gson().toJson(error))
        mockWebServer.enqueue(mockResponse)

        val token = "abcd"
        val serverResult = caffShopApiInteractor.getComments(token, 1)

        assertNotNull(serverResult.error)
        assertNull(serverResult.result)
        assertEquals(ErrorMessage.COMMENT_LIST_REQUEST_FAILED, serverResult.error)

        val receivedRequest = mockWebServer.takeRequest()

        assertEquals("GET", receivedRequest.method)
        assertEquals("/comment?caffId=1&page=1&perpage=20", receivedRequest.path)
        assertEquals("Bearer $token", receivedRequest.getHeader("Authorization"))
    }

    @Test
    fun testPostComment_successful() = runBlocking {
        val expectedPostCommentResponse = true
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_CREATED)
        mockWebServer.enqueue(mockResponse)

        val token = "abcd"
        val commentToCreate = CommentToCreate(1, "test")
        val serverResult = caffShopApiInteractor.postComment(token, commentToCreate)

        assertNull(serverResult.error)
        assertNotNull(serverResult.result)
        assertEquals(expectedPostCommentResponse, serverResult.result)

        val receivedRequest = mockWebServer.takeRequest()

        assertEquals("POST", receivedRequest.method)
        assertEquals("/comment", receivedRequest.path)
        assertEquals("Bearer $token", receivedRequest.getHeader("Authorization"))
        assertEquals(Gson().toJson(commentToCreate), receivedRequest.body.readUtf8())
    }

    @Test
    fun testPostComment_unsuccessful() = runBlocking {
        val error = Error(1, "Invalid request")
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
            .setBody(Gson().toJson(error))
        mockWebServer.enqueue(mockResponse)

        val token = "abcd"
        val commentToCreate = CommentToCreate(1, "test")
        val serverResult = caffShopApiInteractor.postComment(token, commentToCreate)

        assertNotNull(serverResult.error)
        assertNull(serverResult.result)
        assertEquals(ErrorMessage.COMMENT_POST_FAILED, serverResult.error)

        val receivedRequest = mockWebServer.takeRequest()

        assertEquals("POST", receivedRequest.method)
        assertEquals("/comment", receivedRequest.path)
        assertEquals("Bearer $token", receivedRequest.getHeader("Authorization"))
        assertEquals(Gson().toJson(commentToCreate), receivedRequest.body.readUtf8())
    }

    @Test
    fun testDeleteComment_successful() = runBlocking {
        val expectedDeleteCommentResponse = true
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
        mockWebServer.enqueue(mockResponse)

        val token = "abcd"
        val serverResult = caffShopApiInteractor.deleteComment(token, 1)

        assertNull(serverResult.error)
        assertNotNull(serverResult.result)
        assertEquals(expectedDeleteCommentResponse, serverResult.result)

        val receivedRequest = mockWebServer.takeRequest()

        assertEquals("DELETE", receivedRequest.method)
        assertEquals("/comment/1", receivedRequest.path)
        assertEquals("Bearer $token", receivedRequest.getHeader("Authorization"))
    }

    @Test
    fun testDeleteComment_unsuccessful() = runBlocking {
        val error = Error(1, "Invalid request")
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
            .setBody(Gson().toJson(error))
        mockWebServer.enqueue(mockResponse)

        val token = "abcd"
        val serverResult = caffShopApiInteractor.deleteComment(token, 1)

        assertNotNull(serverResult.error)
        assertNull(serverResult.result)
        assertEquals(ErrorMessage.COMMENT_DELETE_FAILED, serverResult.error)

        val receivedRequest = mockWebServer.takeRequest()

        assertEquals("DELETE", receivedRequest.method)
        assertEquals("/comment/1", receivedRequest.path)
        assertEquals("Bearer $token", receivedRequest.getHeader("Authorization"))
    }
}