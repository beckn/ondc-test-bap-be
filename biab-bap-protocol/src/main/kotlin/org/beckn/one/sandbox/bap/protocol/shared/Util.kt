package org.beckn.one.sandbox.bap.protocol.shared

import java.util.*

/** This class is to define all common functions or business logic which can be reused in the project */
object Util {

  private val snakeRegex = "_[a-zA-Z]".toRegex()

  /** Validate BaseUrl ends with slash or not
   *@param baseUrl String
   * @return baseUrl String
   **/
  fun getBaseUri(baseUrl: String): String {
    return if (baseUrl.endsWith("/", true)) baseUrl else "$baseUrl/"
  }

  fun String.snakeToUpperCamelCase(): String {
    return this.snakeToLowerCamelCase().capitalize()
  }

  private fun String.snakeToLowerCamelCase(): String {
    return snakeRegex.replace(this) {
      it.value.replace("_","")
        .toUpperCase()
    }
  }

}