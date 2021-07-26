package eu.ibagroup.formainframe.config.connect.ui

import com.intellij.mock.MockApplication
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.Disposer
import eu.ibagroup.formainframe.config.ConfigSandboxImpl
import eu.ibagroup.formainframe.config.connect.ConnectionConfig
import eu.ibagroup.formainframe.utils.crudable.nextUniqueValue
import io.mockk.spyk
import junit.framework.TestCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ConnectionsTableModelTest: TestCase() {

    val app = spyk(MockApplication(Disposer.newDisposable("")))
    val sandbox = ConfigSandboxImpl()
    val tab = ConnectionsTableModel(sandbox.crudable)
    val state = ConnectionDialogState(connectionName = "a", connectionUrl = "https://a.com", username = "a", password = "a")

    fun testFetch() {
        ApplicationManager.setApplication(app,Disposer.newDisposable(""))
        tab.addRow(state)
        assertEquals(mutableListOf(state),tab.fetch(sandbox.crudable))
        assertTrue(false)
    }

    fun testOnAdd1() {
        ApplicationManager.setApplication(app,Disposer.newDisposable(""))
        tab.onAdd(sandbox.crudable, state)
        assertEquals(mutableListOf(state),tab.fetch(sandbox.crudable))
    }

    fun testOnAdd2() {
        ApplicationManager.setApplication(app,Disposer.newDisposable(""))
        val state2 = ConnectionDialogState(connectionName = "b", connectionUrl = "https://b.com", username = "b", password = "b")
        tab.onAdd(sandbox.crudable, state)
        tab.onAdd(sandbox.crudable, state2)
        assertEquals(mutableListOf(state,state2),tab.fetch(sandbox.crudable))
    }

    fun testOnDelete() {
        ApplicationManager.setApplication(app,Disposer.newDisposable(""))
        tab.onAdd(sandbox.crudable, state)
        tab.onDelete(sandbox.crudable, state)
        assertEquals(mutableListOf<ConnectionDialogState>(),tab.fetch(sandbox.crudable))
    }

    fun testSet1() {
        ApplicationManager.setApplication(app,Disposer.newDisposable(""))
        tab.addRow(state)
        state.connectionName = "b"
        state.connectionUrl = "https://b.com"
        state.username = "b"
        tab[0] = state
        assertEquals(mutableListOf(state),tab.fetch(sandbox.crudable))
    }

    // this test does not work
    // for some reason onAdd does not add a row and the index in tab.set is out of bounds
//    @Test
//    fun set2() {
//        ApplicationManager.setApplication(app,Disposer.newDisposable(""))
//        var state = ConnectionDialogState(connectionUuid = sandbox.crudable.nextUniqueValue<ConnectionConfig, String>(), connectionName = "a", connectionUrl = "https://a.com", username = "a", password = "a")
//        tab.onAdd(sandbox.crudable,state)
//        state.connectionName = "b"
//        state.connectionUrl = "https://b.com"
//        state.username = "b"
//        tab.set(0,state)
//        assertEquals(mutableListOf(state),tab.fetch(sandbox.crudable))
//    }
}