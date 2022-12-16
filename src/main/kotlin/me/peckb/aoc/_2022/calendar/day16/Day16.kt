package me.peckb.aoc._2022.calendar.day16

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import me.peckb.aoc.pathing.GenericIntDijkstra

class Day16 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::tunnels) { input ->
    val (tunnelMap, paths) = preProcess(input)
    findBestPressure(
      currentLocation = "AA",
      timeAtLocation = 0,
      totalTimeAllowed = 30,
      tunnelMap = tunnelMap,
      paths = paths,
      previouslyOpenedValues = emptySet()
    )
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::tunnels) { input ->
    val (tunnelMap, paths) = preProcess(input)
    findBestPressureTwo(
      myLocation = "AA",
      elephantLocation = "AA",
      myTimeAfterOpeningMyValve = 0,
      elephantTimeAfterOpeningTheirValve = 0,
      totalTimeAllowed = 26,
      tunnelMap = tunnelMap,
      paths = paths,
      previouslyOpenedValues = emptySet()
    )
  }

  private fun preProcess(input: Sequence<Tunnel>): Pair<Map<String, Tunnel>, Map<String, Map<Tunnel, Int>>> {
    val tunnelMap = mutableMapOf<String, Tunnel>().apply {
      input.forEach { this[it.tunnelId] = it }
    }

    val solver = TunnelDijkstra()
    val paths = tunnelMap.values.associateWith {
      solver.solve(tunnelMap[it.tunnelId]!!.usingTunnels(tunnelMap))
    }.mapKeys { it.key.tunnelId }

    return tunnelMap.filter { it.value.flowRate > 0 } to paths
  }

  private fun findBestPressure(
    currentLocation: String,
    timeAtLocation: Int,
    totalTimeAllowed: Int,
    tunnelMap: Map<String, Tunnel>,
    paths: Map<String, Map<Tunnel, Int>>,
    previouslyOpenedValues: Set<String>
  ): Int {
    val tunnelOptions = paths[currentLocation]!!.filter { (tn, _) ->
      tunnelMap.containsKey(tn.tunnelId) && !previouslyOpenedValues.contains(tn.tunnelId)
    }

    return tunnelOptions.maxOfOrNull { (valveToOpen, costToTravelToNode) ->
      val timeAtLocationAfterOpening = timeAtLocation + costToTravelToNode + TIME_TO_OPEN_VALVE
      if (timeAtLocationAfterOpening >= totalTimeAllowed) {
        0
      } else {
        val tunnel = tunnelMap[valveToOpen.tunnelId]!!
        val minutesOpen = totalTimeAllowed - timeAtLocationAfterOpening
        val totalPressureGained = minutesOpen * tunnel.flowRate

        totalPressureGained + findBestPressure(
          currentLocation = valveToOpen.tunnelId,
          timeAtLocation = timeAtLocationAfterOpening,
          totalTimeAllowed = totalTimeAllowed,
          tunnelMap = tunnelMap,
          paths = paths,
          previouslyOpenedValues = previouslyOpenedValues.plus(valveToOpen.tunnelId)
        )
      }
    } ?: 0 // in case we have no tunnel options we'd have a null max
  }

  private fun findBestPressureTwo(
    myLocation: String,
    elephantLocation: String,
    myTimeAfterOpeningMyValve: Int,
    elephantTimeAfterOpeningTheirValve: Int,
    totalTimeAllowed: Int,
    tunnelMap: Map<String, Tunnel>,
    paths: Map<String, Map<Tunnel, Int>>,
    previouslyOpenedValues: Set<String>
  ): Int {
    val myOptions = paths[myLocation]!!.filter { (tn, _) ->
      tunnelMap.containsKey(tn.tunnelId) && !previouslyOpenedValues.contains(tn.tunnelId)
    }
    val elephantOptions = paths[elephantLocation]!!.filter { (tn, _) ->
      tunnelMap.containsKey(tn.tunnelId) && !previouslyOpenedValues.contains(tn.tunnelId)
    }

    return myOptions.maxOfOrNull { (myValve, myTravelCost) ->
      val myTimeAtLocationAfterOpening = myTimeAfterOpeningMyValve + myTravelCost + TIME_TO_OPEN_VALVE
      if (myTimeAtLocationAfterOpening < totalTimeAllowed) {
        val myTunnel = tunnelMap[myValve.tunnelId]!!
        val myMinutesToOpen = totalTimeAllowed - myTimeAtLocationAfterOpening
        val myTotalPressureGained = myMinutesToOpen * myTunnel.flowRate

        myTotalPressureGained + (elephantOptions.maxOfOrNull { (elephantValve, elephantTravelCost) ->
          if (myValve == elephantValve) {
            -1
          } else {
            val elephantTimeAtLocationAfterOpening = elephantTimeAfterOpeningTheirValve + elephantTravelCost + TIME_TO_OPEN_VALVE
            if (elephantTimeAtLocationAfterOpening < totalTimeAllowed) {
              val elephantTunnel = tunnelMap[elephantValve.tunnelId]!!
              val elephantMinutesToOpen = totalTimeAllowed - elephantTimeAtLocationAfterOpening
              val elephantTotalPressureGained = elephantMinutesToOpen * elephantTunnel.flowRate

              elephantTotalPressureGained + findBestPressureTwo(
                myLocation = myValve.tunnelId,
                elephantLocation = elephantValve.tunnelId,
                myTimeAfterOpeningMyValve = myTimeAtLocationAfterOpening,
                elephantTimeAfterOpeningTheirValve = elephantTimeAtLocationAfterOpening,
                totalTimeAllowed = totalTimeAllowed,
                tunnelMap = tunnelMap,
                paths = paths,
                previouslyOpenedValues = previouslyOpenedValues.plus(myValve.tunnelId).plus(elephantValve.tunnelId),
              )
            } else { 0 } // going to and opening would take too long
          }
        } ?: 0)
      } else { 0 } // going to and opening would take too long
    } ?: 0
  }

  private fun tunnels(line: String): Tunnel {
    // Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
    val parts = line.split(" ")
    val id = parts[1]
    val flowRate = parts[4].split("=")[1].dropLast(1).toInt()
    val neighboringTunnelIds = parts.drop(9).map { it.take(2) }

    return Tunnel(id, flowRate, neighboringTunnelIds)
  }

  data class Tunnel(
    val tunnelId: String,
    val flowRate: Int,
    val neighboringTunnelIds: List<String>
  ) : GenericIntDijkstra.DijkstraNode<Tunnel> {
    lateinit var tunnelMap: Map<String, Tunnel>

    fun usingTunnels(tunnelMap: Map<String, Tunnel>) = apply { this.tunnelMap = tunnelMap }

    override fun neighbors(): Map<Tunnel, Int> {
      return tunnelMap[tunnelId]
        ?.neighboringTunnelIds
        ?.mapNotNull { tunnelMap[it]?.usingTunnels(tunnelMap) }
        ?.associateWith { 1 }
        ?: emptyMap()
    }
  }

  class TunnelDijkstra : GenericIntDijkstra<Tunnel>()

  companion object {
    private const val TIME_TO_OPEN_VALVE = 1
  }
}
