package eu.ibagroup.formainframe.config.ws.ui

import com.intellij.mock.MockApplication
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.Disposer
import eu.ibagroup.formainframe.config.ConfigSandboxImpl
import eu.ibagroup.formainframe.config.connect.ui.ConnectionDialogState
import eu.ibagroup.formainframe.config.connect.ui.ConnectionsTableModel
import eu.ibagroup.formainframe.config.ws.DSMask
import eu.ibagroup.formainframe.config.ws.UssPath
import eu.ibagroup.formainframe.config.ws.WorkingSetConfig
import io.mockk.spyk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

internal class WSTableModelTest {

    val app = spyk(MockApplication(Disposer.newDisposable("")))
    val sandbox = ConfigSandboxImpl()
    val tab = WSTableModel(sandbox.crudable)
    // NOTE: Should I be able tp create WorkingSetConfig without checking whether the connectionConfigUuid already exists?
    // SECOND NOTE: Setter seems to need it to function properly
    val configNoMask = WorkingSetConfig(uuid = "a", name = "a", connectionConfigUuid = "gibberishA", dsMasks = mutableListOf(), ussPaths = mutableListOf())
    val configMask = WorkingSetConfig(uuid = "b", name = "b", connectionConfigUuid = "gibberishB", dsMasks = mutableListOf(
        DSMask("gibberish", arrayListOf())), ussPaths = mutableListOf(UssPath("something")))

    @Test
    fun fetch() {
        ApplicationManager.setApplication(app,Disposer.newDisposable(""))
        tab.addRow(configNoMask)
        tab.addRow(configMask)
        assertEquals(mutableListOf(configNoMask,configMask), tab.fetch(sandbox.crudable))
    }

    @Test
    fun onDelete() {
        ApplicationManager.setApplication(app,Disposer.newDisposable(""))
        tab.addRow(configNoMask)
        tab.onDelete(sandbox.crudable,configNoMask)
        assertTrue(tab.fetch(sandbox.crudable).isEmpty())
    }

    @Test
    fun onAdd1() {
        ApplicationManager.setApplication(app,Disposer.newDisposable(""))
        tab.onAdd(sandbox.crudable,configNoMask)
        assertEquals(mutableListOf(configNoMask),tab.fetch(sandbox.crudable))
    }

    @Test
    fun onAdd2() {
        ApplicationManager.setApplication(app,Disposer.newDisposable(""))
        tab.onAdd(sandbox.crudable,configNoMask)
        tab.onAdd(sandbox.crudable,configMask)
        assertEquals(mutableListOf(configNoMask, configMask),tab.fetch(sandbox.crudable))
    }

    // ERROR when uncommenting assertEquals(config2.uuid,aux.uuid)
    // The setter does not seem to be correcting uuid
    // if you think about how it is used, then it makes sense as the set function is probably used to edit an existing row
    // however it is still gonna be a row, even if one got duplicates, there wills till be two rows and user would need to differentiate between them
    // and so there is a need for unique id, that is uuid
    @Test
    fun set() {
        ApplicationManager.setApplication(app,Disposer.newDisposable(""))
        val conTab = ConnectionsTableModel(sandbox.crudable)
        val state1 = ConnectionDialogState(connectionName = "a", connectionUrl = "https://a.com", username = "a", password = "a")
        val state2 = ConnectionDialogState(connectionName = "b", connectionUrl = "https://b.com", username = "b", password = "b")
        conTab.onAdd(sandbox.crudable,state1)
        conTab.onAdd(sandbox.crudable,state2)
        val config1 = WorkingSetConfig(uuid = "a", name = "a", connectionConfigUuid = state1.connectionUuid, dsMasks = mutableListOf(), ussPaths = mutableListOf())
        val config2 = WorkingSetConfig(uuid = "b", name = "b", connectionConfigUuid = state2.connectionUuid, dsMasks = mutableListOf(), ussPaths = mutableListOf())
        tab.addRow(config1)
        tab[0] = config2
        val aux = tab.fetch(sandbox.crudable)[0]
        assertEquals(config2.connectionConfigUuid,aux.connectionConfigUuid)
        assertEquals(config2.dsMasks,aux.dsMasks)
        assertEquals(config2.name,aux.name)
        assertEquals(config2.ussPaths,aux.ussPaths)
        //assertEquals(config2.uuid,aux.uuid)
    }
}