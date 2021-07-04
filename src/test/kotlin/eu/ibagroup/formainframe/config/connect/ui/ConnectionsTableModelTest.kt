package eu.ibagroup.formainframe.config.connect.ui

import eu.ibagroup.formainframe.config.ConfigSandboxImpl
import eu.ibagroup.formainframe.config.connect.ConnectionConfig
import eu.ibagroup.formainframe.config.sandboxCrudable
import eu.ibagroup.formainframe.utils.crudable.CrudableLists
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class ConnectionsTableModelTest {

    // This test is fine
    @Test
    fun onAdd1() {
        val sandbox = ConfigSandboxImpl()
        val tab = ConnectionsTableModel(sandbox.crudable)
        assertEquals(mutableListOf<ConnectionConfig>(),tab.fetch(sandbox.crudable))
    }

    // This test gives an error ApplicationManager.getApplication() must not be null
    // I am not sure why I am getting this error
    @Test
    fun onAdd2() {
        val sandbox = ConfigSandboxImpl()
        val tab = ConnectionsTableModel(sandbox.crudable)
        val state = ConnectionDialogState("a","a","a","https://a.com","a","a")
        tab.onAdd(sandbox.crudable, state)
        assertEquals(mutableListOf(ConnectionConfig("a","a","https://a.com")),tab.fetch(sandbox.crudable))
    }
}