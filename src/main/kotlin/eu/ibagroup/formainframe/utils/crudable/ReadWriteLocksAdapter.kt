package eu.ibagroup.formainframe.utils.crudable

import java.util.concurrent.locks.Lock

abstract class ReadWriteLocksAdapter : LocksManager {

  abstract fun <E : Any> getReadLock(rowClass: Class<out E>): Lock?

  abstract fun <E : Any> getWriteLock(rowClass: Class<out E>): Lock?

  override fun <E : Any> getLockForAdding(rowClass: Class<out E>): Lock? {
    return getWriteLock(rowClass)
  }

  override fun <E : Any> getLockForGettingAll(rowClass: Class<out E>): Lock? {
    return getReadLock(rowClass)
  }

  override fun <E : Any> getLockForUpdating(rowClass: Class<out E>): Lock? {
    return getWriteLock(rowClass)
  }

  override fun <E : Any> getLockForDeleting(rowClass: Class<out E>): Lock? {
    return getWriteLock(rowClass)
  }

  override fun <E : Any> getLockForNextUniqueValue(rowClass: Class<out E>): Lock? {
    return getWriteLock(rowClass)
  }

}