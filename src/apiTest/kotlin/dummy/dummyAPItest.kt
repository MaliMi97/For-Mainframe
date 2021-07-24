package dummy

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class dummyAPItest: BasePlatformTestCase() {

    override fun getBasePath() = "/src/apiTest/resources/testData/"

    override fun getTestDataPath() = System.getProperty("user.dir") + getBasePath()

    fun testWithoutMockServer() {
        myFixture.copyFileToProject("dummyAPI.txt")
        myFixture.checkResultByFile("dummyAPI.txt","dummyAPI.txt",true)

    }

    fun testWithMockServer() {

    }

}