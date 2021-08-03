package asd

import com.intellij.openapi.command.impl.DummyProject
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.testFramework.LightProjectDescriptor
import com.intellij.testFramework.PlatformTestUtil
import com.intellij.testFramework.UsefulTestCase
import com.intellij.testFramework.fixtures.*
import com.intellij.testFramework.fixtures.impl.LightTempDirTestFixtureImpl
import eu.ibagroup.formainframe.analytics.AnalyticsService
import eu.ibagroup.formainframe.analytics.AnalyticsStartupActivity
import io.mockk.InternalPlatformDsl.toStr
import io.mockk.every
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.spyk

abstract class PluginTestCase : BasePlatformTestCase() {

    override fun setUp() {
        mockkConstructor(AnalyticsStartupActivity::class)
        every { AnalyticsStartupActivity().runActivity(any()) } returns Unit

        super.setUp()

        val analyticsService = service<AnalyticsService>()
        analyticsService.isAnalyticsEnabled = true
        analyticsService.isUserAcknowledged = true
    }

    override fun getBasePath() = "/testData/"

    override fun getTestDataPath() = System.getProperty("user.dir") + basePath
}