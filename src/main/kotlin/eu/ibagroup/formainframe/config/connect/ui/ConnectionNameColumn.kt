package eu.ibagroup.formainframe.config.connect.ui

import com.intellij.util.ui.ColumnInfo

/**
 * The Name column in the ConnectionsTableModel class.
 */
class ConnectionNameColumn : ColumnInfo<ConnectionDialogState, String>("Name") {

  /**
   * gets the name of the connection
   */
  override fun valueOf(item: ConnectionDialogState): String {
    return item.connectionName
  }

  /**
   * sets the name of the connection
   */
  override fun setValue(item: ConnectionDialogState, value: String) {
    item.connectionName = value
  }

}