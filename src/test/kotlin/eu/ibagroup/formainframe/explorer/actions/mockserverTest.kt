package eu.ibagroup.formainframe.explorer.actions

import com.intellij.openapi.progress.util.ProgressIndicatorBase
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import eu.ibagroup.formainframe.config.*
import eu.ibagroup.formainframe.config.connect.*
import eu.ibagroup.formainframe.config.connect.ui.ConnectionDialogState
import eu.ibagroup.formainframe.config.connect.ui.ConnectionsTableModel
import eu.ibagroup.formainframe.config.ws.WorkingSetConfig
import eu.ibagroup.formainframe.config.ws.ui.WSTableModel
import eu.ibagroup.formainframe.dataops.operations.DatasetAllocationOperation
import eu.ibagroup.formainframe.dataops.operations.DatasetAllocationParams
import eu.ibagroup.formainframe.dataops.operations.DatasetAllocator
import eu.ibagroup.formainframe.utils.crudable.getByForeignKey
import org.mockserver.client.MockServerClient
import org.mockserver.integration.ClientAndServer
import org.mockserver.logging.MockServerLogger
import org.mockserver.model.Format
import org.mockserver.model.HttpRequest.request
import org.mockserver.socket.PortFactory
import org.mockserver.socket.tls.KeyStoreFactory
import java.io.File
import javax.net.ssl.HttpsURLConnection


class mockserverTest: BasePlatformTestCase()  {

    private val port = PortFactory.findFreePort()
    private val host = "https://localhost:$port/"
    private val mockMainframe = ClientAndServer.startClientAndServer(port)
    lateinit var recordedRequests: String

    override fun setUp() {
        super.setUp()
        // ssl verification
        HttpsURLConnection.setDefaultSSLSocketFactory(KeyStoreFactory(MockServerLogger()).sslContext().socketFactory)
        // recorded requests - for debugging
        recordedRequests = MockServerClient("localhost", port)
            .retrieveRecordedRequests(
                request(),
                Format.JSON
            )
    }

    override fun tearDown() {
        // write recorded requests - for debugging
        File("/home/malimi/OMP.json").writeText(recordedRequests)
        // tearDown
        super.tearDown()
        mockMainframe.stop()
    }

    fun testAllocateDataset() {
        // add connection
        val state = ConnectionDialogState(connectionName = "test", connectionUrl = host,
            username = "user", password = "pass")
        state.urlConnection.isAllowSelfSigned = true
        val conTab = ConnectionsTableModel(sandboxCrudable)
        conTab.onAdd(configCrudable,state)
        // set credentials
        CredentialService.instance.setCredentials(state.connectionConfig.uuid,state.username,state.password)
        // add work set to the connection
        val config = WorkingSetConfig(uuid = "a", name = "a", connectionConfigUuid = state.connectionUuid,
            dsMasks = mutableListOf(), ussPaths = mutableListOf())
        val wsTab = WSTableModel(sandboxCrudable)
        wsTab.onAdd(configCrudable,config)
        // allocate dataset
        val pr = ProgressIndicatorBase()
        val param = DatasetAllocationParams(datasetName = "d")
        val op = DatasetAllocationOperation(param, state.connectionConfig, state.urlConnection)
        DatasetAllocator().run(op, pr)
        println(configCrudable
            .getByForeignKey<ConnectionConfig, UrlConnection>(state.connectionConfig))
        println(username(state.connectionConfig))
        println(password(state.connectionConfig))

    }
}