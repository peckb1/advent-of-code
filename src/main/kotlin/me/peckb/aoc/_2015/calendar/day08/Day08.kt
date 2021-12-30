package me.peckb.aoc._2015.calendar.day08

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import org.apache.commons.text.StringEscapeUtils
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject

class Day08 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val stringCode = input.toList()

    val memoryCodeOne = stringCode.map { escapedString ->
      // escape hex codes
      val sb = StringBuilder(escapedString)
      val pattern: Pattern = Pattern.compile("\\\\x[0-9a-fA-F]{2}")
      val matcher: Matcher = pattern.matcher(escapedString)

      while (matcher.find()) {
        val hex = matcher.group()
        val indexOfHexCode = sb.indexOf(hex)
        val num: Int = hex.replace("\\x", "").toInt(16)
        val char = num.toChar()

        // Since the hex escaping and the non-hex escaping are meant to be performed simultaneously
        // we need to re-escape any '\' characters to be correctly unnescaped in the next step
        val str = if (char == '\\') { "$char\\" } else { "$char" }

        sb.replace(indexOfHexCode, indexOfHexCode + hex.length, str)
      }
      val hexCodeEscapeString = sb.toString()
      // then escape the java code
      StringEscapeUtils.unescapeJava(hexCodeEscapeString)
    }

    val codeSum = stringCode.sumOf { it.length }
    val memorySum = memoryCodeOne.sumOf { it.length - 2 } // take away the length of the outer "

    codeSum - memorySum
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val stringCode = input.toList()

    val memoryCode = stringCode.map { StringEscapeUtils.escapeJava(it) }

    val codeSum = stringCode.sumOf { it.length }
    val memoryFourSum = memoryCode.sumOf { it.length + 2 } // add back in the outer "

    memoryFourSum - codeSum
  }
}
