package me.peckb.aoc._2015.calendar.day12

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day12 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val mapper = jacksonObjectMapper()
    countNumbers(mapper.readTree(input))
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val mapper = jacksonObjectMapper()
    countNonRedNumbers(mapper.readTree(input))
  }

  private fun countNumbers(jsonTree: JsonNode): Long {
    return when {
      jsonTree.isArray || jsonTree.isObject -> jsonTree.sumOf { countNumbers(it) }
      jsonTree.isNumber                     -> jsonTree.asLong()
      else -> 0
    }
  }

  private fun countNonRedNumbers(jsonTree: JsonNode): Long {
    return when {
      jsonTree.isArray -> (jsonTree as ArrayNode).sumOf { countNonRedNumbers(it) }
      jsonTree.isObject -> {
        val hasReds = jsonTree.any { (it as? ValueNode)?.toString() == "\"red\"" }
        if (hasReds) { 0 } else { (jsonTree as ObjectNode).sumOf { countNonRedNumbers(it) } }
      }
      jsonTree.isNumber -> jsonTree.asLong()
      else -> 0
    }
  }
}
