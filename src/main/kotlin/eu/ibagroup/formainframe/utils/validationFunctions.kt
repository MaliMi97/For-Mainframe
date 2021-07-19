package eu.ibagroup.formainframe.utils.validation

import com.intellij.openapi.ui.ValidationInfo
import eu.ibagroup.formainframe.config.connect.ConnectionConfig
import eu.ibagroup.formainframe.config.ws.WorkingSetConfig
import eu.ibagroup.formainframe.explorer.WorkingSet
import eu.ibagroup.formainframe.utils.crudable.Crudable
import eu.ibagroup.formainframe.utils.crudable.find
import javax.swing.JComponent
import javax.swing.JTextField

private val urlRegex = Regex("^(https?|http)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")

/**
 * throws an error if text is empty or consists of only white spaces
 */
fun validateForBlank(text: String, component: JComponent): ValidationInfo? {
  return if (text.isBlank()) ValidationInfo("This field must not be blank", component) else null
}

/**
 * throws an error if text of the component is empty or consists of only white spaces
 */
fun validateForBlank(component: JTextField): ValidationInfo? {
  return validateForBlank(component.text, component)
}

/**
 * throws an error if the name of ConnectionConfig already exists
 *
 * else returns null
 */
fun validateConnectionName(component: JTextField, ignoreValue: String? = null, crudable: Crudable): ValidationInfo? {
  val configAlreadyExists = crudable.find<ConnectionConfig> {
    ignoreValue != it.name && it.name == component.text.trim()
  }.count() > 0
  return if (configAlreadyExists) {
    ValidationInfo("You must provide unique connection name. Connection ${component.text} already exists.", component)
  } else {
    null
  }
}

/**
 * throws an error if the name of WorkingSetConfig already exists
 *
 * else returns null
 */
fun validateWorkingSetName(component: JTextField, ignoreValue: String? = null, crudable: Crudable): ValidationInfo? {
  val configAlreadyExists = crudable.find<WorkingSetConfig> {
    ignoreValue != it.name && it.name == component.text
  }.count() > 0
  return if (configAlreadyExists) {
    return ValidationInfo("You must provide unique working set name. Working Set ${component.text} already exists.", component)
  } else {
    null
  }

}

/**
 * throws an error if the WorkingSet already has a mask of the same name
 *
 * else returns null
 */
fun validateWorkingSetMaskName(component: JTextField, ws: WorkingSet): ValidationInfo? {
  val maskAlreadyExists = ws.dsMasks.map {it.mask}.contains(component.text)
          || ws.ussPaths.map {it.path}.contains(component.text)

  return if (maskAlreadyExists) {
    return ValidationInfo("You must provide unique mask in working set. Working Set " +
            "\"${ws.name}\" already has mask - ${component.text}", component)
  } else {
    null
  }

}

/**
 * throws an error if the url is not valid
 *
 * else returns null
 */
fun validateZosmfUrl(component: JTextField): ValidationInfo? {
  return if (!component.text.matches(urlRegex)) {
    ValidationInfo("Please provide a valid URL to z/OSMF. Example: https://myhost.com:10443", component)
  } else {
    null
  }
}

/**
 * throws an error if size of fieldName exceeds length
 *
 * else returns null
 */
fun validateFieldWithLengthRestriction(component: JTextField, length: Int, fieldName: String): ValidationInfo? {
  return if (component.text.trim().length > length) {
    ValidationInfo("$fieldName length must be not exceed $length characters.")
  } else {
    null
  }

}

/**
 * checks whether username is valid
 */
fun validateUsername(component: JTextField): ValidationInfo? {
  return validateFieldWithLengthRestriction(component, 8, "Username")
}

/**
 * checks whether password is valid
 */
fun validatePassword(component: JTextField): ValidationInfo? {
  return validateFieldWithLengthRestriction(component, 8, "Password")
}

private val maskRegex = Regex("[A-Za-z\$@#" + "0-9\\-" + "\\.\\*%]{0,46}")
private val ussPathRegex = Regex("^/|(/[^/]+)+\$")

/**
 * throws an error name of the dataset mask is not valid
 *
 * else returns null
 */
