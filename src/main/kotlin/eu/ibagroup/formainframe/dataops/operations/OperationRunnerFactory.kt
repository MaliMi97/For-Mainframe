package eu.ibagroup.formainframe.dataops.operations

import eu.ibagroup.formainframe.dataops.DataOpsComponentFactory

/**
 * interface for operationRunner extension point
 */
interface OperationRunnerFactory : DataOpsComponentFactory<OperationRunner<*, *>> {
}