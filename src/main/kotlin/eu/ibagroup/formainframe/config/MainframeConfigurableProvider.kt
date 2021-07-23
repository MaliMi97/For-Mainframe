package eu.ibagroup.formainframe.config

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurableProvider

/**
 * provider class for applicationConfigurable extension point
 */
class MainframeConfigurableProvider : ConfigurableProvider() {
  /**
   * creates the For Mainframe tab in File -> Settings -> Other Settings
   */
  override fun createConfigurable(): Configurable {
    return MainframeConfigurable()
  }
}