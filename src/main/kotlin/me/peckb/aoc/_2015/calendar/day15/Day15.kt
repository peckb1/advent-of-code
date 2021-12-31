package me.peckb.aoc._2015.calendar.day15

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject
import kotlin.math.max

typealias Counts = List<Int>

class Day15 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::ingredient) { input ->
    val ingredients = input.toList()
    findHighestTotal(ingredients).first
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::ingredient) { input ->
    val ingredients = input.toList()
    val lowestCalorieIngredient = ingredients.minByOrNull { it.calories }!!

    val perfectCookieOptions: MutableList<Pair<Long, Counts>> = mutableListOf()
    var counts = findHighestTotal(ingredients).second
    var calorieCount = calculateCalories(counts, ingredients)

    // there is a VERY GOOD chance that our bes cost cookie has more calories than our wanted calories
    // the problem is actually setup just in such a way
    // so while we have not found the perfect cookie yet, keep lower the cost until we do
    while (calorieCount != WANTED_CALORIES) {
      findPerfectCookieCount(counts, ingredients)?.let {
        perfectCookieOptions.add(calculateTotal(it, ingredients) to it)
      }

      counts = if (calorieCount > WANTED_CALORIES) {
        // our calorie count is too high, so we need to swap out a high calorie count item
        // with an ingredient that has lower calories, but we only want to swap one at a time
        // finding the highest total cost each time we have to lower our calories
        lowerCalorieCount(counts, ingredients, lowestCalorieIngredient).value.second
      } else {
        // once we've gone under our wanted calories, we can look at all of the perfect cookie
        // options we found, and the perfect cookie option with the highest total is the winner
        perfectCookieOptions.maxByOrNull { it.first }!!.second
      }
      calorieCount = calculateCalories(counts, ingredients)
    }
    calculateTotal(counts, ingredients)
  }

  private fun findHighestTotal(ingredients: List<Ingredient>) : Pair<Long, Counts> {
    var counts = ingredients.map { 1 }
    while (counts.sum() < INGREDIENT_TOTAL) {
      val nextIncrements = counts.indices.map {
        counts.mapIndexed { i, c -> if (i == it) c + 1 else c }
      }
      counts = nextIncrements.maxByOrNull { max(calculateTotal(it, ingredients), 0) }!!
    }
    return calculateTotal(counts, ingredients) to counts
  }

  private fun calculateTotal(counts: Counts, ingredients: List<Ingredient>): Long {
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

  private fun calculateCalories(counts: Counts, ingredients: List<Ingredient>): Long {
    return counts.zip(ingredients).sumOf { (count, ingredient) ->
      ingredient.calories * count
    }
  }

  private fun findPerfectCookieCount(counts: Counts, ingredients: List<Ingredient>): Counts? {
    val possiblePerfectCookieCounts = counts.indices.flatMap { indexToSwapOut ->
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

    return possiblePerfectCookieCounts.firstOrNull { calculateCalories(it, ingredients) == WANTED_CALORIES }
  }

  private fun lowerCalorieCount(counts: Counts, ingredients: List<Ingredient>, lowestCalorieIngredient: Ingredient): IndexedValue<Pair<Int, Counts>> {
    // find the x highest calorie counts
    val highestCalorieIndex = ingredients.indices.filterNot { ingredients[it] == lowestCalorieIngredient }

    val possibleRemovals = highestCalorieIndex.map { index ->
      index to counts.mapIndexed { i, c -> if (i == index) c - 1 else c }
    }

    // find out which removal has the highest total cost
    // remove one from that count
    val afterRemovals = possibleRemovals.maxByOrNull {
      max(calculateTotal(it.second, ingredients), 0)
    }!!

    val ingredientRemoved = ingredients[afterRemovals.first]

    // find calorie counts less than the one we just removed
    val addBackIngredientIndices = ingredients.indices.filterNot { index ->
      ingredients[index].calories >= ingredientRemoved.calories
    }

    val possibleAddBacks = addBackIngredientIndices.map { index ->
      index to afterRemovals.second.mapIndexed { i, c -> if (i == index) c + 1 else c }
    }

    // find out which addition has the highest total cost
    // add that one back in
    return possibleAddBacks.withIndex().maxByOrNull {
      max(calculateTotal(it.value.second, ingredients), 0)
    }!!
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
