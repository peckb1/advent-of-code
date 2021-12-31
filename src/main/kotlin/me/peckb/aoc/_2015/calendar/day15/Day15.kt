package me.peckb.aoc._2015.calendar.day15

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject
import kotlin.math.max

class Day15 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::ingredient) { input ->
    val ingredients = input.toList()

    findHighestTotal(ingredients).first
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::ingredient) { input ->
    val ingredients = input.toList()
    val lowestCalorieIngredient = ingredients.withIndex().minByOrNull { it.value.calories }!!

    val perfectCookies: MutableList<Pair<Long, List<Int>>> = mutableListOf()
    var counts = findHighestTotal(ingredients).second
    var calorieCount = calculateCalories(counts, ingredients)

    while (calorieCount != WANTED_CALORIES) {
      val sneakies = counts.indices.flatMap { indexToSwapOut ->
        counts.indices.mapNotNull { indexToSwapIn ->
          if (indexToSwapOut != indexToSwapIn) {
            counts.mapIndexed { i, c ->
              when (i) {
                indexToSwapOut -> c - 1
                indexToSwapIn -> c + 1
                else -> c
              }
            }
          } else {
            null
          }
        }
      }

      val perfectCookieOption = sneakies.firstOrNull { calculateCalories(it, ingredients) == WANTED_CALORIES }
      perfectCookieOption?.let { perfectCookies.add(calculateTotal(it, ingredients) to it) }

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
        counts = perfectCookies.maxByOrNull { it.first }!!.second
      }
      calorieCount = calculateCalories(counts, ingredients)
    }
    calculateTotal(counts, ingredients)
  }

  private fun findHighestTotal(ingredients: List<Ingredient>) : Pair<Long, List<Int>> {
    var counts = ingredients.map { 1 }
    while (counts.sum() < INGREDIENT_TOTAL) {
      val nextIncrements = counts.indices.map {
        counts.mapIndexed { i, c -> if (i == it) c + 1 else c }
      }
      counts = nextIncrements.maxByOrNull { max(calculateTotal(it, ingredients), 0) }!!
    }

    return calculateTotal(counts, ingredients) to counts
  }

  private fun calculateTotal(counts: List<Int>, ingredients: List<Ingredient>): Long {
    val totals = counts.zip(ingredients).map { (count, ingredient) ->
      CookieTotal(
        ingredient.capacity * count,
        ingredient.durability * count,
        ingredient.flavor * count,
        ingredient.texture * count,
      )
    }

    val cookieTotal = totals.fold(CookieTotal(0, 0, 0, 0)) { acc, next -> acc + next }

    return cookieTotal.capacity * cookieTotal.durability * cookieTotal.flavor * cookieTotal.texture
  }

  private fun calculateCalories(counts: List<Int>, ingredients: List<Ingredient>): Long {
    val calories = counts.zip(ingredients).map { (count, ingredient) ->
      ingredient.calories * count
    }

    return calories.sum()
  }

  private fun ingredient(line: String): Ingredient {
    // Butterscotch: capacity -1, durability -2, flavor 6, texture 3, calories 8
    val (name, data) = line.split(": ")
    val (cap, dur, fla, tex, cal) = data.split(", ").map { it.split(" ").last() }

    return Ingredient(name, cap.toLong(), dur.toLong(), fla.toLong(), tex.toLong(), cal.toLong())
  }

  data class Ingredient(val name: String, val capacity: Long, val durability: Long, val flavor: Long, val texture: Long, val calories: Long)

  data class CookieTotal(val capacity: Long, val durability: Long, val flavor: Long, val texture: Long) {
    infix operator fun plus(other: CookieTotal) = CookieTotal(
      capacity + other.capacity,
      durability + other.durability,
      flavor + other.flavor,
      texture + other.texture,
    )
  }

  companion object {
    const val INGREDIENT_TOTAL = 100
    const val WANTED_CALORIES = 500L
  }
}
