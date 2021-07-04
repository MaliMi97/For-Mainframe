package eu.ibagroup.formainframe.config.ws.ui

import com.intellij.openapi.ui.ValidationInfo
import eu.ibagroup.formainframe.common.message
import eu.ibagroup.formainframe.common.ui.ValidatingCellRenderer
import eu.ibagroup.formainframe.common.ui.ValidatingColumnInfo
import eu.ibagroup.formainframe.config.ws.WorkingSetConfig
import javax.swing.JComponent
import javax.swing.JTable

/**
 * The Name column in the WSTableModel class.
 * Names of Work Sets are members of this column.
 */
class WSNameColumn(private val wsProvider: () -> List<WorkingSetConfig>) :
  ValidatingColumnInfo<WorkingSetConfig>(message("configurable.ws.tables.ws.name")) {

  companion object {
    /**
     * not used code
     */
    @JvmStatic
    private fun getDefaultError(component: JComponent) =
      ValidationInfo(message("configurable.ws.tables.ws.name.tooltip.error"), component)
  }

  /**
   * not used code
   */
  override fun validateOnInput(oldItem: WorkingSetConfig, newValue: String, component: JComponent): ValidationInfo? {
    with(newValue.trim()) {
      return if ((oldItem.name == this && wsProvider().count { it.name == this } > 1)
        || (oldItem.name != this && wsProvider().any { it.name == this })) {
        getDefaultError(component)
      } else {
        null
      }
    }
  }

  /**
   * returns the name of the column
   */
  override fun valueOf(item: WorkingSetConfig): String {
    return item.name
  }

  /**
   * not used code
   */
  override fun isCellEditable(item: WorkingSetConfig?): Boolean {
    return false
  }

  /**
   * not used code
   */
  override fun getWidth(table: JTable?): Int {
    return 200
  }

  /**
   * sets the name of the column to value
   */
  override fun setValue(item: WorkingSetConfig, value: String) {
    item.name = value
  }

  /**
   * not used code
   */
  override fun getTooltipText(): String {
    return message("configurable.ws.tables.ws.name.tooltip")
  }

  /**
   * not used code
   */
  override fun validateEntered(item: WorkingSetConfig, component: JComponent): ValidationInfo? {
    return if (wsProvider().count { it.name == item.name } > 1) {
      getDefaultError(component)
    } else {
      when {
        item.name.isEmpty() -> ValidationInfo("Can't be empty", component)
        item.name.isBlank() -> ValidationInfo("Can't be blank", component)
        else -> null
      }
    }
  }

  /**
   * not used code
   */
  override fun getValidatingCellRenderer(item: WorkingSetConfig): ValidatingCellRenderer<WorkingSetConfig> {
    return ValidatingCellRenderer()
  }
}
