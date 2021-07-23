package eu.ibagroup.formainframe.explorer

import com.intellij.openapi.Disposable
import com.intellij.openapi.util.Disposer
import eu.ibagroup.formainframe.config.configCrudable
import eu.ibagroup.formainframe.config.connect.ConnectionConfig
import eu.ibagroup.formainframe.config.connect.UrlConnection
import eu.ibagroup.formainframe.config.ws.DSMask
import eu.ibagroup.formainframe.config.ws.UssPath
import eu.ibagroup.formainframe.config.ws.WorkingSetConfig
import eu.ibagroup.formainframe.utils.clone
import eu.ibagroup.formainframe.utils.crudable.getByForeignKey
import eu.ibagroup.formainframe.utils.crudable.getByForeignKeyDeeply
import eu.ibagroup.formainframe.utils.runIfTrue
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.withLock

/**
 * implementation of WorkingSet
 * By either using action AddWorkingSetAction in explorer or adding working set in the Working Set table in
 * File -> Settings -> Other Settings -> For Mainframe, we add WorkingSetConfig to configCrudable
 * This class uses the data from the WorkingSetConfig to create WorkingSet
 */
class GlobalWorkingSet(
  override val uuid: String,
  globalExplorer: GlobalExplorer,
  private val workingSetConfigProvider: (String) -> WorkingSetConfig?,
  parentDisposable: Disposable
) : WorkingSet, Disposable {

  override val explorer = globalExplorer

  private val lock = ReentrantLock()

  private val isDisposed = AtomicBoolean(false)

  /**
   * returns WorkingSetConfig, if the WorkingSetConfig with the uuid exists
   */
  private val workingSetConfig: WorkingSetConfig?
    get() = lock.withLock {
      (isDisposed.compareAndSet(false, false)).runIfTrue { workingSetConfigProvider(uuid) }
    }

  init {
    Disposer.register(parentDisposable, this)
  }

  /**
   * name of the WorkingSetConfig used to create the GlobalWorkingSet
   */
  override val name
    get() = workingSetConfig?.name ?: ""

  /**
   * connectionConfig used to create the connection on which the WorkingSet is allocated
   */
  override val connectionConfig: ConnectionConfig?
    get() = lock.withLock {
      workingSetConfig
        ?.let {
          return@withLock configCrudable.getByForeignKey(it)
        }
    }

  /**
   * url of the WorkingSetConfig used to create the GlobalWorkingSet
   */
  override val urlConnection: UrlConnection?
    get() = lock.withLock { workingSetConfig?.let { configCrudable.getByForeignKeyDeeply(it) } }

  /**
   * dsMask of the WorkingSetConfig used to create the GlobalWorkingSet
   */
  override val dsMasks: Collection<DSMask>
    get() = lock.withLock { workingSetConfig?.dsMasks ?: listOf() }

  /**
   * adds mask to the GlobalWorkingSet
   */
  override fun addMask(dsMask: DSMask) {
    val newWsConfig = workingSetConfig?.clone() ?: return
    if (newWsConfig.dsMasks.add(dsMask)) {
      configCrudable.update(newWsConfig)
    }
  }

  /**
   * removes mask from the GlobalWorkingSet
   */
  override fun removeMask(dsMask: DSMask) {
    val newWsConfig = workingSetConfig?.clone() ?: return
    if (newWsConfig.dsMasks.remove(dsMask)) {
      configCrudable.update(newWsConfig)
    }
  }

  /**
   * uss paths of the GLobalWorkingSet
   */
  override val ussPaths: Collection<UssPath>
    get() = lock.withLock { workingSetConfig?.ussPaths ?: listOf() }

  /**
   * adds uss path to the GlobalWorkingSet
   */
  override fun addUssPath(ussPath: UssPath) {
    val newWsConfig = workingSetConfig?.clone() ?: return
    if (newWsConfig.ussPaths.add(ussPath)) {
      configCrudable.update(newWsConfig)
    }
  }

  /**
   * removes uss path from the GlobalWorkingSet
   */
  override fun removeUssPath(ussPath: UssPath) {
    val newWsConfig = workingSetConfig?.clone() ?: return
    if (newWsConfig.ussPaths.remove(ussPath)) {
      configCrudable.update(newWsConfig)
    }
  }

  override fun dispose() {
    isDisposed.set(true)
  }

}