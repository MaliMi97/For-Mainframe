package auxiliary

import com.intellij.remoterobot.fixtures.CommonContainerFixture
import com.intellij.remoterobot.search.locators.Locator
import com.intellij.remoterobot.utils.waitFor
import java.time.Duration

fun CommonContainerFixture.clickButton(text: String) {
    val button = button(text)
    waitFor(Duration.ofSeconds(60)) {
        button.isEnabled()
    }
    button.click()
}

fun CommonContainerFixture.clickButton(locator: Locator, duration: Duration = Duration.ofSeconds(60)) {
    val button = button(locator)
    waitFor(Duration.ofSeconds(60)) {
        button.isEnabled()
    }
    button.click()
}

fun CommonContainerFixture.clickActionButton(locator: Locator) {
    val button = actionButton(locator)
    waitFor(Duration.ofSeconds(60)) {
        button.isEnabled()
    }
    button.click()
}

