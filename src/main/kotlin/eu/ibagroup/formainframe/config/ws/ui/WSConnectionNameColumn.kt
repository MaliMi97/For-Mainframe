package eu.ibagroup.formainframe.config.ws.ui

import com.intellij.util.ui.ColumnInfo
import com.intellij.util.ui.ComboBoxCellEditor
import eu.ibagroup.formainframe.common.message
import eu.ibagroup.formainframe.config.connect.ConnectionConfig
import eu.ibagroup.formainframe.config.ws.WorkingSetConfig
import eu.ibagroup.formainframe.utils.crudable.Crudable
import eu.ibagroup.formainframe.utils.crudable.find
import eu.ibagroup.formainframe.utils.crudable.getAll
import eu.ibagroup.formainframe.utils.crudable.getByUniqueKey
import eu.ibagroup.formainframe.utils.findAnyNullable
import eu.ibagroup.formainframe.utils.toMutableList
import javax.swing.table.TableCellEditor

/**
 * The Connection Name column in the WSTableModel class.
 * Members of this column are the names (ConnectionNameColumn class) in ConncectionsTableModel.
 */
class WSConnectionNameColumn(private val crudable: Crudable) :
  ColumnInfo<WorkingSetConfig, String>(message("configurable.ws.tables.ws.connection.name")) {

  inner class ConnectionTableCellEditor : ComboBoxCellEditor() {
    /**
     * not used code
     */
    override fun getComboBoxItems(): MutableList<String> {
      return crudable.getAll<ConnectionConfig>()
        .map { it.name }
        .toMutableList()
    }
  }

  /**
   * sets the name of the column to value
   */
  override fun setValue(item: WorkingSetConfig, value: String) {
    crudable.find<ConnectionConfig> { it.name == value }.findAnyNullable()?.let {
      item.connectionConfigUuid = it.uuid
    }
  }

  /**
   * returns the name of the column
   */
  override fun valueOf(item: WorkingSetConfig): String {
    return crudable.getByUniqueKey<ConnectionConfig>(item.connectionConfigUuid)?.name ?: ""
  }

  /**
   * not used code
   */
  override fun isCellEditable(item: WorkingSetConfig): Boolean {
    return false
  }

  /**
   * not used code
   */
  override fun getEditor(item: WorkingSetConfig): TableCellEditor {
    return ConnectionTableCellEditor()
  }

  /**
   * not used code
   */
  override fun getTooltipText(): String {
    return message("configurable.ws.tables.ws.connection.tooltip")
  }

}