package me.peckb.aoc._2025.calendar.day11

import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day11 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::server) { input ->
    val serverMap = input.plus(Server("out")).associateBy { it.id }

    countAllPaths(serverMap, serverMap["you"]!!)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::server) { input ->
    val serverMap = input.plus(Server("out")).associateBy { it.id }

    countAllPaths(
      serverMap = serverMap,
      start = serverMap["svr"]!!,
      mandatoryStops = setOf(serverMap["dac"]!!, serverMap["fft"]!!)
    )
  }

  fun countAllPaths(
    serverMap: Map<String, Server>,
    start: Server,
    end: Server = serverMap["out"]!!,
    mandatoryStops: Set<Server> = emptySet(),
  ): Long {
    // Cache needs to include which mandatory stops have been visited
    val stepsCount = mutableMapOf<Pair<Server, Set<Server>>, Long>()
    // mutable state of our current path
    val currentPath = mutableSetOf<Server>()

    fun dfs(current: Server, stopsHit: Set<Server> = emptySet()): Long {
      // AoC is nice, and we don't have loops but uh ... just in case someone on reddit gets squirrelly with their data
      if (current in currentPath) return 0

      // Cache check needs to consider which mandatory stops were visited
      val cacheKey = current to stopsHit

      // if we have already found the steps for when we hit this node after already touching
      // some / all of the mandatory stops we need to hit then we can bail out
      // since we only update the cache after we have bottomed out
      stepsCount[cacheKey]?.let { return it }

      // If this is stop is a mandatory stop, we don't want to update the
      // steps passed in, so keep it in a new variable
      val updatedStopsHit = if (current in mandatoryStops) { stopsHit + current } else { stopsHit }

      val steps = when (current) {
        // Only count if we're at the end AND we've visited all required nodes!
        end if mandatoryStops.size == updatedStopsHit.size -> 1
        // At end but missing required nodes
        end -> 0
        // not at the end yet, so keep going...
        else -> {
          // update our current path so our kids know where we have been
          currentPath.add(current)

          // find out how many steps we have
          val steps = serverMap[current.id]!!.nextServers
            .map { serverMap[it]!! }
            .sumOf { dfs(it, updatedStopsHit) }

          // current path is mutable so don't forget to pop ourselves back off the stack!
          currentPath.remove(current)

          steps
        }
      }

      // Cache result with the visited required nodes
      stepsCount[cacheKey] = steps

      return steps
    }

    return dfs(start)
  }

  private fun server(line: String): Server {
    val (id, serverList) = line.split(": ")
    return Server(id, (serverList.split(" ")))
  }
}

data class Server(val id: String, val nextServers: List<String> = emptyList()) {
  override fun toString(): String = id
}
