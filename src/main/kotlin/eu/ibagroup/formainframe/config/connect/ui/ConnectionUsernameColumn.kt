package eu.ibagroup.formainframe.config.connect.ui

import com.intellij.util.ui.ColumnInfo

/**
 * The Name column in the ConnectionsTableModel class.
 */
class ConnectionUsernameColumn : ColumnInfo<ConnectionDialogState, String>("Username") {

  /**
   * gets the name of the the column
   */
  override fun valueOf(item: ConnectionDialogState): String {
    return item.username
  }

  /**
   * sets the name of the column
   */
  override fun setValue(item: ConnectionDialogState, value: String) {
    item.username = value
  }

}