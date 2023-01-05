package me.peckb.aoc._2020.calendar.day02

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day02 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::passwordData) { input ->
    input.count { (policy, password) ->
      password.count { it == policy.letter } in (policy.min .. policy.max)
    }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::passwordData) { input ->
    input.count { (policy, password) ->
      val firstMatch = password[policy.min - 1] == policy.letter
      val secondMatch = password[policy.max - 1] == policy.letter

      val eitherMatch = firstMatch || secondMatch
      val bothMatch = firstMatch && secondMatch

      eitherMatch && !bothMatch
    }
  }

  private fun passwordData(line: String): PasswordData {
    val parts = line.split(" ")

    val (min, max) = parts[0].split("-").map { it.toInt() }
    val letter = parts[1].dropLast(1)[0] // drop the ':'
    val password = parts[2]

    return PasswordData(
      passwordPolicy = PasswordPolicy(letter, min, max),
      password = password
    )
  }

  data class PasswordData(val passwordPolicy: PasswordPolicy, val password: String)

  data class PasswordPolicy(val letter: Char, val min: Int, val max: Int)
}
