package me.peckb.aoc._2018.calendar.day14

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.lang.StringBuilder

class Day14 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val bestElfGuess = input.toInt()
    val chocolateRecipeOne = Node(3)
    val chocolateRecipeTwo = Node(7).apply {
      parent = chocolateRecipeOne
      child = chocolateRecipeOne
      chocolateRecipeOne.child = this
      chocolateRecipeOne.parent = this
    }

    var firstElfPointer = chocolateRecipeOne
    var secondElfPointer = chocolateRecipeTwo
    var lastElement = chocolateRecipeTwo

    var recipesMade = 2
    while(recipesMade < bestElfGuess + 10) {
      var newRecipeScore = firstElfPointer + secondElfPointer
      if (newRecipeScore >= 10) {
        val tensNode = Node(1).insertAfter(lastElement)
        lastElement = tensNode
        newRecipeScore -= 10
        recipesMade++
      }
      val onesNode = Node(newRecipeScore).insertAfter(lastElement)
      lastElement = onesNode
      recipesMade++

      repeat(firstElfPointer + 1) { firstElfPointer = firstElfPointer.child }
      repeat(secondElfPointer + 1) { secondElfPointer = secondElfPointer.child }
    }

    if (recipesMade > bestElfGuess + 10) {
      lastElement = lastElement.parent
    }

    val sb = StringBuilder()
    repeat(10) {
      sb.insert(0, lastElement.value)
      lastElement = lastElement.parent
    }
    sb.toString()
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { wantedRecipies ->
    val chocolateRecipeOne = Node(3)
    val chocolateRecipeTwo = Node(7).apply {
      parent = chocolateRecipeOne
      child = chocolateRecipeOne
      chocolateRecipeOne.child = this
      chocolateRecipeOne.parent = this
    }

    var firstElfPointer = chocolateRecipeOne
    var secondElfPointer = chocolateRecipeTwo
    var lastElement = chocolateRecipeTwo

    var foundRecipes = false
    var recipesMade = 2
    while(!foundRecipes) {
      var newRecipeScore = firstElfPointer + secondElfPointer
      if (newRecipeScore >= 10) {
        val tensNode = Node(1).insertAfter(lastElement)
        lastElement = tensNode
        newRecipeScore -= 10
        recipesMade++
        foundRecipes = checkLast(wantedRecipies, lastElement)
      }
      val onesNode = Node(newRecipeScore).insertAfter(lastElement)
      lastElement = onesNode
      recipesMade++

      repeat(firstElfPointer + 1) { firstElfPointer = firstElfPointer.child }
      repeat(secondElfPointer + 1) { secondElfPointer = secondElfPointer.child }
      foundRecipes = foundRecipes || checkLast(wantedRecipies, lastElement)
    }

    if (lastElement.value == Character.getNumericValue(wantedRecipies.last())) {
      recipesMade - wantedRecipies.length
    } else {
      recipesMade - 1 - wantedRecipies.length
    }

  }

  private fun checkLast(wantedRecipes: String, lastElement: Node): Boolean {
    var movingPointer = lastElement
    return wantedRecipes.indices.all { index ->
      movingPointer.value == Character.getNumericValue(wantedRecipes[wantedRecipes.length - 1 - index]).also {
        movingPointer = movingPointer.parent
      }
    }
  }

  data class Node(val value: Int) {
    lateinit var parent: Node
    lateinit var child: Node

    infix operator fun plus(other: Node) = value + other.value
    infix operator fun plus(other: Int) = value + other

    fun insertAfter(newParent: Node) = apply {
      parent = newParent
      child = newParent.child
      newParent.child = this
      child.parent = this
    }
  }
}
