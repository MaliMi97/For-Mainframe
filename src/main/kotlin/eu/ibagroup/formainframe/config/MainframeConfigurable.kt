package eu.ibagroup.formainframe.config

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.TabbedConfigurable
import eu.ibagroup.formainframe.config.connect.ui.ConnectionConfigurable
import eu.ibagroup.formainframe.config.ws.ui.WSConfigurable

/**
 * creates the For Mainframe setting in File -> Settings -> Other Settings
 */
class MainframeConfigurable : TabbedConfigurable() {

  var preferredConfigurableClass: Class<*>? = null

  override fun getDisplayName(): String {
    return "For Mainframe"
  }

  private lateinit var connectionConfigurable: ConnectionConfigurable
  private lateinit var wsConfigurable: WSConfigurable

  override fun createConfigurables(): MutableList<Configurable> {
    return mutableListOf(
      ConnectionConfigurable().also { connectionConfigurable = it },
      WSConfigurable().also { wsConfigurable = it }
    )
  }

  override fun apply() {
    super.apply()
    ConfigSandbox.instance.updateState()
  }

  override fun reset() {
    ConfigSandbox.instance.fetch()
    super.reset()
  }

  override fun cancel() {
    configurables.forEach { it.cancel() }
  }

  override fun createConfigurableTabs() {
    super.createConfigurableTabs().also { myTabbedPane.selectedIndex = if (preferredConfigurableClass == WSConfigurable::class.java) 1 else 0 }
  }

}