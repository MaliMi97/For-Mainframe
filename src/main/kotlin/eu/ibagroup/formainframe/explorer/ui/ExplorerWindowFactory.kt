package eu.ibagroup.formainframe.explorer.ui

import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

/**
 * factory class for ToolWindow extension
 * creates the For Mainframe explorer tool window
 */
class ExplorerWindowFactory : ToolWindowFactory, DumbAware {

  /**
   * needs to return true for the explorer tool window to function
   */
  override fun isApplicable(project: Project): Boolean {
    return true
  }

  /**
   * creates the content of the For Mainframe explorer tool window
   */
  override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
    val contentFactory = ContentFactory.SERVICE.getInstance()
    val factory = service<ExplorerContent>()
    val content = contentFactory
      .createContent(factory.buildComponent(toolWindow.disposable, project), factory.displayName, factory.isLockable)
    toolWindow.contentManager.addContent(content)
  }

  /**
   * NOT USED FUNCTION
   */
  override fun init(toolWindow: ToolWindow) {}

  /**
   * eeds to return true for the explorer tool window to function
   */
  override fun shouldBeAvailable(project: Project): Boolean {
    return true
  }
}