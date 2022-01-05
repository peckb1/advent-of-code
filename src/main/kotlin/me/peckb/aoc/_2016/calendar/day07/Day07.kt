package me.peckb.aoc._2016.calendar.day07

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day07 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    input.count { ipv7Address ->
      val hypernetSequences = "\\[\\w*]".toRegex().findAll(ipv7Address)
      val hyperABBA = hypernetSequences.any { it.value.hasABBA() }

      !hyperABBA && ipv7Address.hasABBA()
    }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    input.count { ipv7Address ->
      val hypernetSeq = "\\[\\w*]".toRegex().findAll(ipv7Address).toList()
      val nonHypernetSeq = "(\\w*\\[|]\\w*)".toRegex().findAll(ipv7Address).toList()

      val ABAList = nonHypernetSeq.flatMap { it.value.ABAs() }
      val matchingBAB = hypernetSeq.any { it.value.hasBAB(ABAList) }

      matchingBAB
    }
  }

  private fun String.hasABBA(): Boolean {
    return windowed(4).any { it[0] != it[1] && it[0] == it[3] && it[1] == it[2] }
  }

  private fun String.ABAs(): List<String> {
    return windowed(3).filter {
      it[0] == it[2] && it[0] != it[1]
    }
  }
}

private fun String.hasBAB(abaList: List<String>) : Boolean {
  return windowed(3).any {
    it[0] == it[2] && it[0] != it[1] && abaList.contains("${it[1]}${it[0]}${it[1]}")
  }
}
