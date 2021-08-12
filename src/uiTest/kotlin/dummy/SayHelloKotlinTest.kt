import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.fixtures.*
import com.intellij.remoterobot.search.locators.byXpath
import com.intellij.remoterobot.stepsProcessing.step
import com.intellij.remoterobot.utils.waitFor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.wait
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.After
import org.junit.Before
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.*
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.reflect.Method
import java.time.Duration
import javax.imageio.ImageIO

@ExtendWith(RemoteRobotExtension::class)
class dum {

    @BeforeEach
    fun setUp(remoteRobot: RemoteRobot) = with(remoteRobot) {
        welcomeFrame {
            openProject.click()
            dialog("Open File or Project") {
                textField(byXpath("","//div[@class='BorderlessTextField']")).text =
                    System.getProperty("user.dir") + "/src/uiTest/resources/untitled"
                button("OK").click()
            }
        }
        Thread.sleep(30000)
    }

    @AfterEach
    fun tearDown(remoteRobot: RemoteRobot) = with(remoteRobot) {
        actionMenu(remoteRobot, "File").click()
        actionMenuItem(remoteRobot, "Close Project").click()
    }

    @Test
    fun test(remoteRobot: RemoteRobot) = with(remoteRobot) {
        ideFrameImpl("untitled") {
            forMainframe.click()
            explorer {
                settings.click()
            }
            dialog("Settings") {
                configurableEditor {
                    find<ComponentFixture>(byXpath("z/OSMF Connections","//div[@accessiblename='z/OSMF Connections' and @class='TabLabel']")).click()
                    actionButton(byXpath("Add","//div[@accessiblename='Add' and @class='ActionButton' and @myaction='Add (Add)']"))
                        .click()
                }
                dialog("Add Connection") {
                    var conParams = findAll<JTextFieldFixture>(byXpath("//div[@class='JBTextField']"))
                    conParams[0].text = "a"
                    conParams[1].text = "https://a.com"
                    conParams[2].text = "a"
                    textField(byXpath("//div[@class='JPasswordField']")).text = "a"
                    checkBox(byXpath("//div[@accessiblename='Accept self-signed SSL certificates' and @class='JBCheckBox' and @text='Accept self-signed SSL certificates']"))
                        .select()
                    button("OK").click()
                }
                dialog("Error Creating Connection") {
                    button(byXpath("//div[@accessiblename='Yes' and @class='JButton' and @text='Yes']")).click()
                }
                configurableEditor {
                    actionButton(byXpath("Add","//div[@accessiblename='Add' and @class='ActionButton' and @myaction='Add (Add)']"))
                        .click()
                }
                dialog("Add Connection") {
                    var conParams = findAll<JTextFieldFixture>(byXpath("//div[@class='JBTextField']"))
                    conParams[0].text = "a"
                    conParams[1].text = "https://b.com"
                    conParams[2].text = "b"
                    textField(byXpath("//div[@class='JPasswordField']")).text = "b"
                    checkBox(byXpath("//div[@accessiblename='Accept self-signed SSL certificates' and @class='JBCheckBox' and @text='Accept self-signed SSL certificates']"))
                        .select()
                    assertFalse(button("OK").isEnabled())
                    button("Cancel").click()
                }
                button("Cancel").click()
            }
            forMainframe.click()
        }
    }
}

fun RemoteRobot.configurableEditor(function: ConfigurableEditor.() -> Unit) {
    find<ConfigurableEditor>(ConfigurableEditor.xPath(), Duration.ofSeconds(60)).apply(function)
}

@FixtureName("Explorer")
class ConfigurableEditor(remoteRobot: RemoteRobot, remoteComponent: RemoteComponent) : CommonContainerFixture(remoteRobot, remoteComponent) {
    companion object {
        @JvmStatic
        fun xPath() = byXpath("","//div[@class='ConfigurableEditor']")
    }
}

fun RemoteRobot.explorer(function: Explorer.() -> Unit) {
    find<Explorer>(Explorer.xPath(), Duration.ofSeconds(60)).apply(function)
}

