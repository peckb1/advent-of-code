package me.peckb.aoc._2020.calendar.day21

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day21 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::food) { input ->
    val (allergenToPossibilities, nonAllergenIngredientCounts) = generatePossibilitiesAndCounts(input)

    allergenToPossibilities.forEach { (_, ingredientPossibilityCounts) ->
      val sortedByCounts = ingredientPossibilityCounts.entries.sortedByDescending { it.value }
      val topCountValue = sortedByCounts.first().value

      val mightBe = sortedByCounts.filter { it.value == topCountValue }

      mightBe.forEach {
        nonAllergenIngredientCounts.remove(it.key)
      }
    }

    nonAllergenIngredientCounts.entries.sumOf { it.value }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::food) { input ->
    val (allergenToPossibilities, _) = generatePossibilitiesAndCounts(input)

    val probableAllergenIngredients = mutableMapOf<String, MutableMap<String, Int>>()

    allergenToPossibilities.forEach { (allergen, ingredientPossibilityCounts) ->
      val sortedByCounts = ingredientPossibilityCounts.entries.sortedByDescending { it.value }
      val topCountValue = sortedByCounts.first().value

      val mightBe= sortedByCounts.filter { it.value == topCountValue }

      probableAllergenIngredients[allergen] = mightBe.associateBy({it.key}, {it.value}).toMutableMap()
    }

    val ingredientWithAllergen = mutableMapOf<String, String>()
    while (probableAllergenIngredients.isNotEmpty()) {
      val (guaranteedValues, valuesStillUnknown) = probableAllergenIngredients.entries.partition { it.value.count() == 1 }

      guaranteedValues.forEach { (allergen, ingredientMap) ->
        val ingredient = ingredientMap.entries.first().key

        ingredientWithAllergen[allergen] = ingredient

        valuesStillUnknown.forEach { (_, ingredientPossibilities) ->
          ingredientPossibilities.remove(ingredient)
        }
      }

      guaranteedValues.forEach {
        probableAllergenIngredients.remove(it.key)
      }
    }

    ingredientWithAllergen.toSortedMap().values.joinToString(",")
  }

  private fun food(line: String): Food {
    val (ingredientsString, allergensString) = line.split(" (contains ")
    val ingredients = ingredientsString.split(" ")
    val allergens = allergensString.dropLast(1).split(", ")

    return Food(ingredients, allergens)
  }

  private fun generatePossibilitiesAndCounts(input: Sequence<Food>): FoodContents {
    val allergenToPossibilities = mutableMapOf<String, MutableMap<String, Int>>()
    val nonAllergenIngredientCounts = mutableMapOf<String, Int>()

    input.forEach { (ingredients, allergens) ->
      ingredients.forEach { i ->  nonAllergenIngredientCounts.merge(i, 1, Int::plus) }
      allergens.forEach { allergen ->
        allergenToPossibilities.merge(
          allergen,
          ingredients.associateWith { 1 }.toMutableMap()
        ) { originalMap, newMap ->
          originalMap.apply {
            newMap.forEach { (ingredient, count) ->
              this.merge(ingredient, count, Int::plus)
            }
          }
        }
      }
    }

    return FoodContents(allergenToPossibilities, nonAllergenIngredientCounts)
  }

  data class FoodContents(
    val allergenToPossibilities: MutableMap<String, MutableMap<String, Int>>,
    val nonAllergenIngredientCounts: MutableMap<String, Int>
  )

  data class Food(
    val ingredients: List<String>,
    val allergens: List<String>
  )
}
