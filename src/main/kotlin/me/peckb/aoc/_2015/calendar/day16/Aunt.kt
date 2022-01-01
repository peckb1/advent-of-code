package me.peckb.aoc._2015.calendar.day16

class Aunt private constructor(
  // our aunts "name"
  val number: Int,
  // the information we remember
  val akitas: Int? = null,
  val cars: Int? = null,
  val cats: Int? = null,
  val children: Int? = null,
  val goldfish: Int? = null,
  val pomeranians: Int? = null,
  val perfumes: Int? = null,
  val samoyeds: Int? = null,
  val trees: Int? = null,
  val vizslas: Int? = null
) {
  class Builder(private val number: Int) {
    private var akitas: Int? = null
    private var cars: Int? = null
    private var cats: Int? = null
    private var children: Int? = null
    private var goldfish: Int? = null
    private var pomeranians: Int? = null
    private var perfumes: Int? = null
    private var samoyeds: Int? = null
    private var trees: Int? = null
    private var vizslas: Int? = null

    fun withAkitas(akitas: Int): Builder = apply { this.akitas = akitas }
    fun withCars(cars: Int): Builder = apply { this.cars = cars }
    fun withCats(cats: Int): Builder = apply { this.cats = cats }
    fun withChildren(children: Int): Builder = apply { this.children = children }
    fun withGoldfish(goldfish: Int): Builder = apply { this.goldfish = goldfish }
    fun withPomeranians(pomeranians: Int): Builder = apply { this.pomeranians = pomeranians }
    fun withPerfumes(perfumes: Int): Builder = apply { this.perfumes = perfumes }
    fun withSamoyeds(samoyeds: Int): Builder = apply { this.samoyeds = samoyeds }
    fun withTrees(trees: Int): Builder = apply { this.trees = trees }
    fun withVizslas(vizslas: Int): Builder = apply { this.vizslas = vizslas }

    fun build(): Aunt = Aunt(number, akitas, cars, cats, children, goldfish, pomeranians, perfumes, samoyeds, trees, vizslas)
  }
}