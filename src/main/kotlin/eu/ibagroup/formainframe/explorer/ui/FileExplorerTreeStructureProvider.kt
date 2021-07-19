package eu.ibagroup.formainframe.explorer.ui

import eu.ibagroup.formainframe.explorer.ExplorerViewSettings

class FileExplorerTreeStructureProvider : ExplorerTreeStructureProvider() {

  /**
   * returns children of the node as MutableList
   */
  override fun modifyOurs(
    parent: ExplorerTreeNode<*>,
    children: Collection<ExplorerTreeNode<*>>,
    settings: ExplorerViewSettings
  ): MutableCollection<ExplorerTreeNode<*>> {
    return children.toMutableList()
  }
}