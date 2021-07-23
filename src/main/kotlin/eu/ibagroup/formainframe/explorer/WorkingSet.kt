package eu.ibagroup.formainframe.explorer

import eu.ibagroup.formainframe.config.ws.DSMask
import eu.ibagroup.formainframe.config.ws.UssPath

/**
 * By either using action AddWorkingSetAction in explorer or adding working set in the Working Set table in
 * File -> Settings -> Other Settings -> For Mainframe, we add WorkingSetConfig to configCrudable
 * The classes derived from this interface use the data from the WorkingSetConfig to create WorkingSet
 */
interface WorkingSet : ExplorerUnit {

  val dsMasks: Collection<DSMask>

  fun addMask(dsMask: DSMask)

  fun removeMask(dsMask: DSMask)

  val ussPaths: Collection<UssPath>

  fun addUssPath(ussPath: UssPath)

  fun removeUssPath(ussPath: UssPath)

}