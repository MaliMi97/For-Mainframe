package connection

import com.intellij.openapi.components.service
import customTestCase.PluginTestCase
import eu.ibagroup.formainframe.config.configCrudable
import eu.ibagroup.formainframe.config.connect.ConnectionConfig
import eu.ibagroup.formainframe.config.connect.Credentials
import eu.ibagroup.formainframe.config.connect.UrlConnection
import eu.ibagroup.formainframe.config.connect.authToken
import eu.ibagroup.formainframe.config.connect.ui.ConnectionDialogState
import eu.ibagroup.formainframe.config.connect.ui.ConnectionsTableModel
import eu.ibagroup.formainframe.config.sandboxCrudable
import eu.ibagroup.formainframe.dataops.DataOpsManager
import eu.ibagroup.formainframe.dataops.operations.DatasetAllocationOperation
import eu.ibagroup.formainframe.dataops.operations.DatasetAllocationParams
import eu.ibagroup.formainframe.dataops.operations.DatasetAllocator
import eu.ibagroup.formainframe.dataops.operations.InfoOperation
import eu.ibagroup.formainframe.utils.crudable.getAll
import eu.ibagroup.formainframe.utils.toMutableList
import eu.ibagroup.r2z.CreateDataset
import eu.ibagroup.r2z.DatasetOrganization
import eu.ibagroup.r2z.RecordFormat
import io.mockk.every
import io.mockk.mockkStatic
import junit.framework.TestCase

class connectionTest: PluginTestCase() {

    private val conState = ConnectionDialogState(
        connectionName = "testConnection",
        connectionUrl = "https://zzow03.zowe.marist.cloud:10443/",
        username = "MENTEE1",
        password = "OdORaxxC",
        isAllowSsl = true)
    lateinit var conTab: ConnectionsTableModel

    override fun setUp() {
        super.setUp()
        conTab = ConnectionsTableModel(sandboxCrudable)
        conTab.onAdd(configCrudable, conState)
    }

    fun testConnected() {
        val throwable =
            try {
                service<DataOpsManager>().performOperation(InfoOperation(conState.urlConnection.url, conState.isAllowSsl))
                null
            } catch (t: Throwable) {
                t
            }
        TestCase.assertNull(throwable)
    }

    fun testNotSelfSigned() {
        val throwable =
            try {
                service<DataOpsManager>().performOperation(InfoOperation(conState.urlConnection.url, false))
                null
            } catch (t: Throwable) {
                t
            }
        TestCase.assertNotNull(throwable)
    }

    fun testNotConnected() {
        val throwable =
            try {
                service<DataOpsManager>().performOperation(InfoOperation("https://a.com", true))
                null
            } catch (t: Throwable) {
                t
            }
        TestCase.assertNotNull(throwable)
    }

    fun testConfigCrudable() {
        TestCase.assertEquals(configCrudable.getAll<ConnectionConfig>().toMutableList()[0],conState.connectionConfig)
        TestCase.assertEquals(configCrudable.getAll<UrlConnection>().toMutableList()[0],conState.urlConnection)
        // for some reason there is an index out of bound error
        TestCase.assertEquals(configCrudable.getAll<Credentials>().toMutableList()[0],conState.credentials)
    }

    fun testDatasetAllocation() {
        // This should not be needed!!!!!!!!!
        mockkStatic("eu.ibagroup.formainframe.config.connect.CredentialServiceKt")
        every { any<ConnectionConfig>().authToken } returns
                okhttp3.Credentials.basic(conState.username, conState.password)



        val config = configCrudable.getAll<ConnectionConfig>().toMutableList()[0]
        val url = configCrudable.getAll<UrlConnection>().toMutableList()[0]
        val name = "TESTB"
        val param = DatasetAllocationParams(name, CreateDataset(
            primaryAllocation = 1,
            secondaryAllocation = 1,
            recordFormat = RecordFormat.FB,
            datasetOrganization = DatasetOrganization.PS))
        val dataOp = DatasetAllocationOperation(param, config, url)
        DatasetAllocator().run(dataOp)
    }

    fun testNullEditor() {
        TestCase.assertNotNull(myFixture.editor)
    }
}