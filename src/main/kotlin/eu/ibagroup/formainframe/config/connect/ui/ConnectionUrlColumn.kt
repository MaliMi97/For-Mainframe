package eu.ibagroup.formainframe.config.connect.ui

import com.intellij.util.ui.ColumnInfo

/**
 * The z/OSMF url column in the ConnectionsTableModel class.
 */
@Suppress("DialogTitleCapitalization")
class ConnectionUrlColumn : ColumnInfo<ConnectionDialogState, String>("z/OSMF URL") {

  /**
   * gets the name of the column
   */
  override fun valueOf(item: ConnectionDialogState): String {
    return item.connectionUrl
  }

  /**
   * sets the name of the column
   */
  override fun setValue(item: ConnectionDialogState, value: String) {
    item.connectionUrl = value
  }

}