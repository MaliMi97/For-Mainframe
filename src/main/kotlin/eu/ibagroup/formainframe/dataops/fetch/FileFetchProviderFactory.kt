package eu.ibagroup.formainframe.dataops.fetch

import eu.ibagroup.formainframe.dataops.DataOpsComponentFactory

/**
 * interface for fileDataProvider extension point
 */
interface FileFetchProviderFactory : DataOpsComponentFactory<FileFetchProvider<*, *, *>>