@FixtureName("Explorer")
class Explorer(remoteRobot: RemoteRobot, remoteComponent: RemoteComponent) : CommonContainerFixture(remoteRobot, remoteComponent) {
    val settings = actionButton(byXpath("","//div[@class='ActionButton' and @myaction=' ()']"))
    companion object {
        @JvmStatic
        fun xPath() = byXpath("File Explorer", "//div[@accessiblename='File Explorer Tool Window' and @class='InternalDecoratorImpl']")
    }
}

fun RemoteRobot.ideFrameImpl(name: String, function: IdeFrameImpl.() -> Unit) {
        find<IdeFrameImpl>(IdeFrameImpl.xPath(name), Duration.ofSeconds(60)).apply(function)
}

@FixtureName("IdeFrameImpl")
class IdeFrameImpl(remoteRobot: RemoteRobot, remoteComponent: RemoteComponent) : CommonContainerFixture(remoteRobot, remoteComponent) {
    val forMainframe
        get() = button(byXpath("For Mainframe", "//div[@accessiblename='For Mainframe' and @class='StripeButton' and @text='For Mainframe']"))
    companion object {
        @JvmStatic
        fun xPath(name: String) = byXpath("$name", "//div[@accessiblename='$name - IntelliJ IDEA' and @class='IdeFrameImpl']")
    }
}

fun RemoteRobot.welcomeFrame(function: WelcomeFrame.()-> Unit) {
    find(WelcomeFrame::class.java, Duration.ofSeconds(60)).apply(function)
}

@FixtureName("Welcome Frame")
@DefaultXpath("type", "//div[@class='FlatWelcomeFrame']")
class WelcomeFrame(remoteRobot: RemoteRobot, remoteComponent: RemoteComponent) : CommonContainerFixture(remoteRobot, remoteComponent) {
    val openProject
        get() = actionLink(byXpath("Open Project", "//div[(@accessiblename='Open or Import' and @class='JButton') or (@class='MainButton' and @text='Open')]"))
}

fun ContainerFixture.dialog(
    title: String,
    timeout: Duration = Duration.ofSeconds(60),
    function: DialogFixture.() -> Unit = {}): DialogFixture = step("Search for dialog with title $title") {
    find<DialogFixture>(DialogFixture.byTitle(title), timeout).apply(function)
}

@FixtureName("Dialog")
class DialogFixture(
    remoteRobot: RemoteRobot,
    remoteComponent: RemoteComponent) : CommonContainerFixture(remoteRobot, remoteComponent) {

    companion object {
        @JvmStatic
        fun byTitle(title: String) = byXpath("title $title", "//div[@title='$title' and @class='MyDialog']")
    }

    val title: String
        get() = callJs("component.getTitle();")
}

fun RemoteRobot.actionMenu(remoteRobot: RemoteRobot, text: String): ActionMenuFixture {
    val xpath = byXpath("text '$text'", "//div[@class='ActionMenu' and @text='$text']")
    waitFor {
        findAll<ActionMenuFixture>(xpath).isNotEmpty()
    }
    return findAll<ActionMenuFixture>(xpath).first()
}

fun RemoteRobot.actionMenuItem(remoteRobot: RemoteRobot, text: String): ActionMenuItemFixture {
    val xpath = byXpath("text '$text'", "//div[@class='ActionMenuItem' and @text='$text']")
    waitFor {
        findAll<ActionMenuItemFixture>(xpath).isNotEmpty()
    }
    return findAll<ActionMenuItemFixture>(xpath).first()
}

@FixtureName("ActionMenu")
class ActionMenuFixture(remoteRobot: RemoteRobot, remoteComponent: RemoteComponent) : ComponentFixture(remoteRobot, remoteComponent)

@FixtureName("ActionMenuItem")
class ActionMenuItemFixture(remoteRobot: RemoteRobot, remoteComponent: RemoteComponent) : ComponentFixture(remoteRobot, remoteComponent)














