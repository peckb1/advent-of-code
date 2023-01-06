package me.peckb.aoc._2020.calendar.day04

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.lang.StringBuilder

class Day04 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    readPassports(input).count { it.valid }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    readPassports(input).count { it.valid && it.dataPresent() }
  }

  private fun readPassports(input: Sequence<String>): List<Passport> {
    val passports: MutableList<Passport> = mutableListOf()

    val currentPassport = StringBuilder()
    input.forEach {
      if (it.isEmpty()) {
        passports.add(parsePassport(currentPassport.toString()))
        currentPassport.clear()
      } else {
        currentPassport.append(" $it")
      }
    }
    if (currentPassport.isNotBlank()) {
      passports.add(parsePassport(currentPassport.toString()))
    }

    return passports
  }

  private fun parsePassport(passportString: String): Passport {
    val parts = passportString.split(" ")

    var byr: String? = null // (Birth Year)
    var iyr: String? = null // (Issue Year)
    var eyr: String? = null // (Expiration Year)
    var hgt: String? = null // (Height)
    var hcl: String? = null // (Hair Color)
    var ecl: String? = null // (Eye Color)
    var pid: String? = null // (Passport ID)
    var cid: String? = null // (Country ID)

    parts.forEach {
      val data = it.split(":")
      when (data.first()) {
        "byr" -> byr = data.last()
        "iyr" -> iyr = data.last()
        "eyr" -> eyr = data.last()
        "hgt" -> hgt = data.last()
        "hcl" -> hcl = data.last()
        "ecl" -> ecl = data.last()
        "pid" -> pid = data.last()
        "cid" -> cid = data.last()
      }
    }

    return Passport(byr, iyr, eyr, hgt, hcl, ecl, pid, cid)
  }

  data class Passport(
    val byr: String?, // (Birth Year)
    val iyr: String?, // (Issue Year)
    val eyr: String?, // (Expiration Year)
    val hgt: String?, // (Height)
    val hcl: String?, // (Hair Color)
    val ecl: String?, // (Eye Color)
    val pid: String?, // (Passport ID)
    val cid: String?, // (Country ID)
  ) {
    val valid = listOfNotNull(byr, iyr, eyr, hgt, hcl, ecl, pid).size == 7

    fun dataPresent(): Boolean =
      validByr() && validIyr() && validEyr() && validHgt() && validHcl() && validEcl() && validPid()

    // byr (Birth Year) - four digits; at least 1920 and at most 2002.
    private fun validByr() = byr?.toIntOrNull()?.let { it in (1920 .. 2002) } ?: false

    // iyr (Issue Year) - four digits; at least 2010 and at most 2020.
    private fun validIyr() = iyr?.toIntOrNull()?.let { it in (2010 .. 2020) } ?: false

    // eyr (Expiration Year) - four digits; at least 2020 and at most 2030.
    private fun validEyr() = eyr?.toIntOrNull()?.let { it in (2020 .. 2030) } ?: false

    // hgt (Height) - a number followed by either cm or in:
    //                If cm, the number must be at least 150 and at most 193.
    //                If in, the number must be at least 59 and at most 76.
    private fun validHgt() = hgt?.let {
      when {
        it.endsWith("cm") -> it.dropLast(2).toIntOrNull() in (150 .. 193)
        it.endsWith("in") -> it.dropLast(2).toIntOrNull() in (59 .. 76)
        else -> false
      }
    } ?: false

    // hcl (Hair Color) - a # followed by exactly six characters 0-9 or a-f.
    //                    `^`           -> start of the string
    //                    `#`           -> raw value '#'
    //                    `[0-9a-f]{6}` -> any character 0-9 or a-f, for six sequential characters
    //                    `$`           -> end of the string
    private fun validHcl() = hcl?.matches(Regex("^#[0-9a-f]{6}$")) ?: false

    // ecl (Eye Color) - exactly one of: amb blu brn gry grn hzl oth.
    private fun validEcl() = ecl?.let { it in EYE_COLOURS } ?: false

    // pid (Passport ID) - a nine-digit number, including leading zeroes.
    private fun validPid() = pid?.let {
      it.length == 9 && it.toIntOrNull() != null
    } ?: false
  }

  companion object {
    private val EYE_COLOURS = hashSetOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth")
  }
}
