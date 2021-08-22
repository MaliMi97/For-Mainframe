package eu.ibagroup.formainframe.config

import com.intellij.mock.MockApplication
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.Disposer
import com.intellij.testFramework.UsefulTestCase
import eu.ibagroup.formainframe.config.connect.ui.ConnectionDialogState
import eu.ibagroup.formainframe.config.connect.ui.ConnectionsTableModel
import eu.ibagroup.formainframe.config.ws.DSMask
import eu.ibagroup.formainframe.config.ws.UssPath
import eu.ibagroup.formainframe.config.ws.WorkingSetConfig
import eu.ibagroup.formainframe.config.ws.ui.WSTableModel
import io.mockk.spyk
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach

/**
 * A test case for unit tests, which only depend on the existence of an application.
 */
open class UnitTestCase {
    /**
     * spying on MockApplication
     */
    val app = spyk(MockApplication(Disposer.newDisposable("")))

    /**
     * Setting up MockApplication
     */
    @BeforeEach
    fun setUp() {
        ApplicationManager.setApplication(app,Disposer.newDisposable(""))
    }

    /**
     * Tearing down MockApplication
     */
    @AfterEach
    fun tearDown() {
        app.dispose()
    }
}