package me.peckb.aoc._2025.calendar.day11

import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day11 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::server) { input ->
    val serverMap = input.plus(Server("out", emptyList()))
      .map { it.id to it }
      .toMap()

    findAllPaths(serverMap).size
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::server) { input ->
    val serverMap = input.plus(Server("out", emptyList()))
      .map { it.id to it }
      .toMap()

//    val paths = findAllPaths(serverMap, serverMap["svr"]!!, serverMap["fft"]!!)
//    paths.count { path ->
//      path.contains(serverMap["dac"]!!) && path.contains(serverMap["fft"]!!)
//    }

    -1
  }

  fun findAllPaths(
    serverMap: Map<String, Server>,
    start: Server = serverMap["you"]!!,
    end: Server = serverMap["out"]!!
  ): List<List<Server>> {
    val paths = mutableListOf<List<Server>>()
    val currentPath = mutableListOf<Server>()

    dfs(serverMap, start, end, currentPath, paths)

    return paths
  }

  fun dfs(
    serverMap: Map<String, Server>,
    current: Server,
    end: Server,
    currentPath: MutableList<Server>,
    paths: MutableList<List<Server>>
  ) {
    currentPath.add(current)

    if (current == end) {
      paths.add(currentPath.toList())
    } else {
      serverMap[current.id]?.nextServers?.map { serverMap[it]!!}?.forEach { next ->
        if (!currentPath.contains(next)) {
          dfs(serverMap, next, end, currentPath, paths)
        }
      }
    }

    currentPath.removeAt(currentPath.lastIndex)
  }

  private fun server(line: String): Server {
    val (id, serverList) = line.split(": ")
    return Server(id, (serverList.split(" ")))
  }
}

data class Server(val id: String, val nextServers: List<String>)
