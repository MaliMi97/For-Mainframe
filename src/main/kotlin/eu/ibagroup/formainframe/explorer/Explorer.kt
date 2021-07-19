package eu.ibagroup.formainframe.explorer

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ComponentManager
import com.intellij.openapi.project.Project
import com.intellij.util.messages.Topic
import eu.ibagroup.formainframe.utils.castOrNull

val globalExplorer
  get() = Explorer.instance

interface ExplorerListener {
  fun onChanged(explorer: Explorer, unit: ExplorerUnit) {}
  fun onAdded(explorer: Explorer, unit: ExplorerUnit) {}
  fun onDeleted(explorer: Explorer, unit: ExplorerUnit) {}
}

@JvmField
val UNITS_CHANGED = Topic.create("unitsChanged", ExplorerListener::class.java)

/**
 * an interface for an extension point
 * the intelliJ explorer
 */
interface Explorer {

  companion object {
    @JvmStatic
    val instance: Explorer
      get() = ApplicationManager.getApplication().getService(Explorer::class.java)
  }

  /**
   * collection of units (WorkingSets)
   */
  val units: Collection<ExplorerUnit>

  /**
   * disposes/deletes unit (WorkingSet)
   */
  fun disposeUnit(unit: ExplorerUnit)

  /**
   * checks whether unit is of necessary type (for now only WorkingSets) and whether units contain it
   */
  fun isUnitPresented(unit: ExplorerUnit): Boolean

  val componentManager: ComponentManager

  /**
   * is componentManager (the Application in case of GlobalExplorer) cased as Project, if the componentManager is not null
   *
   * is null otherwise
   */
  val nullableProject: Project?
    get() = componentManager.castOrNull()

  fun reportThrowable(t: Throwable, project: Project?)

  fun reportThrowable(t: Throwable, unit: ExplorerUnit, project: Project?)

}