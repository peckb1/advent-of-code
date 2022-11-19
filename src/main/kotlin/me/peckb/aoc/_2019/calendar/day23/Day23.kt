package me.peckb.aoc._2019.calendar.day23

import kotlinx.coroutines.*
import me.peckb.aoc._2019.calendar.day23.Day23.OutputState.*
import me.peckb.aoc._2019.calendar.incode.IntcodeComputer
import me.peckb.aoc._2019.calendar.incode.IntcodeComputer.Companion.asMutableMap
import me.peckb.aoc._2019.calendar.incode.IntcodeComputer.Companion.operations
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.lang.Exception
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit.MILLISECONDS
import javax.inject.Inject

class Day23 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val nicCount = 50

    val computer = IntcodeComputer()
    val baseOperations = operations(input).asMutableMap()

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
                    when (val packet = lastPacket) {
                      null -> {
                        when (val nextPacket = nicInput[nicId].poll(50, MILLISECONDS)) {
                          null -> -1
                          else -> {
                            lastPacket = nextPacket
                            nextPacket.x
                          }
                        }
                      }
                      else -> packet.y.also { lastPacket = null }
                    }
                  } else {
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
                    if (nextComputer == 255) { yValueFor255 = nextY }
                    val packetToSend = Packet(nextX, nextY)
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
    val nicCount = 50

    val computer = IntcodeComputer()
    val baseOperations = operations(input).asMutableMap()

    val nicInput = Array (nicCount) { LinkedBlockingQueue<Packet>() }

    val natPackets = mutableListOf<Packet>()
    var bestPacket: Packet? = null

    try {
      runBlocking {
        val NAT = async {
          var lastNatMessagesSize: Int = -1
          var lastSentNatMessage: Packet? = null
          while (true) {
            withContext(Dispatchers.IO) { Thread.sleep(50) }
            if (lastNatMessagesSize == natPackets.size) {
              val packet = natPackets.lastOrNull()
              if (packet != null) {
                if (lastSentNatMessage != null && lastSentNatMessage.y == packet.y) {
                  bestPacket = packet
                  throw EndTime()
                }
                nicInput[0].add(packet)
                lastSentNatMessage = packet
                natPackets.clear()
              }
            }
            lastNatMessagesSize = natPackets.size
          }
        }

        val nics = (0 until nicCount).map { nicId ->
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
                  if (!idSent) {
                    nicId.toLong().also { idSent = true }
                  } else {
                    when (val packet = lastPacket) {
                      null -> {
                        when (val nextPacket = nicInput[nicId].poll(250, MILLISECONDS)) {
                          null -> -1
                          else -> {
                            lastPacket = nextPacket
                            nextPacket.x
                          }
                        }
                      }

                      else -> packet.y.also { lastPacket = null }
                    }
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
                    val packetToSend = Packet(nextX, nextY)
                    if (nextComputer == 255) {
                      natPackets.add(packetToSend)
                    } else {
                      nicInput[nextComputer].put(packetToSend)
                    }
                  }
                }
              }
            )
          }
        }

        nics.plus(NAT).awaitAll()
      }
    } catch (_: EndTime) { /* Expected */ }

    bestPacket?.y
  }

  enum class OutputState {
    COMPUTER { override fun nextState() = X_VALUE },
    X_VALUE { override fun nextState() = Y_VALUE },
    Y_VALUE { override fun nextState() = COMPUTER };

    abstract fun nextState(): OutputState
  }

  data class Packet(val x: Long, val y: Long)

  class EndTime : Exception()
}
