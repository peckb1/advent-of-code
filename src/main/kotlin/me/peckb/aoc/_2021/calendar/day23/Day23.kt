package me.peckb.aoc._2021.calendar.day23

import me.peckb.aoc._2021.calendar.day23.Day23.Movement.EnterHallFromRoom
import me.peckb.aoc._2021.calendar.day23.Day23.Movement.EnterRoomFromHall
import me.peckb.aoc._2021.calendar.day23.Day23.Movement.EnterRoomFromRoom
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.util.PriorityQueue
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Day23 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  companion object {
    private const val AMBER = 'A'
    private const val BRONZE = 'B'
    private const val COPPER = 'C'
    private const val DESERT = 'D'

    private const val AMBER_DOORWAY = 2
    private const val BRONZE_DOORWAY = 4
    private const val COPPER_DOORWAY = 6
    private const val DESERT_DOORWAY = 8

    private val OCCUPANT_DOORWAY = mapOf(
      AMBER to AMBER_DOORWAY,
      BRONZE to BRONZE_DOORWAY,
      COPPER to COPPER_DOORWAY,
      DESERT to DESERT_DOORWAY
    )

    private const val AMBER_COST = 1
    private const val BRONZE_COST = 10
    private const val COPPER_COST = 100
    private const val DESERT_COST = 1000

    private val OCCUPANT_COST = mapOf(
      AMBER to AMBER_COST,
      BRONZE to BRONZE_COST,
      COPPER to COPPER_COST,
      DESERT to DESERT_COST
    )

    private const val EMPTY = '.'
  }

  fun partOne(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    val hallway = setupInitialHallway(input.toList())

    val cheapestMoves = playGame(hallway)

    -1
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    -1
  }

  private fun playGame(hallway: Hallway): List<Movement>? {
    println(hallway)
    println()

    val moves = hallway.availableMoves()

    var cheapestMoves = listOf<Movement>()
    var cheapestCost = Int.MAX_VALUE

    while(moves.isNotEmpty()) {
      val nextMove = moves.remove()
      hallway.makeMove(nextMove)
      if (hallway.isComplete()) {
        val finishedPath = hallway.movesMade
        val finishedCost = finishedPath.sumOf { it.cost }
        if (finishedCost < cheapestCost) {
          cheapestMoves = finishedPath
          cheapestCost = finishedCost
        }
      } else {
        playGame(hallway)?.let { cheapestChildMoves ->
          val finishedCost = cheapestChildMoves.sumOf { it.cost }
          if (finishedCost < cheapestCost) {
            cheapestMoves = cheapestChildMoves
            cheapestCost = finishedCost
          }
        }
      }
      hallway.undoMove(nextMove)
    }

    return cheapestMoves.ifEmpty { null }
  }

  private fun setupInitialHallway(data: List<String>): Hallway {
    val (amberShallow, bronzeShallow, copperShallow, desertShallow) = data.toList()[2].substringAfter("###").substringBefore("###").split("#").map { it[0] }
    val (amberDeep, bronzeDeep, copperDeep, desertDeep) = data.toList()[3].split("#").drop(1).take(4).map { it.trim()[0] }

    return Hallway().apply {
      this.doorways[AMBER_DOORWAY]?.let {
        it.spaces[0] = amberShallow
        it.spaces[1] = amberDeep
      }
      this.doorways[BRONZE_DOORWAY]?.let {
        it.spaces[0] = bronzeShallow
        it.spaces[1] = bronzeDeep
      }
      this.doorways[COPPER_DOORWAY]?.let {
        it.spaces[0] = copperShallow
        it.spaces[1] = copperDeep
      }
      this.doorways[DESERT_DOORWAY]?.let {
        it.spaces[0] = desertShallow
        it.spaces[1] = desertDeep
      }
    }
  }


  sealed class Movement(val cost: Int) {
    class EnterRoomFromHall(val hallIndex: Int, val roomKey: Int, val roomIndex: Int, cost: Int) : Movement(cost)
    class EnterHallFromRoom(val roomKey: Int, val roomIndex: Int, val hallIndex: Int, cost: Int): Movement(cost)
    class EnterRoomFromRoom(val sourceRoomKey: Int, val sourceRoomIndex: Int, val destinationRoomKey: Int, val destinationRoomIndex: Int, cost: Int) : Movement(cost)
  }

  data class Hallway(
    val hall: MutableList<Char> = Array(11) { EMPTY }.toMutableList(),
    val doorways: MutableMap<Int, Room> = mutableMapOf(
      AMBER_DOORWAY to Room(AMBER),
      BRONZE_DOORWAY to Room(BRONZE),
      COPPER_DOORWAY to Room(COPPER),
      DESERT_DOORWAY to Room(DESERT)
    )
  ) {
    var movesMade = mutableListOf<Movement>()

    fun makeMove(movement: Movement) {
      when (movement) {
        is EnterHallFromRoom -> {
          hall[movement.hallIndex] = doorways[movement.roomKey]!!.spaces[movement.roomIndex]
          doorways[movement.roomKey] = doorways[movement.roomKey]!!.also { it.spaces[movement.roomIndex] = '.' }
        }
        is EnterRoomFromHall -> {
          doorways[movement.roomKey] = doorways[movement.roomKey]!!.also { it.spaces[movement.roomIndex] = hall[movement.hallIndex] }
          hall[movement.hallIndex] = '.'
        }
        is EnterRoomFromRoom -> {
          doorways[movement.destinationRoomKey]!!.spaces[movement.destinationRoomIndex] =
            doorways[movement.sourceRoomKey]!!.spaces[movement.sourceRoomIndex]
          doorways[movement.sourceRoomKey]!!.spaces[movement.sourceRoomKey] = '.'
        }
      }
      movesMade.add(movement)
    }

    fun undoMove(movement: Movement) {
      when (movement) {
        is EnterHallFromRoom -> TODO()
        is EnterRoomFromHall -> TODO()
        is EnterRoomFromRoom -> TODO()
      }
      movesMade.removeLast()
    }

    fun availableMoves(): PriorityQueue<Movement> {
      val moves = PriorityQueue(compareBy<Movement> { it.cost })

      // check anyone in the hallway, and see if they can go into their room
      hall.forEachIndexed { hallIndex, occupant ->
        if (occupant != EMPTY) {
          when (occupant) {
            AMBER -> {
              val myDoorway = doorways[AMBER_DOORWAY]
              if (myDoorway?.spaces?.get(0) == EMPTY && myDoorway.spaces[1] == EMPTY) {
                val cost = abs(hallIndex - AMBER_DOORWAY) * AMBER_COST + (2 * AMBER_COST)
                moves.add(EnterRoomFromHall(hallIndex, AMBER_DOORWAY, 1, cost))
              } else if (myDoorway?.spaces?.get(0) == EMPTY && myDoorway.spaces[1] == AMBER) {
                val cost = abs(hallIndex - AMBER_DOORWAY) * AMBER_COST + (AMBER_COST)
                moves.add(EnterRoomFromHall(hallIndex, AMBER_DOORWAY, 1, cost))
              }
            }
            BRONZE -> {
              val myDoorway = doorways[BRONZE_DOORWAY]
              if (myDoorway?.spaces?.get(0) == EMPTY && myDoorway.spaces[1] == EMPTY) {
                val cost = abs(hallIndex - BRONZE_DOORWAY) * BRONZE_COST + (2 * BRONZE_COST)
                moves.add(EnterRoomFromHall(hallIndex, BRONZE_DOORWAY, 1, cost))
              } else if (myDoorway?.spaces?.get(0) == EMPTY && myDoorway.spaces[1] == BRONZE) {
                val cost = abs(hallIndex - BRONZE_DOORWAY) * BRONZE_COST + (BRONZE_COST)
                moves.add(EnterRoomFromHall(hallIndex, BRONZE_DOORWAY, 1, cost))
              }
            }
            COPPER -> {
              val myDoorway = doorways[COPPER_DOORWAY]
              if (myDoorway?.spaces?.get(0) == EMPTY && myDoorway.spaces[1] == EMPTY) {
                val cost = abs(hallIndex - COPPER_DOORWAY) * COPPER_COST + (2 * COPPER_COST)
                moves.add(EnterRoomFromHall(hallIndex, COPPER_DOORWAY, 1, cost))
              } else if (myDoorway?.spaces?.get(0) == EMPTY && myDoorway.spaces[1] == COPPER) {
                val cost = abs(hallIndex - COPPER_DOORWAY) * COPPER_COST + (COPPER_COST)
                moves.add(EnterRoomFromHall(hallIndex, COPPER_DOORWAY, 1, cost))
              }
            }
            DESERT -> {
              val myDoorway = doorways[DESERT_DOORWAY]
              if (myDoorway?.spaces?.get(0) == EMPTY && myDoorway.spaces[1] == EMPTY) {
                val cost = abs(hallIndex - DESERT_DOORWAY) * DESERT_COST + (2 * DESERT_COST)
                moves.add(EnterRoomFromHall(hallIndex, DESERT_DOORWAY, 1, cost))
              } else if (myDoorway?.spaces?.get(0) == EMPTY && myDoorway.spaces[1] == DESERT) {
                val cost = abs(hallIndex - DESERT_DOORWAY) * DESERT_COST + (DESERT_COST)
                moves.add(EnterRoomFromHall(hallIndex, DESERT_DOORWAY, 1, cost))
              }
            }
          }
        }
      }

      // check the shallow rooms and see if anyone can go to
      //   * their destination room
      //   * the hallway
      doorways.forEach { (doorKey, room) ->
        val occupant = room.spaces[0]
        if (occupant != EMPTY) {
          // check outside the door
          if (hall[doorKey] == EMPTY) {
            moves.add(EnterHallFromRoom(doorKey, 0, doorKey, OCCUPANT_COST[occupant]!!))
          }
          // check walking left from the door
          var canKeepWalkingLeft = true
          var leftSteps = 0
          while (canKeepWalkingLeft && (doorKey - ++leftSteps) >= 0) {
            if (hall[doorKey - leftSteps] == EMPTY) {
              moves.add(EnterHallFromRoom(doorKey, 0, doorKey - leftSteps, (leftSteps + 1) * OCCUPANT_COST[occupant]!!))
            } else {
              canKeepWalkingLeft = false
            }
          }

          // check walking right form the door
          var canKeepWalkingRight = true
          var rightSteps = 0
          while (canKeepWalkingRight && (doorKey + ++rightSteps) < hall.size) {
            if (hall[doorKey + rightSteps] == EMPTY) {
              moves.add(EnterHallFromRoom(doorKey, 0, doorKey + rightSteps, (rightSteps + 1) * OCCUPANT_COST[occupant]!!))
            } else {
              canKeepWalkingRight = false
            }
          }

          val myTargetDoor = doorways[OCCUPANT_DOORWAY[occupant]!!]!!
          val hallway = min(doorKey, OCCUPANT_DOORWAY[occupant]!!)..max(doorKey, OCCUPANT_DOORWAY[occupant]!!)
          val pathClear = hall.slice(hallway).all { it == EMPTY }
          if (pathClear && myTargetDoor.spaces[0] == EMPTY && myTargetDoor.spaces[1] == EMPTY) {
            moves.add(EnterRoomFromRoom(doorKey, 0, OCCUPANT_DOORWAY[occupant]!!, 1, (hallway.count() + 2) * OCCUPANT_COST[occupant]!!))
          } else if (pathClear && myTargetDoor.spaces[0] == EMPTY && myTargetDoor.spaces[1] == occupant) {
            moves.add(EnterRoomFromRoom(doorKey, 0, OCCUPANT_DOORWAY[occupant]!!, 0, (hallway.count() + 1) * OCCUPANT_COST[occupant]!!))
          }
        }
      }

      // check the deep rooms and see if anyone can go to
      //   * their destination room
      //   * the hallway
      doorways.forEach { (doorKey, room) ->
        val occupant = room.spaces[1]
        if (occupant != EMPTY && room.spaces[0] == EMPTY) {
          // check outside the door
          if (hall[doorKey] == EMPTY) {
            moves.add(EnterHallFromRoom(doorKey, 1, doorKey, OCCUPANT_COST[occupant]!!))
          }
          // check walking left from the door
          var canKeepWalkingLeft = true
          var leftSteps = 0
          while (canKeepWalkingLeft && (doorKey - ++leftSteps) >= 0) {
            if (hall[doorKey - leftSteps] == EMPTY) {
              moves.add(EnterHallFromRoom(doorKey, 1, doorKey - leftSteps, (leftSteps + 2) * OCCUPANT_COST[occupant]!!))
            } else {
              canKeepWalkingLeft = false
            }
          }

          // check walking right form the door
          var canKeepWalkingRight = true
          var rightSteps = 0
          while (canKeepWalkingRight && (doorKey + ++rightSteps) < hall.size) {
            if (hall[doorKey + rightSteps] == EMPTY) {
              moves.add(EnterHallFromRoom(doorKey, 1, doorKey + rightSteps, (rightSteps + 2) * OCCUPANT_COST[occupant]!!))
            } else {
              canKeepWalkingRight = false
            }
          }

          val myTargetDoor = doorways[OCCUPANT_DOORWAY[occupant]!!]!!
          val hallway = min(doorKey, OCCUPANT_DOORWAY[occupant]!!)..max(doorKey, OCCUPANT_DOORWAY[occupant]!!)
          val pathClear = hall.slice(hallway).all { it == EMPTY }
          if (pathClear && myTargetDoor.spaces[0] == EMPTY && myTargetDoor.spaces[1] == EMPTY) {
            moves.add(EnterRoomFromRoom(doorKey, 0, OCCUPANT_DOORWAY[occupant]!!, 1, (hallway.count() + 3) * OCCUPANT_COST[occupant]!!))
          } else if (pathClear && myTargetDoor.spaces[0] == EMPTY && myTargetDoor.spaces[1] == occupant) {
            moves.add(EnterRoomFromRoom(doorKey, 0, OCCUPANT_DOORWAY[occupant]!!, 0, (hallway.count() + 2) * OCCUPANT_COST[occupant]!!))
          }
        }
      }

      return moves
    }

    override fun toString(): String {
      val shortFormHall = hall.toString()
        .replace("[", "")
        .replace(",", "")
        .replace("]", "")
        .replace(" ", "")
        .trim()

      val amberShallow = doorways[AMBER_DOORWAY]?.spaces?.get(0) ?: EMPTY
      val amberDeep = doorways[AMBER_DOORWAY]?.spaces?.get(1) ?: EMPTY
      val bronzeShallow = doorways[BRONZE_DOORWAY]?.spaces?.get(0) ?: EMPTY
      val bronzeDeep = doorways[BRONZE_DOORWAY]?.spaces?.get(1) ?: EMPTY
      val copperShallow = doorways[COPPER_DOORWAY]?.spaces?.get(0) ?: EMPTY
      val copperDeep = doorways[COPPER_DOORWAY]?.spaces?.get(1) ?: EMPTY
      val desertShallow = doorways[DESERT_DOORWAY]?.spaces?.get(0) ?: EMPTY
      val desertDeep = doorways[DESERT_DOORWAY]?.spaces?.get(1) ?: EMPTY

      return """
        #############
        #${shortFormHall}#
        ###$amberShallow#$bronzeShallow#$copperShallow#$desertShallow###
          #$amberDeep#$bronzeDeep#$copperDeep#$desertDeep#
          #########
      """.trimIndent()
    }

    fun isComplete(): Boolean {
      return doorways.all { (_, room) ->
        room.spaces.all { occupant ->
          occupant == room.owner
        }
      }
    }
  }

  data class Room(val owner: Char, val spaces: MutableList<Char> = Array(2) { EMPTY }.toMutableList())


}
