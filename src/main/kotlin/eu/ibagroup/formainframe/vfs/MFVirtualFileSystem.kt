package eu.ibagroup.formainframe.vfs

import com.intellij.openapi.components.service
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFileManager
import eu.ibagroup.formainframe.dataops.DataOpsManager
import kotlin.IllegalStateException

class MFVirtualFileSystem : VirtualFileSystemModelWrapper<MFVirtualFile, MFVirtualFileSystemModel>(
  MFVirtualFile::class.java,
  MFVirtualFileSystemModel()
) {

  companion object {
    const val SEPARATOR = "/"
    const val PROTOCOL = "mf"
    const val ROOT_NAME = "For Mainframe"
    const val ROOT_ID = 0

    @JvmStatic
    val instance: MFVirtualFileSystem
      get() = VirtualFileManager.getInstance().getFileSystem(PROTOCOL)
        .let { if (it == null) throw IllegalStateException("MFVirtualFileSystem instance is null") else return it as MFVirtualFileSystem }

    @JvmStatic
    val model
      get() = instance.model
  }

  init {
    Disposer.register(service<DataOpsManager>(), this)
  }

  val root = model.root

  override fun isValidName(name: String) = name.isNotBlank()

}