package settings.connection

import com.intellij.openapi.components.service
import com.intellij.testFramework.UsefulTestCase
import customTestCase.PluginTestCase
import eu.ibagroup.formainframe.config.configCrudable
import eu.ibagroup.formainframe.config.connect.ConnectionConfig
import eu.ibagroup.formainframe.config.connect.Credentials
import eu.ibagroup.formainframe.config.connect.ui.ConnectionConfigurable
import eu.ibagroup.formainframe.config.connect.ui.ConnectionDialogState
import eu.ibagroup.formainframe.config.connect.ui.ConnectionsTableModel
import eu.ibagroup.formainframe.config.sandboxCrudable
import eu.ibagroup.formainframe.dataops.DataOpsManager
import eu.ibagroup.formainframe.dataops.exceptions.CallException
import eu.ibagroup.formainframe.dataops.operations.InfoOperation
import eu.ibagroup.formainframe.utils.crudable.getAll
import eu.ibagroup.formainframe.utils.crudable.getByForeignKey
import junit.framework.TestCase
import org.junit.jupiter.api.Assertions.assertNotEquals
import java.net.UnknownHostException
import javax.net.ssl.SSLPeerUnverifiedException
import kotlin.streams.toList

/**
 * Testnig the connection manager on API level.
 */
class ConnectionManagerTest: PluginTestCase() {

    private lateinit var conTab: ConnectionsTableModel
    private val conConfig = ConnectionConfigurable()

    /**
     * For now we have only worked with real connection. Please fill in your own.
     */
    private val conState = ConnectionDialogState(
        connectionName = "testConnection",
        connectionUrl = "https://zzow03.zowe.marist.cloud:10443/",
        username = "MENTEE1",
        password = "--------",
        isAllowSsl = true)
    private val conStateA = ConnectionDialogState(connectionName = "a", connectionUrl = "https://a.com", username = "a", password = "a")
    private val conStateB = ConnectionDialogState(connectionName = "b", connectionUrl = "https://b.com", username = "b", password = "b")

    override fun setUp() {
        super.setUp()
        conTab = ConnectionsTableModel(sandboxCrudable)
    }

    /**
     * There is a need to eliminate all rows in the ConnectionTableModel after each test
     */
    override fun tearDown() {
        for (item in conTab.fetch(sandboxCrudable)) {
            conTab.onDelete(sandboxCrudable,item)
        }
        super.tearDown()
    }

    /**
     * The function checking whether the sandboxCrudable is modified accrodingly
     */
    fun assertCrudable(connectionDialogStateList: List<ConnectionDialogState>) {
        var conConfigSet = emptySet<ConnectionConfig>()
        var creSet = emptyList<Credentials>()
        for (connectionDialogState in connectionDialogStateList) {
            conConfigSet += connectionDialogState.connectionConfig
            creSet += connectionDialogState.credentials
        }
        TestCase.assertEquals(conConfigSet.toList(),sandboxCrudable.getAll<ConnectionConfig>().toList())
        TestCase.assertEquals(creSet,sandboxCrudable.getAll<Credentials>().toList())
    }

    /**
     * testing the onAdd method of ConnectionTableModel on API level,
     * meaning checking whether the sandboxCrudable is modified accrodingly
     */
    fun testOnAdd() {
        conTab.onAdd(sandboxCrudable, conStateA)
        conTab.onAdd(sandboxCrudable, conStateB)
        conConfig.apply()
        UsefulTestCase.assertEquals(
            mutableListOf(conStateA, conStateB),
            conTab.fetch(sandboxCrudable)
        )
        assertCrudable(listOf(conStateA, conStateB))
        conTab.onDelete(sandboxCrudable,conStateA)
        conTab.onDelete(sandboxCrudable,conStateB)
        assertCrudable(listOf())
    }

    /**
     * Tests what happens to the sandboxCrudable if two connections with the same name are added.
     */
    fun testOnAddExistingName() {
        val connectionDialogState = ConnectionDialogState(connectionName = conStateA.connectionName)
        conTab.onAdd(sandboxCrudable, conStateA)
        conTab.onAdd(sandboxCrudable, connectionDialogState)
        conConfig.apply()
        UsefulTestCase.assertEquals(mutableListOf(conStateA), conTab.fetch(sandboxCrudable))
        assertCrudable(listOf(conStateA))
        conTab.onDelete(sandboxCrudable,conStateA)
        assertCrudable(listOf())
    }

    /**
     * Tests what happens to the sandboxCrudable if two connections with the same url are added.
     */
    fun testOnAddExistingUrl() {
        val connectionDialogState = ConnectionDialogState(connectionUrl = conStateA.connectionUrl)
        conTab.onAdd(sandboxCrudable, conStateA)
        conTab.onAdd(sandboxCrudable, connectionDialogState)
        conConfig.apply()
        UsefulTestCase.assertEquals(
            mutableListOf(conStateA, connectionDialogState),
            conTab.fetch(sandboxCrudable)
        )
        assertCrudable(listOf(conStateA, connectionDialogState))
        conTab.onDelete(sandboxCrudable,conStateA)
        conTab.onDelete(sandboxCrudable,connectionDialogState)
        assertCrudable(listOf())
    }


}