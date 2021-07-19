package eu.ibagroup.formainframe.utils

import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.ide.plugins.PluginManager
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.*
import com.intellij.openapi.components.ComponentManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.serviceContainer.AlreadyDisposedException
import com.intellij.util.messages.Topic
import org.jetbrains.annotations.Nls
import org.jetbrains.concurrency.*
import java.io.File
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class Dummy private constructor()

/**
 * returns IdeaPluginDescriptor of enabled plugin by class.java
 *
 * returns null if no such plugin is enabled
 */
fun PluginManager.getPluginDescriptorByClass(clazz: Class<*>): IdeaPluginDescriptor? {
  return getPluginOrPlatformByClassName(clazz.name)?.let {
    findEnabledPlugin(it)
  }
}

/**
 * not used code
 */
val forMainframePluginDescriptor by lazy {
  PluginManager.getInstance().getPluginDescriptorByClass(Dummy::class.java)
    ?: throw IllegalStateException("Dummy class wasn't loaded by For Mainframe plugin's class loader for some reason")
}

/**
 * NOT SURE, the path is to a directory
 */
val cachesDir by lazy {
  val cachesDirString = System.getProperty("caches_dir")
  val cachesDir = File(cachesDirString ?: PathManager.getSystemPath() + "/caches/")
  return@lazy cachesDir
}

/**
 * NOT SURE
 */
fun <L> sendTopic(
  topic: Topic<L>,
  componentManager: ComponentManager = ApplicationManager.getApplication()
): L {
  return componentManager.messageBus.syncPublisher(topic)
}

/**
 * creates new connection to the componentManager's messageBus and subscribes the handler to the target endpoint (topic)
 *
 * the connection is released when disposable's parent is collected
 */
fun <L : Any> subscribe(
  componentManager: ComponentManager,
  topic: Topic<L>,
  handler: L,
  disposable: Disposable
) = componentManager
  .messageBus
  .connect(disposable)
  .subscribe(topic, handler)

/**
 * creates new connection and subscribes the handler to the target endpoint (topic)
 *
 * the connection is disconnected on message bus dispose, or on explicit dispose
 */
fun <L : Any> subscribe(
  componentManager: ComponentManager,
  topic: Topic<L>,
  handler: L
) = componentManager
  .messageBus
  .connect()
  .subscribe(topic, handler)

/**
 * not used code
 */
fun <L : Any> subscribe(topic: Topic<L>, handler: L) = ApplicationManager.getApplication()
  .messageBus
  .connect()
  .subscribe(topic, handler)

/**
 * creates new connection to the Application's messageBus and subscribes the handler to the target endpoint (topic)
 *
 * the connection is released when disposable's parent is collected
 */
fun <L : Any> subscribe(topic: Topic<L>, disposable: Disposable, handler: L) = ApplicationManager.getApplication()
  .messageBus
  .connect(disposable)
  .subscribe(topic, handler)

/**
 * not used code
 */
fun assertReadAllowed() = ApplicationManager.getApplication().assertReadAccessAllowed()

/**
 * assert whether the Application's write access is allowed
 */
fun assertWriteAllowed() = ApplicationManager.getApplication().assertWriteAccessAllowed()

/**
 * creates a write-thread-based executor with no modal dialogs, submits/schedules the block to the executor and returns
 * the result of the block
 */
fun <T> submitOnWriteThread(block: () -> T): T {
  @Suppress("UnstableApiUsage")
  return AppUIExecutor.onWriteThread(ModalityState.NON_MODAL).submit(block).get()
}

/**
 * basically it executes the block as writeAction
 */
@Suppress("UnstableApiUsage")
inline fun <T> runWriteActionOnWriteThread(crossinline block: () -> T): T {
  val app = ApplicationManager.getApplication()
  return if (app.isWriteThread) {
    if (app.isWriteAccessAllowed) {
      block()
    } else {
      runWriteAction(block)
    }
  } else
    submitOnWriteThread {
      runWriteAction(block)
    }
}

/**
 * NOT SURE
 */
inline fun <T> runReadActionInEdtAndWait(crossinline block: () -> T): T {
  return invokeAndWaitIfNeeded { runReadAction(block)}
}

/**
 * not used code
 */
fun AlreadyDisposedException(clazz: Class<*>) = AlreadyDisposedException("${clazz.name} is already disposed")

inline fun <reified S : Any> ComponentManager.service(): S {
  return getService(S::class.java)
}

inline fun <reified S : Any> ComponentManager.component(): S {
  return getComponent(S::class.java)
}

/**
 * not used code
 */
inline fun <T> runPromiseAsBackgroundTask(
  title: String,
  project: Project? = null,
  canBeCancelled: Boolean = true,
  needsToCancelPromise: Boolean = false,
  crossinline promiseGetter: (ProgressIndicator) -> Promise<T>
) {
  ProgressManager.getInstance().run(object : Task.Backgroundable(project, title, canBeCancelled) {
    private var promise: Promise<T>? = null
    override fun run(indicator: ProgressIndicator) {
      val lock = ReentrantLock()
      val condition = lock.newCondition()
      promise = promiseGetter(indicator).also {
        it.onProcessed {
          lock.withLock { condition.signalAll() }
        }
      }
      lock.withLock { condition.await() }
    }

    override fun onCancel() {
      if (needsToCancelPromise) {
        promise.castOrNull<CancellablePromise<*>>()?.cancel()
      }
    }
  })
}


/**
 * NOT SURE
 *
 * wakes up all waiting threats, the current threat waits for result of the promise
 *
 * returns this on success
 *
 * throws failure on error
 */
fun <T> Promise<T>.get(): T? {
  return if (this is AsyncPromise<T>) {
    get()
  } else {
    val lock = ReentrantLock()
    val condition = lock.newCondition()
    var value: T? = null
    var throwable: Throwable? = null
    var isSuccess = false
    onSuccess {
      isSuccess = true
      value = it
      lock.withLock {
        condition.signalAll()
      }
    }
    onError {
      throwable = it
      lock.withLock {
        condition.signalAll()
      }
    }
    lock.withLock { condition.await() }
    if (isSuccess) {
      value
    } else {
      throw throwable ?: Throwable()
    }
  }
}

/**
 * NOT SURE
 */
inline fun <reified T> runTask(
  @Nls(capitalization = Nls.Capitalization.Sentence) title: String,
  project: Project? = null,
  cancellable: Boolean = true,
  crossinline task: (ProgressIndicator) -> T
): T {
  return ProgressManager.getInstance().run(object : Task.WithResult<T, Exception>(project, title, cancellable) {
    override fun compute(indicator: ProgressIndicator): T {
      return task(indicator)
    }
  })
}

/**
 * not used code
 */
inline fun <reified S : Any> ComponentManager.hasService(): Boolean {
  return picoContainer.getComponentInstance(S::class.java.name) != null
}

/**
 * NOT SURE
 */
inline fun runWriteActionInEdt(crossinline block: () -> Unit) {
  runInEdt {
    runWriteAction(block)
  }
}