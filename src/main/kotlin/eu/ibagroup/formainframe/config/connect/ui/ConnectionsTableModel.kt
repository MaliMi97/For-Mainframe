package eu.ibagroup.formainframe.config.connect.ui

import eu.ibagroup.formainframe.common.ui.CrudableTableModel
import eu.ibagroup.formainframe.config.connect.ConnectionConfig
import eu.ibagroup.formainframe.config.connect.Credentials
import eu.ibagroup.formainframe.config.connect.UrlConnection
import eu.ibagroup.formainframe.utils.crudable.*
import eu.ibagroup.formainframe.utils.nullable
import eu.ibagroup.formainframe.utils.toMutableList

/**
 * The z/OSMF Connections table in File -> Settings -> Other Settings -> For Mainframe
 * It seems that CrudableListBuilder is used as Crudable later in the code
 */
class ConnectionsTableModel(
  crudable: Crudable
) : CrudableTableModel<ConnectionDialogState>(
  crudable,
  ConnectionNameColumn(),
  ConnectionUrlColumn(),
  ConnectionUsernameColumn()
) {

  /**
   * gets all rows
   */
  override fun fetch(crudable: Crudable): MutableList<ConnectionDialogState> {
    return crudable.getAll<ConnectionConfig>().map {
      it.toDialogState(crudable)
    }.toMutableList()
  }

  /**
   * updates table
   * is called when setting up the table
   * is not called when using onAdd or onDelete
   */
  override fun onUpdate(crudable: Crudable, value: ConnectionDialogState): Boolean {
    return with(crudable) {
      val oldUrlConnection = crudable.getByUniqueKey<UrlConnection>(value.urlConnectionUuid)
      if (oldUrlConnection != null) {
        if (oldUrlConnection.url != value.urlConnection.url) {
          value.urlConnectionUuid = crudable.nextUniqueValue<UrlConnection, String>()
        }
        listOf(
          update(value.credentials),
          update(value.connectionConfig),
          addOrUpdate(value.urlConnection)
        ).all { it.isPresent }
      } else {
        false
      }
    }
  }

  /**
   * deletes row
   */
  override fun onDelete(crudable: Crudable, value: ConnectionDialogState) {
    with(crudable) {
      delete(value.credentials)
      delete(value.connectionConfig)
      if (!this.getAll<ConnectionConfig>().anyMatch {
          //Check if UrlConnectionConfig is not in use by any other ConnectionConfigs - hotfix
          //TODO
          it.uuid != value.connectionUuid && it.urlConnectionUuid == value.urlConnection.uuid
      }) {
        delete(value.urlConnection)
      }
    }
  }

  /**
   * adds row
   */
  override fun onAdd(crudable: Crudable, value: ConnectionDialogState): Boolean {
    return with(crudable) {
      val alreadyDefinedZOSMFUrlConnection = crudable.find<UrlConnection> {
        it.url == value.connectionUrl && it.isAllowSelfSigned == value.isAllowSsl
      }.findAny().nullable
      if (alreadyDefinedZOSMFUrlConnection != null) {
        value.urlConnection = alreadyDefinedZOSMFUrlConnection
      } else {
        value.urlConnectionUuid = crudable.nextUniqueValue<UrlConnection, String>()
      }
      value.connectionUuid = crudable.nextUniqueValue<ConnectionConfig, String>()
      listOf(
        add(value.credentials),
        add(value.connectionConfig),
        addOrUpdate(value.urlConnection)
      ).all { it.isPresent }
    }
  }

  /**
   * NOT SURE WHAT IT DOES
   */
  override fun onApplyingMergedCollection(crudable: Crudable, merged: MergedCollections<ConnectionDialogState>) {
    listOf(
      Pair(Credentials::class.java, ConnectionDialogState::credentials),
      Pair(ConnectionConfig::class.java, ConnectionDialogState::connectionConfig),
      Pair(UrlConnection::class.java, ConnectionDialogState::urlConnection)
    ).forEach { pair ->
      crudable.applyMergedCollections(
        pair.first, MergedCollections(
          toAdd = merged.toAdd.map { pair.second.get(it) },
          toUpdate = merged.toUpdate.map { pair.second.get(it) },
          toDelete = merged.toDelete.map { pair.second.get(it) }
        )
      )
    }
  }

  /**
   * edits Connection
   */
  override fun set(row: Int, item: ConnectionDialogState) {
    get(row).isAllowSsl = item.isAllowSsl
    get(row).password = item.password
    super.set(row, item)
  }

  /**
   * NOT SURE WHAT THIS IS SUPPOSED TO BE
   */
  override val clazz = ConnectionDialogState::class.java
}