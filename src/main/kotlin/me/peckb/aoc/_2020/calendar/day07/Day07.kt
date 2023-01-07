package me.peckb.aoc._2020.calendar.day07

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day07 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::bagData) { input ->
    val bags = bags(input)

    val containerBags = mutableSetOf<Bag>()
    val bagsToInspect = mutableListOf<Bag>()

    bags[SHINY_GOLD_BAG]?.also {
      bagsToInspect.addAll(it.getParentContainers())
    }

    while(bagsToInspect.isNotEmpty()) {
      bagsToInspect.removeFirst().also { bag ->
        containerBags.add(bag)
        bagsToInspect.addAll(bag.getParentContainers())
      }
    }

    containerBags.size
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::bagData) { input ->
    val bags = bags(input)

    bags[SHINY_GOLD_BAG]?.let { bag ->
      bag.getContents().entries.sumOf { (childBag, count) ->
        count + (count * childBagCount(childBag))
      }
    }
  }

  private fun bags(input: Sequence<Pair<Bag, List<Pair<Bag, Int>>>>): MutableMap<Bag, Bag> {
    val bags = mutableMapOf<Bag, Bag>()

    input.forEach { (bag, contents) ->
      bags.putIfAbsent(bag, bag)
      contents.forEach { bags.putIfAbsent(it.first, it.first) }

      bags[bag]?.also { parentBag ->
        contents.forEach { (childBag, count) ->
          bags[childBag]?.also {
            it.containedIn(parentBag)
            parentBag.addMandatoryContent(it, count)
          }
        }
      }
    }

    return bags
  }

  private fun bagData(line: String): Pair<Bag, List<Pair<Bag, Int>>> {
    val (outer, inner) = line.dropLast(1) // remove the final `.`
      .split(" contain ")

    val outerBag = outer.split(" ").take(2).let { (style, colour) -> Bag(style, colour) }

    val innerBagData = if (inner.contains("no other bags")) {
      emptyList()
    } else {
      inner.split(", ")
        .map {  it.split(" ").take(3) }
        .map { (count, style, colour) -> Bag(style, colour) to count.toInt()}
    }

    return outerBag to innerBagData
  }

  private fun childBagCount(bag: Bag): Int {
    return bag.getContents().entries.sumOf { (childBag, count) ->
      count + (count * childBagCount(childBag))
    }
  }

  data class Bag(val style: String, val colour: String) {
    private val mandatoryContents: MutableMap<Bag, Int> = mutableMapOf()
    private val parentContainers: MutableSet<Bag> = mutableSetOf()

    fun addMandatoryContent(bag: Bag, count: Int) {
      if (mandatoryContents.containsKey(bag)) {
        throw IllegalStateException("Bag was already specified as a mandatory content.")
      }

      mandatoryContents[bag] = count
    }

    fun getContents(): Map<Bag, Int> {
      return mandatoryContents.toMap()
    }

    fun containedIn(bag: Bag) {
      parentContainers.add(bag)
    }

    fun getParentContainers(): Set<Bag> {
      return parentContainers.toSet()
    }
  }

  companion object {
    private val SHINY_GOLD_BAG = Bag("shiny", "gold")
  }
}
