package me.peckb.aoc._2015.calendar.day16

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day16 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::aunt) { input ->
    val akitasMap = mutableMapOf<Int, MutableSet<Int>>()
    val carsMap = mutableMapOf<Int, MutableSet<Int>>()
    val catsMap = mutableMapOf<Int, MutableSet<Int>>()
    val childrenMap = mutableMapOf<Int, MutableSet<Int>>()
    val goldfishMap = mutableMapOf<Int, MutableSet<Int>>()
    val pomeraniansMap = mutableMapOf<Int, MutableSet<Int>>()
    val perfumesMap = mutableMapOf<Int, MutableSet<Int>>()
    val samoyedsMap = mutableMapOf<Int, MutableSet<Int>>()
    val treesMap = mutableMapOf<Int, MutableSet<Int>>()
    val vizslasMap = mutableMapOf<Int, MutableSet<Int>>()

    input.forEach { aunt ->
      aunt.akitas?.let { akitasMap.merge(it, mutableSetOf(aunt.number)) { a, b -> a.also { it.addAll(b) } } }
      aunt.cars?.let { carsMap.merge(it, mutableSetOf(aunt.number)) { a, b -> a.also { it.addAll(b) } } }
      aunt.cats?.let { catsMap.merge(it, mutableSetOf(aunt.number)) { a, b -> a.also { it.addAll(b) } } }
      aunt.children?.let { childrenMap.merge(it, mutableSetOf(aunt.number)) { a, b -> a.also { it.addAll(b) } } }
      aunt.goldfish?.let { goldfishMap.merge(it, mutableSetOf(aunt.number)) { a, b -> a.also { it.addAll(b) } } }
      aunt.pomeranians?.let { pomeraniansMap.merge(it, mutableSetOf(aunt.number)) { a, b -> a.also { it.addAll(b) } } }
      aunt.perfumes?.let { perfumesMap.merge(it, mutableSetOf(aunt.number)) { a, b -> a.also { it.addAll(b) } } }
      aunt.samoyeds?.let { samoyedsMap.merge(it, mutableSetOf(aunt.number)) { a, b -> a.also { it.addAll(b) } } }
      aunt.trees?.let { treesMap.merge(it, mutableSetOf(aunt.number)) { a, b -> a.also { it.addAll(b) } } }
      aunt.vizslas?.let { vizslasMap.merge(it, mutableSetOf(aunt.number)) { a, b -> a.also { it.addAll(b) } } }
    }

    val childrenAunts = childrenMap[children] ?: emptySet()
    val catsAunts = catsMap[cats] ?: emptySet()
    val samoyedsAunts = samoyedsMap[samoyeds] ?: emptySet()
    val pomeraniansAunts = pomeraniansMap[pomeranians] ?: emptySet()
    val akitasAunts = akitasMap[akitas] ?: emptySet()
    val vizslasAunts = vizslasMap[vizslas] ?: emptySet()
    val goldfishAunts = goldfishMap[goldfish] ?: emptySet()
    val treesAunts = treesMap[trees] ?: emptySet()
    val carsAunts = carsMap[cars] ?: emptySet()
    val perfumesAunts = perfumesMap[perfumes] ?: emptySet()

    val auntCounts = mutableMapOf<Int, Int>()
    childrenAunts.forEach { aunt -> auntCounts.merge(aunt, 1) { a, b -> a + b } }
    catsAunts.forEach { aunt -> auntCounts.merge(aunt, 1) { a, b -> a + b } }
    samoyedsAunts.forEach { aunt -> auntCounts.merge(aunt, 1) { a, b -> a + b } }
    pomeraniansAunts.forEach { aunt -> auntCounts.merge(aunt, 1) { a, b -> a + b } }
    akitasAunts.forEach { aunt -> auntCounts.merge(aunt, 1) { a, b -> a + b } }
    vizslasAunts.forEach { aunt -> auntCounts.merge(aunt, 1) { a, b -> a + b } }
    goldfishAunts.forEach { aunt -> auntCounts.merge(aunt, 1) { a, b -> a + b } }
    treesAunts.forEach { aunt -> auntCounts.merge(aunt, 1) { a, b -> a + b } }
    carsAunts.forEach { aunt -> auntCounts.merge(aunt, 1) { a, b -> a + b } }
    perfumesAunts.forEach { aunt -> auntCounts.merge(aunt, 1) { a, b -> a + b } }

    auntCounts.maxByOrNull { it.value }?.key
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::aunt) { input ->
    val akitasMap = mutableMapOf<Int, MutableSet<Int>>()
    val carsMap = mutableMapOf<Int, MutableSet<Int>>()
    val catsMap = mutableMapOf<Int, MutableSet<Int>>()
    val childrenMap = mutableMapOf<Int, MutableSet<Int>>()
    val goldfishMap = mutableMapOf<Int, MutableSet<Int>>()
    val pomeraniansMap = mutableMapOf<Int, MutableSet<Int>>()
    val perfumesMap = mutableMapOf<Int, MutableSet<Int>>()
    val samoyedsMap = mutableMapOf<Int, MutableSet<Int>>()
    val treesMap = mutableMapOf<Int, MutableSet<Int>>()
    val vizslasMap = mutableMapOf<Int, MutableSet<Int>>()

    input.forEach { aunt ->
      aunt.akitas?.let { akitasMap.merge(it, mutableSetOf(aunt.number)) { a, b -> a.also { it.addAll(b) } } }
      aunt.cars?.let { carsMap.merge(it, mutableSetOf(aunt.number)) { a, b -> a.also { it.addAll(b) } } }
      aunt.cats?.let { catsMap.merge(it, mutableSetOf(aunt.number)) { a, b -> a.also { it.addAll(b) } } }
      aunt.children?.let { childrenMap.merge(it, mutableSetOf(aunt.number)) { a, b -> a.also { it.addAll(b) } } }
      aunt.goldfish?.let { goldfishMap.merge(it, mutableSetOf(aunt.number)) { a, b -> a.also { it.addAll(b) } } }
      aunt.pomeranians?.let { pomeraniansMap.merge(it, mutableSetOf(aunt.number)) { a, b -> a.also { it.addAll(b) } } }
      aunt.perfumes?.let { perfumesMap.merge(it, mutableSetOf(aunt.number)) { a, b -> a.also { it.addAll(b) } } }
      aunt.samoyeds?.let { samoyedsMap.merge(it, mutableSetOf(aunt.number)) { a, b -> a.also { it.addAll(b) } } }
      aunt.trees?.let { treesMap.merge(it, mutableSetOf(aunt.number)) { a, b -> a.also { it.addAll(b) } } }
      aunt.vizslas?.let { vizslasMap.merge(it, mutableSetOf(aunt.number)) { a, b -> a.also { it.addAll(b) } } }
    }

    val childrenAunts = childrenMap[children] ?: emptySet()
    val catsAunts = catsMap.keys.mapNotNull {
      if (it > children) {
        catsMap[it]
      } else {
        null
      }
    }.toSet().flatten()
    val samoyedsAunts = samoyedsMap[samoyeds] ?: emptySet()
    val pomeraniansAunts = pomeraniansMap.keys.mapNotNull {
      if (it < pomeranians) {
        pomeraniansMap[it]
      } else {
        null
      }
    }.toSet().flatten()
    val akitasAunts = akitasMap[akitas] ?: emptySet()
    val vizslasAunts = vizslasMap[vizslas] ?: emptySet()
    val goldfishAunts = goldfishMap.keys.mapNotNull {
      if (it < pomeranians) {
        goldfishMap[it]
      } else {
        null
      }
    }.toSet().flatten()
    val treesAunts = treesMap.keys.mapNotNull {
      if (it < trees) {
        treesMap[it]
      } else {
        null
      }
    }.toSet().flatten()
    val carsAunts = carsMap[cars] ?: emptySet()
    val perfumesAunts = perfumesMap[perfumes] ?: emptySet()

    val auntCounts = mutableMapOf<Int, Int>()
    childrenAunts.forEach { aunt -> auntCounts.merge(aunt, 1) { a, b -> a + b } }
    catsAunts.forEach { aunt -> auntCounts.merge(aunt, 1) { a, b -> a + b } }
    samoyedsAunts.forEach { aunt -> auntCounts.merge(aunt, 1) { a, b -> a + b } }
    pomeraniansAunts.forEach { aunt -> auntCounts.merge(aunt, 1) { a, b -> a + b } }
    akitasAunts.forEach { aunt -> auntCounts.merge(aunt, 1) { a, b -> a + b } }
    vizslasAunts.forEach { aunt -> auntCounts.merge(aunt, 1) { a, b -> a + b } }
    goldfishAunts.forEach { aunt -> auntCounts.merge(aunt, 1) { a, b -> a + b } }
    treesAunts.forEach { aunt -> auntCounts.merge(aunt, 1) { a, b -> a + b } }
    carsAunts.forEach { aunt -> auntCounts.merge(aunt, 1) { a, b -> a + b } }
    perfumesAunts.forEach { aunt -> auntCounts.merge(aunt, 1) { a, b -> a + b } }

    auntCounts.maxByOrNull { it.value }?.key
  }

  private fun aunt(line: String): Aunt {
    // Sue 1: cars: 9, akitas: 3, goldfish: 0
    val name = line.substringBefore(": ").split(" ").last()
    val builder = Aunt.Builder(name.toInt())

    line.substringAfter("Sue $name: ").split(", ").forEach { dataItemString ->
      val (detail, value) = dataItemString.split(": ")
      when (detail) {
        "akitas" -> builder.withAkitas(value.toInt())
        "cars" -> builder.withCars(value.toInt())
        "cats" -> builder.withCats(value.toInt())
        "children" -> builder.withChildren(value.toInt())
        "goldfish" -> builder.withGoldfish(value.toInt())
        "pomeranians" -> builder.withPomeranians(value.toInt())
        "perfumes" -> builder.withPerfumes(value.toInt())
        "samoyeds" -> builder.withSamoyeds(value.toInt())
        "trees" -> builder.withTrees(value.toInt())
        "vizslas" -> builder.withVizslas(value.toInt())
      }
    }

    return builder.build()
  }

  companion object {
    private const val children = 3
    private const val cats = 7
    private const val samoyeds = 2
    private const val pomeranians = 3
    private const val akitas = 0
    private const val vizslas = 0
    private const val goldfish = 5
    private const val trees = 3
    private const val cars = 2
    private const val perfumes = 1
  }
}
