package eu.ibagroup.formainframe.config.ws.ui

import eu.ibagroup.formainframe.common.ui.CrudableTableModel
import eu.ibagroup.formainframe.config.connect.Credentials
import eu.ibagroup.formainframe.config.connect.UrlConnection
import eu.ibagroup.formainframe.config.ws.WorkingSetConfig
import eu.ibagroup.formainframe.utils.crudable.*
import eu.ibagroup.formainframe.utils.toMutableList

/**
 * Table of Working Sets showing their names, the names  of their connections, usernames and z/OSMF url
 * Located in File -> Settings -> Other Settings -> For Mainframe -> Working Sets
 * It seems that CrudableListBuilder is used as Crudable later in the code
 * Why are we using in onAdd, onDelete, etc. WorkingSetConfig and not WorkingSetDialogState?
 */
class WSTableModel(
  crudable: Crudable,
) : CrudableTableModel<WorkingSetConfig>(crudable) {

  init {
    columnInfos = arrayOf(
      WSNameColumn { this.items },
      WSConnectionNameColumn(crudable),
      WSUsernameColumn { crudable.getByUniqueKey<Credentials>(it.connectionConfigUuid)?.username },
      UrlColumn { crudable.getByForeignKeyDeeply<WorkingSetConfig, UrlConnection>(it)?.url }
    )
  }

  /**
   * gets all rows
   */
  override fun fetch(crudable: Crudable): MutableList<WorkingSetConfig> {
    return crudable.getAll<WorkingSetConfig>().toMutableList()
  }

  /**
   * updates table
   */
  override fun onUpdate(crudable: Crudable, value: WorkingSetConfig): Boolean {
    return crudable.update(value).isPresent
  }

  /**
   * deletes row
   */
  override fun onDelete(crudable: Crudable, value: WorkingSetConfig) {
    crudable.delete(value)
  }

  /**
   * adds row
   */
  override fun onAdd(crudable: Crudable, value: WorkingSetConfig): Boolean {
    return crudable.add(value).isPresent
  }

  /**
   * NOT SURE WHAT IT DOES
   */
  override fun onApplyingMergedCollection(crudable: Crudable, merged: MergedCollections<WorkingSetConfig>) {
    crudable.applyMergedCollections(merged)
  }

  /**
   * NOT SURE WHAT THIS IS FOR
   */
  override val clazz = WorkingSetConfig::class.java

  /**
   * adds / edits DS Mask or USS Path
   */
  override operator fun set(row: Int, item: WorkingSetConfig) {
    get(row).dsMasks = item.dsMasks
    get(row).ussPaths = item.ussPaths
    super.set(row, item)
  }

}