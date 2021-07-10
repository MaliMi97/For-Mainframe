package eu.ibagroup.formainframe.explorer.actions

import com.intellij.icons.AllIcons
import com.intellij.mock.MockApplication
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.util.Disposer
import com.intellij.util.containers.isEmpty
import eu.ibagroup.formainframe.config.ConfigService
import eu.ibagroup.formainframe.config.ConfigServiceImpl
import eu.ibagroup.formainframe.config.configCrudable
import eu.ibagroup.formainframe.config.connect.ConnectionConfig
import eu.ibagroup.formainframe.config.connect.ui.ConnectionDialog
import eu.ibagroup.formainframe.config.connect.ui.ConnectionDialogState
import eu.ibagroup.formainframe.config.connect.ui.initEmptyUuids
import eu.ibagroup.formainframe.config.ws.WorkingSetConfig
import eu.ibagroup.formainframe.config.ws.ui.WorkingSetDialog
import eu.ibagroup.formainframe.config.ws.ui.WorkingSetDialogState
import eu.ibagroup.formainframe.config.ws.ui.initEmptyUuids
import eu.ibagroup.formainframe.explorer.ui.FILE_EXPLORER_VIEW
import eu.ibagroup.formainframe.explorer.ui.GlobalFileExplorerView
import eu.ibagroup.formainframe.utils.crudable.getAll
import eu.ibagroup.formainframe.utils.crudable.nextUniqueValue
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.awt.EventQueue
import java.util.stream.Stream

internal class AddWorkingSetActionTest{

    val app = spyk(MockApplication(Disposer.newDisposable("")))
    val action = AddWorkingSetAction()
    val e = mockk<AnActionEvent>()

    @Test
    fun testActionPerformedNoConnectionNull() {
        ApplicationManager.setApplication(app, Disposer.newDisposable(""))
        val impl = ConfigServiceImpl()
        every { app.getService(ConfigService::class.java) } returns impl
        mockkStatic("eu.ibagroup.formainframe.utils.crudable.CrudableKt")
        every { configCrudable.getAll<ConnectionConfig>() } returns Stream.empty()
        mockkStatic("eu.ibagroup.formainframe.config.connect.ui.ConnectionDialogStateKt")
        every {ConnectionDialogState().initEmptyUuids(configCrudable) } returns
                ConnectionDialogState().initEmptyUuids(impl.crudable)
        mockkObject(ConnectionDialog)
        every { ConnectionDialog.showAndTestConnection(
            crudable = configCrudable,
            project = e.project,
            initialState = ConnectionDialogState().initEmptyUuids(configCrudable)
        ) } returns null
        action.actionPerformed(e)
        assertTrue(impl.crudable.getAll<WorkingSetConfig>().isEmpty())
    }

    @Test
    fun testActionPerformedNoConnection() {

    }

    // StackOverflow Error
    @Test
    fun testActionPerformedAlreadyHaveConnection() {
        ApplicationManager.setApplication(app, Disposer.newDisposable(""))
        val impl = ConfigServiceImpl()
        val conFig = ConnectionConfig(name = "a", uuid = "aa", urlConnectionUuid = "aaa")
        impl.crudable.add(conFig)
        mockkStatic("eu.ibagroup.formainframe.utils.crudable.CrudableKt")
        every { configCrudable.getAll<ConnectionConfig>() } returns impl.crudable.getAll<ConnectionConfig>()
        every { configCrudable.nextUniqueValue<WorkingSetConfig, String>() } returns
                impl.crudable.nextUniqueValue<WorkingSetConfig, String>()
        action.actionPerformed(e)

    }

    @Test
    fun testIsDumbAware() {
        assertTrue(action.isDumbAware)
    }

    @Test
    fun testUpdateNull() {
        val presentation = Presentation()
        every { e.presentation } returns presentation
        every { e.getData(FILE_EXPLORER_VIEW) } returns null
        action.update(e)
        verify { e.getData(FILE_EXPLORER_VIEW) }
        assertEquals(AllIcons.General.Add, presentation.icon)
        assertEquals("Create Working Set", presentation.text)
    }

    @Test
    fun testUpdateNotNull() {
        val presentation = Presentation()
        val notNull = mockk<GlobalFileExplorerView>()
        every { e.presentation } returns presentation
        every { e.getData(FILE_EXPLORER_VIEW) } returns notNull
        action.update(e)
        verify { e.getData(FILE_EXPLORER_VIEW) }
        assertEquals(AllIcons.Nodes.Project, presentation.icon)
        assertEquals("Working Set", presentation.text)
    }

}