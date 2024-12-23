package me.peckb.aoc._2024.calendar.day23

import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day23 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::connection) { input ->
    val connections = findFriends(input)
    val triplets = mutableSetOf<Set<String>>()

    findCommonFriend(connections) { me, them, ourFriends ->
      ourFriends.forEach { ourFriend -> triplets.add(setOf(me, them, ourFriend)) }
    }

    triplets.count { group -> group.any { it.startsWith('t') } }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::connection) { input ->
    val connections = findFriends(input)
    val friends = mutableSetOf<Set<String>>()

    findCommonFriend(connections) { me, them, ourFriends ->
      if (ourFriends.isNotEmpty()) {
        val everyFriend = ourFriends.all { maybeMutual ->
          ourFriends.minus(maybeMutual).all {
            (maybeMutual in connections[it]!!)
          }
        }
        if (everyFriend) {
          friends.add(ourFriends.plus(me).plus(them))
        }
      }
    }

    friends.maxBy { it.size }.toList().sorted().joinToString(",")
  }

  private fun connection(line: String) : Pair<String, String> {
    return line.split("-").let { (a, b) -> a to b }
  }

  private fun findFriends(input: Sequence<Pair<String, String>>): MutableMap<String, Set<String>> {
    val connections = mutableMapOf<String, Set<String>>()

    input.forEach { (c1, c2) ->
      connections.merge(c1, setOf(c2)) { a, b -> a + b }
      connections.merge(c2, setOf(c1)) { a, b -> a + b }
    }

    return connections
  }

  private fun findCommonFriend(connections: Map<String, Set<String>>, handleFriends: (String, String, Set<String>) -> Unit) {
    val keys = connections.keys.toList()
    (0 until keys.size - 2).forEach { i ->
      val me = keys[i]
      val myFriends = connections[me]!!

      (i + 1 until keys.size - 1).forEach { j ->
        val them = keys[j]
        val theirFriends = connections[them]!!

        if (me in theirFriends) {
          handleFriends(me, them, myFriends.intersect(theirFriends))
        }
      }
    }
  }
}