fun validateDatasetMask(text: String, component: JComponent): ValidationInfo? {
  return if (text.length > 46) {
    ValidationInfo("Dataset mask must be less than 46 characters", component)
  } else if (text.isNotBlank() && !text.matches(maskRegex)) {
    ValidationInfo("Enter valid dataset mask", component)
  } else {
    null
  }
}

/**
 * throws an error name of the uss mask is not valid
 *
 * else returns null
 */
fun validateUssMask(text: String, component: JComponent): ValidationInfo? {
  return if (text.isNotBlank() && !text.matches(ussPathRegex)) {
    ValidationInfo("Provide a valid USS path", component)
  } else {
    null
  }
}

private const val forbiddenSymbol = "/"
private const val warningSymbols = "^[^>|:& ]*$"

/**
 * throws an error name of the file name is not valid
 *
 * else returns null
 */
fun validateUssFileName(component: JTextField): ValidationInfo? {
  return if (component.text.length > 255) {
    ValidationInfo("Filename must not exceed 255 characters.", component)
  } else if (component.text.isNotBlank() && component.text.contains(forbiddenSymbol)) {
    ValidationInfo("Filename must not contain reserved '/' symbol.", component)
  } else {
    null
  }
}

private val firstSymbol = "A-Za-z\$@#"
private val remainingSymbol = firstSymbol + "0-9\\-"
private val firstGroup = "([${firstSymbol}][${remainingSymbol}]{0,7})"
private val remainingGroup = "[${remainingSymbol}]{1,8}"
private val smallErrorMessage = "First segment must be alphabetic (A to Z) or national (# @ \$)"
private val errorMessageForFullText =
  "Each name segment (qualifier) is 1 to 8 characters,\nthe first of which must be alphabetic (A to Z) or national (# @ \$).\nThe remaining seven characters are either alphabetic,\nnumeric (0 - 9), national, a hyphen (-).\nName segments are separated by a period (.)"

private val datasetNameRegex = Regex("${firstGroup}(\\.${remainingGroup})*")

/**
 * validates the name of the dataset while it is being written in a dialog window
 */
fun validateDatasetNameOnInput(component: JTextField): ValidationInfo? {
  val text = component.text.trim()
  val length = text.length
  val firstPart = text.substringBefore('.')
  return if (length > 44) {
    ValidationInfo("Dataset name cannot exceed 44 characters", component)
  } else if (component.text.isNotBlank() && !firstPart.matches(Regex(firstGroup))) {
    ValidationInfo(smallErrorMessage, component)
  } else if (!text.endsWith('.') && !text.matches(datasetNameRegex)) {
    ValidationInfo(
      errorMessageForFullText, component
    )
  } else {
    null
  }
}


private val volserRegex = Regex("[A-Za-z0-9]{1,6}")

/**
 * throws an error if the inputed volume of the dataset is not valid
 *
 * else returns null
 */
fun validateVolser(component: JTextField): ValidationInfo? {
  return if (component.text.isNotBlank() && !component.text.matches(volserRegex)) {
    ValidationInfo("Enter a valid volume serial", component)
  } else {
    null
  }
}

/**
 * throws an error if the text of the component casted to integer is either null or less or zero
 *
 * else returns null
 */
fun validateForPositiveInteger(component: JTextField): ValidationInfo? {
  return if (component.text.toIntOrNull() ?: -1 < 0) {
    ValidationInfo("Enter a positive number", component)
  } else {
    null
  }
}

private val firstLetterRegex = Regex("[A-Z@\$#a-z]")
private val memberRegex = Regex("[A-Z@$#a-z][A-Z@#\$a-z0-9]{0,7}")

/**
 * validates name of a member of a dataset
 */
fun validateMemberName(component: JTextField): ValidationInfo? {
  return if (component.text.length > 8) {
    ValidationInfo("Member name must not exceed 8 characters.", component)
  } else if (component.text.isNotEmpty() && !component.text[0].toString().matches(firstLetterRegex)) {
    ValidationInfo("Member name should start with A-Z a-z or national characters", component)
  } else if (component.text.isNotBlank() && !component.text.matches(memberRegex)) {
    ValidationInfo("Member name should contain only A-Z a-z 0-9 or national characters", component)
  }  else {
    null
  }
}




