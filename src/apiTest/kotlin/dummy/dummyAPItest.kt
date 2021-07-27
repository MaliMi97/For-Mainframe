package dummy

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import junit.framework.TestCase
import org.mockserver.client.MockServerClient
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.mockserver.socket.PortFactory
import org.mockserver.verify.VerificationTimes
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class dummyAPItest: BasePlatformTestCase() {

    override fun getBasePath() = "/src/apiTest/resources/testData/"

    override fun getTestDataPath() = System.getProperty("user.dir") + getBasePath()

    fun testWithoutMockServer() {
        myFixture.copyFileToProject("dummyAPI.txt")
        myFixture.checkResultByFile("dummyAPI.txt","dummyAPI.txt",true)
    }

    fun callServer(uri: String): String {
        val client = HttpClient.newHttpClient()
        val request = HttpRequest.newBuilder()
            .uri(URI.create(uri))
            .GET()
            .build()
        return client.send(request, HttpResponse.BodyHandlers.ofString()).body()
    }

    fun testWithMockServerInShell() {
        val port = 8080
        val host = "127.0.0.1"
        val path = "/hello/world"
        MockServerClient(host, port).`when`(
            request().withMethod("GET").withPath(path)
        ).respond(
            response().withBody("Hello World!")
        )
        var received: String
        try {
            received = callServer("http://$host:$port$path")
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
        MockServerClient(host, port).verify(
            request().withPath(path),
            VerificationTimes.atLeast(1)
        )
        TestCase.assertEquals(received, "Hello World!")
    }

    fun testWithMockServerInTest() {
        val port = PortFactory.findFreePort()
        val host = "127.0.0.1"
        val path = "/hello/world"
        val mockMainframe = ClientAndServer.startClientAndServer(port)
        MockServerClient(host, port).`when`(
            request().withMethod("GET").withPath(path)
        ).respond(
            response().withBody("Hello World!")
        )
        var received: String
        try {
            received = callServer("http://$host:$port$path")
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
        MockServerClient(host, port).verify(
            request().withPath(path),
            VerificationTimes.atLeast(1)
        )
        TestCase.assertEquals(received, "Hello World!")
        mockMainframe.stop()
    }

}