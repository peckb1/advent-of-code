package me.peckb.aoc._2019.calendar.day23

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import me.peckb.aoc._2019.calendar.day23.Day23.OutputState.*
import me.peckb.aoc._2019.calendar.incode.IntcodeComputer
import me.peckb.aoc._2019.calendar.incode.IntcodeComputer.Companion.asMutableMap
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class Day23 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val nicCount = 50

    val computer = IntcodeComputer()
    val baseOperations = IntcodeComputer.operations(input).asMutableMap()

    val nicInput = Array (nicCount) { LinkedBlockingQueue<Packet>() }

    var yValueFor255 = -1L

    try {
      runBlocking {
        (0 until nicCount).map { nicId ->
          // spin up the logic for the computers
          async {
            var currentOutputState = COMPUTER

            var nextComputer = -1
            var nextX = -1L
            var nextY = -1L

            var idSent = false
            var lastPacket: Packet? = null

            computer.runProgram(
              operations = baseOperations.toMutableMap(),
              userInput = {
                withContext(Dispatchers.IO) {
                  if (idSent) {
                    val p = lastPacket
                    if (p == null) {
                      // println("Computer $nicId waiting for input")
                      nicInput[nicId].poll(50, TimeUnit.MILLISECONDS)?.let {
                        // println("Computer $nicId received $it and sending ${it.x}")
                        lastPacket = it
                        it.x
                      } ?: run {
                        // println("Computer $nicId had no packet ready, sending default")
                        -1
                      }
                    } else {
                      p.y.also {
                        // println("Computer $nicId sending $it and resetting packet")
                        lastPacket = null
                      }
                    }
                  } else {
                    // println("Sending ID $nicId to Computer")
                    idSent = true
                    nicId.toLong()
                  }
                }
              },
              handleOutput = {
                withContext(Dispatchers.IO) {
                  when (currentOutputState) {
                    COMPUTER -> nextComputer = it.toInt()
                    X_VALUE -> nextX = it
                    Y_VALUE -> nextY = it
                  }
                  currentOutputState = currentOutputState.nextState()
                  if (currentOutputState == COMPUTER) {
                    if (nextComputer == 255) {
                      yValueFor255 = nextY
                    }
                    val packetToSend = Packet(nextX, nextY)
//                  println("Computer $nicId sending $packetToSend")
                    nicInput[nextComputer].put(packetToSend)
                  }
                }
              }
            )
          }
        }
      }
    } catch (e: ArrayIndexOutOfBoundsException) { /* expected */ }

    yValueFor255
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    -1
  }

  enum class OutputState {
    COMPUTER { override fun nextState() = X_VALUE },
    X_VALUE { override fun nextState() = Y_VALUE },
    Y_VALUE { override fun nextState() = COMPUTER };

    abstract fun nextState(): OutputState
  }

  data class Packet(val x: Long, val y: Long)
}
