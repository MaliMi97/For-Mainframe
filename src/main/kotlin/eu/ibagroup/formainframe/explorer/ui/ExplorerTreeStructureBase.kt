package eu.ibagroup.formainframe.explorer.ui

import com.intellij.ide.util.treeView.AbstractTreeStructureBase
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import eu.ibagroup.formainframe.explorer.Explorer
import eu.ibagroup.formainframe.explorer.ExplorerViewSettings

abstract class ExplorerTreeStructureBase(
  protected val explorer: Explorer,
  protected val project: Project
) : AbstractTreeStructureBase(project), ExplorerViewSettings {

  /**
   * registers node to the tree structure
   */
  abstract fun registerNode(node: ExplorerTreeNode<*>)

  /**
   * finds data set by value
   */
  abstract fun <V : Any> findByValue(value: V): Collection<ExplorerTreeNode<V>>

  /**
   * returns a list of data sets matching predicate
   */
  abstract fun findByPredicate(predicate: (ExplorerTreeNode<*>) -> Boolean): Collection<ExplorerTreeNode<*>>

  /**
   * finds file
   */
  abstract fun findByVirtualFile(file: VirtualFile): Collection<ExplorerTreeNode<*>>

}