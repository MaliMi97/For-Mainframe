package eu.ibagroup.formainframe.dataops.attributes

import eu.ibagroup.formainframe.dataops.DataOpsComponentFactory

/**
 * interface for attributeService extension point
 */
interface AttributesServiceFactory : DataOpsComponentFactory<AttributesService<*, *>>