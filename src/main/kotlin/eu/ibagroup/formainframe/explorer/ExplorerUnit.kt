package eu.ibagroup.formainframe.explorer

import eu.ibagroup.formainframe.config.connect.ConnectionConfig
import eu.ibagroup.formainframe.config.connect.UrlConnection

/**
 * element/unit of the explorer, for example WorkingSets
 */
interface ExplorerUnit {

  val name: String

  val uuid: String

  val connectionConfig: ConnectionConfig?

  val urlConnection: UrlConnection?

  val explorer: Explorer

}