package eu.ibagroup.formainframe.explorer.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.progress.util.ProgressIndicatorBase
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import eu.ibagroup.formainframe.config.configCrudable
import eu.ibagroup.formainframe.config.connect.*
import eu.ibagroup.formainframe.config.connect.ui.ConnectionDialogState
import eu.ibagroup.formainframe.config.connect.ui.ConnectionsTableModel
import eu.ibagroup.formainframe.config.sandboxCrudable
import eu.ibagroup.formainframe.config.ws.WorkingSetConfig
import eu.ibagroup.formainframe.config.ws.ui.WSTableModel
import eu.ibagroup.formainframe.config.ws.ui.WorkingSetDialog
import eu.ibagroup.formainframe.config.ws.ui.WorkingSetDialogState
import eu.ibagroup.formainframe.config.ws.ui.initEmptyUuids
import eu.ibagroup.formainframe.dataops.operations.DatasetAllocationOperation
import eu.ibagroup.formainframe.dataops.operations.DatasetAllocationParams
import eu.ibagroup.formainframe.dataops.operations.DatasetAllocator
import eu.ibagroup.formainframe.explorer.GlobalExplorer
import eu.ibagroup.formainframe.explorer.GlobalWorkingSet
import eu.ibagroup.formainframe.explorer.WorkingSet
import eu.ibagroup.formainframe.explorer.ui.*
import eu.ibagroup.formainframe.utils.crudable.getByForeignKey
import eu.ibagroup.formainframe.vfs.MFVirtualFile
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import java.util.stream.Stream
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class fixtureTest: BasePlatformTestCase() {

    override fun getBasePath() = "/testData"

    override fun getTestDataPath() = "/home/malimi/OMP/git/For-Mainframe" + getBasePath()

    fun test1() {
        // add connection
        val state = ConnectionDialogState(connectionName = "marist", connectionUrl = "https://zzow03.zowe.marist.cloud:10443/",
            username = "MENTEE1", password = "ZGFmzJeQ")
        state.urlConnection.isAllowSelfSigned = true
        state.isAllowSsl = true
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
        param.allocationParameters.primaryAllocation = 1
        param.allocationParameters.secondaryAllocation = 1
        param.allocationParameters.recordLength = 1
        param.allocationParameters.blockSize = 1
        val op = DatasetAllocationOperation(param, state.connectionConfig, state.urlConnection)
        DatasetAllocator().run(op, pr)
        println(configCrudable
            .getByForeignKey<ConnectionConfig, UrlConnection>(state.connectionConfig))
        println(username(state.connectionConfig))
        println(password(state.connectionConfig))
    }

    fun testAnAction() {
        // add connection
        val state = ConnectionDialogState(connectionName = "marist", connectionUrl = "https://zzow03.zowe.marist.cloud:10443/",
            username = "MENTEE1", password = "ZGFmzJeQ")
        state.urlConnection.isAllowSelfSigned = true
        state.isAllowSsl = true
        val conTab = ConnectionsTableModel(sandboxCrudable)
        conTab.onAdd(configCrudable,state)
        CredentialService.instance.setCredentials(state.connectionConfig.uuid,state.username,state.password)

        val e = mockk<AnActionEvent>()
        every { e.project } returns myFixture.project
        val action = AddWorkingSetAction()
        action.actionPerformed(e)

//        val testView = mockk<GlobalFileExplorerView>()
//        every { e.getData(FILE_EXPLORER_VIEW) } returns testView
        //every { testView.mySelectedNodesData[0].node } returns

    }

    fun testTest() {

    }

}


//val allocateAction = AllocateDatasetAction()
//myFixture.testAction(allocateAction)
//val e = AnActionEvent(null, DataManager.getInstance().getDataContext(), "File Explorer", Presentation(), ActionManager.getInstance(), 16)