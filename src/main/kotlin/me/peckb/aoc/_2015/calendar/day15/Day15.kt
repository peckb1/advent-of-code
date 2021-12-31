package me.peckb.aoc._2015.calendar.day15

import arrow.core.Tuple4
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.util.Arrays
import javax.inject.Inject
import kotlin.math.max

class Day15 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::ingredient) { input ->
    val ingredients = input.toList()

    var counts = ingredients.map { 1 }
    while (counts.sum() < 100) {
      val nextIncrements = counts.indices.map {
        counts.mapIndexed { i, c -> if (i == it) c + 1 else c }
      }
      counts = nextIncrements.maxByOrNull {
        max(calculateTotal(it, ingredients), 0)
      }!!
    }

    calculateTotal(counts, ingredients)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::ingredient) { input ->
    val ingredients = input.toList()

    var counts = ingredients.map { 1 }
    while (counts.sum() < 100) {
      val nextIncrements = counts.indices.map {
        counts.mapIndexed { i, c -> if (i == it) c + 1 else c }
      }
      counts = nextIncrements.maxByOrNull {
        max(calculateTotal(it, ingredients), 0)
      }!!
    }

    var calorieCount = calculateCalories(counts, ingredients)
    val lowestCalorieIngredient = ingredients.withIndex().minByOrNull { it.value.calories }!!
    val magicValues: MutableList<Pair<Long, List<Int>>> = mutableListOf()

    while (calorieCount != 500) {
      val sneakies = counts.indices.flatMap { indexToSwapOut ->
        counts.indices.mapNotNull { indexToSwapIn ->
          if (indexToSwapOut != indexToSwapIn) {
            counts.mapIndexed { i, c ->
              if (i == indexToSwapOut) {
                c - 1
              } else if (i == indexToSwapIn) {
                c + 1
              } else {
                c
              }
            }
          } else {
            null
          }
        }
      }

      val magicValue2 = sneakies.firstOrNull {
        calculateCalories(it, ingredients) == 500
      }

      if (magicValue2 != null) {
        val magicValueTotal = calculateTotal(magicValue2, ingredients)
        magicValues.add(magicValueTotal to magicValue2)
      }


      if (calorieCount > 500) {
        // find the x highest calorie counts
        val highestCalorieIngredients =
          ingredients.withIndex().filterNot { it.value == lowestCalorieIngredient.value }

        val possibleRemovals = highestCalorieIngredients.map { iv ->
          iv.index to counts.mapIndexed { i, c ->
            if (i == iv.index) {
              c - 1
            } else {
              c
            }
          }
        }

        // find out which removal has the highest total cost
        // remove one from that count
        val afterRemovals = possibleRemovals.withIndex().maxByOrNull {
          max(calculateTotal(it.value.second, ingredients), 0)
        }!!

        val ingredientRemoved = ingredients[afterRemovals.value.first]

        // find calorie counts less than the one we just removed
        val ingredientsToAddBack =
          ingredients.withIndex().filterNot { it.value.calories >= ingredientRemoved.calories }

        val possibleAddBacks = ingredientsToAddBack.map { iv ->
          iv.index to afterRemovals.value.second.mapIndexed { i, c ->
            if (i == iv.index) {
              c + 1
            } else {
              c
            }
          }
        }

        // find out which addition has the highest total cost
        // add that one back in
        val afterAddBacks = possibleAddBacks.withIndex().maxByOrNull {
          max(calculateTotal(it.value.second, ingredients), 0)
        }!!

        counts = afterAddBacks.value.second
      } else {
        counts = magicValues.maxByOrNull { it.first }!!.second
      }
      calorieCount = calculateCalories(counts, ingredients)
    }
    calculateTotal(counts, ingredients)
  }

  private fun calculateCalories(counts: List<Int>, ingredients: List<Ingredient>): Int {
    val calories = counts.zip(ingredients).map { (count, ingredient) ->
      ingredient.calories * count
    }

    return calories.sum()
  }

  private fun calculateTotal(counts: List<Int>, ingredients: List<Ingredient>): Long {
    val tuples = counts.zip(ingredients).map { (count, ingredient) ->
      Tuple4(
        ingredient.capacity * count,
        ingredient.durability * count,
        ingredient.flavor * count,
        ingredient.texture * count
      )
    }

    val tupleSum = tuples.fold(Tuple4(0L, 0L, 0L, 0L)) { acc, next ->
      Tuple4(
        acc.first + next.first,
        acc.second + next.second,
        acc.third + next.third,
        acc.fourth + next.fourth,
      )
    }

    return tupleSum.first * tupleSum.second * tupleSum.third * tupleSum.fourth
  }

  private fun ingredient(line: String): Ingredient {
    // Butterscotch: capacity -1, durability -2, flavor 6, texture 3, calories 8
    val (name, data) = line.split(": ")
    val (cap, dur, fla, tex, cal) = data.split(", ").map { it.split(" ").last() }

    return Ingredient(name, cap.toInt(), dur.toInt(), fla.toInt(), tex.toInt(), cal.toInt())
  }

  data class Ingredient(
    val name: String,
    val capacity: Int,
    val durability: Int,
    val flavor: Int,
    val texture: Int,
    val calories: Int
  )

  private fun sum_up_recursive(numbers: ArrayList<Int>, target: Int, partial: ArrayList<Int>) {
    var s = 0
    for (x in partial) s += x
    if (s == target) println("sum(" + Arrays.toString(partial.toTypedArray()) + ")=" + target)
    if (s >= target) return
    for (i in numbers.indices) {
      val remaining = ArrayList<Int>()
      val n = numbers[i]
      for (j in i + 1 until numbers.size) remaining.add(numbers[j])
      val partial_rec = ArrayList(partial)
      partial_rec.add(n)
      sum_up_recursive(remaining, target, partial_rec)
    }
  }

  private fun sum_up(numbers: ArrayList<Int>, target: Int) {
    sum_up_recursive(numbers, target, ArrayList())
  }
}
