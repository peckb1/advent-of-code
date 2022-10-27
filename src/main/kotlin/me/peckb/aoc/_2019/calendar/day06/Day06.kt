package me.peckb.aoc._2019.calendar.day06

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day06 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::orbitData) { input ->
    val celestialBodies = mutableMapOf<String, CelestialBody>()

    input.forEach { (orbitee, orbiter) ->
      val orbiteeBody = celestialBodies.getOrPut(orbitee) { CelestialBody(orbitee) }
      val orbiterBody = celestialBodies.getOrPut(orbiter) { CelestialBody(orbiter) }

      orbiteeBody.addSatellite(orbiterBody)
    }

    // setup every body's depth
    celestialBodies[CENTER_OF_MASS_IDENTIFIER]?.setDepth(0)

    // find the total
    celestialBodies.values.sumOf { it.getDepth() }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::orbitData) { input ->
    val celestialBodies = mutableMapOf<String, CelestialBody>()

    input.forEach { (orbitee, orbiter) ->
      val orbiteeBody = celestialBodies.getOrPut(orbitee) { CelestialBody(orbitee) }
      val orbiterBody = celestialBodies.getOrPut(orbiter) { CelestialBody(orbiter) }

      orbiterBody.setParent(orbiteeBody)
      orbiteeBody.addSatellite(orbiterBody)
    }

    val com = celestialBodies[CENTER_OF_MASS_IDENTIFIER]!!
    val santa = celestialBodies[SANTA_IDENTIFIER]!!
    val you = celestialBodies[YOU_IDENTIFIER]!!

    // find the chain of SAN -> COM
    val santaChain = LinkedHashSet<CelestialBody>()
    var santaParent = santa
    while(santaParent != com) {
      santaChain.add(santaParent)
      santaParent = santaParent.getParent()
    }

    // the root body SAN and YOU first share
    var commonRootBody = you.getParent()
    // the number of hops to that common body
    var hopsToYouParent = 0
    // find the two above values
    while(!santaChain.contains(commonRootBody)) {
      commonRootBody = commonRootBody.getParent()
      hopsToYouParent += 1
    }

    // how many hops from santa to that common body
    val hopsToSantaParent = santaChain.indexOf(commonRootBody)

    // the - 1 is we actually want to hop to Santa's parent, not santa
    hopsToSantaParent + hopsToYouParent -1
  }

  private fun orbitData(line: String) = line.split(")").let { (orbitee, orbiter) ->
    OrbitData(orbitee, orbiter)
  }

  data class OrbitData(val orbitee: String, val orbiter: String)

  data class CelestialBody(private val identifier: String) {
    private var depth: Int = 0
    private lateinit var parentBody: CelestialBody

    private val satellites = mutableListOf<CelestialBody>()

    fun addSatellite(satellite: CelestialBody) = satellites.add(satellite)

    fun setDepth(depth: Int) {
      this.depth = depth
      satellites.forEach { it.setDepth(depth + 1) }
    }

    fun getDepth() = this.depth

    fun setParent(celestialBody: CelestialBody) {
      parentBody = celestialBody
    }

    fun getParent() = this.parentBody
  }

  companion object {
    const val CENTER_OF_MASS_IDENTIFIER = "COM"
    const val SANTA_IDENTIFIER = "SAN"
    const val YOU_IDENTIFIER = "YOU"
  }
}
