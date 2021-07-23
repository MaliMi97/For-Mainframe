package eu.ibagroup.formainframe.config.ws.ui

import com.intellij.util.ui.ColumnInfo
import eu.ibagroup.formainframe.common.message
import eu.ibagroup.formainframe.common.ui.ErrorableTableCellRenderer
import eu.ibagroup.formainframe.config.ws.WorkingSetConfig
import javax.swing.JTable
import javax.swing.table.TableCellRenderer

@Suppress("DialogTitleCapitalization")
/**
 * The z/OSMF url column in the WSTableModel class.
 * Members of this column are the z/OSMF urls (ConnectionUrlColumn class) in ConncectionsTableModel.
 */
class UrlColumn(
  private val getUrl: (WorkingSetConfig) -> String?
) : ColumnInfo<WorkingSetConfig, String>(message("configurable.ws.tables.ws.url.name")) {

  /**
   * returns the name of the column
   */
  override fun valueOf(item: WorkingSetConfig): String {
    return getUrl(item) ?: message("configurable.ws.tables.ws.url.error.empty")
  }

  override fun isCellEditable(item: WorkingSetConfig): Boolean {
    return false
  }

  override fun getWidth(table: JTable?): Int {
    return 270
  }

  override fun getRenderer(item: WorkingSetConfig): TableCellRenderer {
    return ErrorableTableCellRenderer(
      errorMessage = message("configurable.ws.tables.ws.url.error.empty.tooltip")
    ) {
      getUrl(item) == null
    }
  }

}