class RemoteRobotExtension : AfterTestExecutionCallback, ParameterResolver {
    private val url: String = System.getProperty("remote-robot-url") ?: "http://127.0.0.1:8580"
    private val remoteRobot: RemoteRobot = if (System.getProperty("debug-retrofit")?.equals("enable") == true) {
        val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder().apply {
            this.addInterceptor(interceptor)
        }.build()
        RemoteRobot(url, client)
    } else {
        RemoteRobot(url)
    }
    private val client = OkHttpClient()

    override fun supportsParameter(parameterContext: ParameterContext?, extensionContext: ExtensionContext?): Boolean {
        return parameterContext?.parameter?.type?.equals(RemoteRobot::class.java) ?: false
    }

    override fun resolveParameter(parameterContext: ParameterContext?, extensionContext: ExtensionContext?): Any {
        return remoteRobot
    }

    override fun afterTestExecution(context: ExtensionContext?) {
        val testMethod: Method = context?.requiredTestMethod ?: throw IllegalStateException("test method is null")
        val testMethodName = testMethod.name
        val testFailed: Boolean = context.executionException?.isPresent ?: false
        if (testFailed) {
//            saveScreenshot(testMethodName)
            saveIdeaFrames(testMethodName)
            saveHierarchy(testMethodName)
        }
    }

    private fun saveScreenshot(testName: String) {
        fetchScreenShot().save(testName)
    }

    private fun saveHierarchy(testName: String) {
        val hierarchySnapshot =
            saveFile(url, "build/reports", "hierarchy-$testName.html")
        if (File("build/reports/styles.css").exists().not()) {
            saveFile("$url/styles.css", "build/reports", "styles.css")
        }
        println("Hierarchy snapshot: ${hierarchySnapshot.absolutePath}")
    }

    private fun saveFile(url: String, folder: String, name: String): File {
        val response = client.newCall(Request.Builder().url(url).build()).execute()
        return File(folder).apply {
            mkdirs()
        }.resolve(name).apply {
            writeText(response.body?.string() ?: "")
        }
    }

    private fun BufferedImage.save(name: String) {
        val bytes = ByteArrayOutputStream().use { b ->
            ImageIO.write(this, "png", b)
            b.toByteArray()
        }
        File("build/reports").apply { mkdirs() }.resolve("$name.png").writeBytes(bytes)
    }

    private fun saveIdeaFrames(testName: String) {
        remoteRobot.findAll<ContainerFixture>(byXpath("//div[@class='IdeFrameImpl']")).forEachIndexed { n, frame ->
            val pic = try {
                frame.callJs<ByteArray>(
                    """
                        importPackage(java.io)
                        importPackage(javax.imageio)
                        importPackage(java.awt.image)
                        const screenShot = new BufferedImage(component.getWidth(), component.getHeight(), BufferedImage.TYPE_INT_ARGB);
                        component.paint(screenShot.getGraphics())
                        let pictureBytes;
                        const baos = new ByteArrayOutputStream();
                        try {
                            ImageIO.write(screenShot, "png", baos);
                            pictureBytes = baos.toByteArray();
                        } finally {
                          baos.close();
                        }
                        pictureBytes;   
            """, true
                )
            } catch (e: Throwable) {
                e.printStackTrace()
                throw e
            }
            pic.inputStream().use {
                ImageIO.read(it)
            }.save(testName + "_" + n)
        }
    }

    private fun fetchScreenShot(): BufferedImage {
        return remoteRobot.callJs<ByteArray>(
            """
            importPackage(java.io)
            importPackage(javax.imageio)
            const screenShot = new java.awt.Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
            let pictureBytes;
            const baos = new ByteArrayOutputStream();
            try {
                ImageIO.write(screenShot, "png", baos);
                pictureBytes = baos.toByteArray();
            } finally {
              baos.close();
            }
            pictureBytes;
        """
        ).inputStream().use {
            ImageIO.read(it)
        }
    }
}