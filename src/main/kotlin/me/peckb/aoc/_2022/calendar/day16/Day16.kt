package me.peckb.aoc._2022.calendar.day16

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import me.peckb.aoc.pathing.GenericIntDijkstra

class Day16 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::tunnels) { input ->
    val (tunnelMap, paths) = preProcess(input)
    findBestPressure("AA", 0, 30, tunnelMap, paths, emptySet())
  }

  private fun preProcess(input: Sequence<Tunnel>): Pair<MutableMap<String, Tunnel>, Map<Tunnel, MutableMap<TunnelNode, Int>>> {
    val tunnelMap = mutableMapOf<String, Tunnel>().apply {
      input.forEach { this[it.id] = it }
    }

    val solver = TunnelDijkstra()
    val paths = tunnelMap.values.associateWith {
      solver.solve(TunnelNode(it.id).usingTunnels(tunnelMap))
    }

    return tunnelMap to paths
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::tunnels) { input ->
    val (tunnelMap, paths) = preProcess(input)
    findBestPressureTwo("AA", "AA", 0, 0, 26, tunnelMap, paths, emptySet())
  }

  private fun findBestPressure(
    currentLocation: String,
    timeAtLocation: Int,
    totalTimeAllowed: Int,
    tunnelMap: MutableMap<String, Tunnel>,
    paths: Map<Tunnel, MutableMap<TunnelNode, Int>>,
    previouslyOpenedValues: Set<String>
  ): Int {
    val tunnelOptions = paths[tunnelMap[currentLocation]!!]!!.filter { (tn, _) ->
      tunnelMap[tn.tunnelId]!!.flowRate > 0 && !previouslyOpenedValues.contains(tn.tunnelId)
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
    tunnelMap: MutableMap<String, Tunnel>,
    paths: Map<Tunnel, MutableMap<TunnelNode, Int>>,
    previouslyOpenedValues: Set<String>
  ): Int {
    val myOptions = paths[tunnelMap[myLocation]!!]!!.filter { (tn, _) ->
      tunnelMap[tn.tunnelId]!!.flowRate > 0 && !previouslyOpenedValues.contains(tn.tunnelId)
    }
    val elephantOptions = paths[tunnelMap[elephantLocation]!!]!!.filter { (tn, _) ->
      tunnelMap[tn.tunnelId]!!.flowRate > 0 && !previouslyOpenedValues.contains(tn.tunnelId)
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
            // we are headed to different nodes!
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

  data class Tunnel(val id: String, val flowRate: Int, val neighboringTunnelIds: List<String>)

  data class TunnelNode(val tunnelId: String) : GenericIntDijkstra.DijkstraNode<TunnelNode> {
    lateinit var tunnelMap: Map<String, Tunnel>

    fun usingTunnels(tunnelMap: Map<String, Tunnel>) = apply { this.tunnelMap = tunnelMap }

    override fun neighbors(): Map<TunnelNode, Int> {
      return tunnelMap[tunnelId]!!
        .neighboringTunnelIds
        .map { TunnelNode(it).usingTunnels(tunnelMap) }
        .associateWith { 1 }
    }
  }

  class TunnelDijkstra : GenericIntDijkstra<TunnelNode>()

  companion object {
    private const val TIME_TO_OPEN_VALVE = 1
  }
}
