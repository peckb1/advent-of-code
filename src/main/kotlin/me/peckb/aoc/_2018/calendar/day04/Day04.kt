package me.peckb.aoc._2018.calendar.day04

import me.peckb.aoc._2018.calendar.day04.Day04.Action.BeginShift
import me.peckb.aoc._2018.calendar.day04.Day04.Action.FallAsleep
import me.peckb.aoc._2018.calendar.day04.Day04.Action.WakesUp
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

typealias GuardId = Int
typealias MinuteOfTheHour = Int
typealias NumberOfTimesAsleep = Int

class Day04 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::event) { input ->
    val guardsSleepTimes = watchGuards(input)

    val sleepyGuard = guardsSleepTimes.maxByOrNull { it.value.values.sum() }!!
    val sleepyMinute = sleepyGuard.value.maxByOrNull { it.value }

    sleepyGuard.key * (sleepyMinute?.key ?: -1)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::event) { input ->
    val guardsSleepTimes = watchGuards(input)

    val sleepyGuard = guardsSleepTimes.maxByOrNull { it.value.values.maxOf { timesAsleep -> timesAsleep } }!!
    val sleepyMinute = sleepyGuard.value.maxByOrNull { it.value }

    sleepyGuard.key * (sleepyMinute?.key ?: -1)
  }

  private fun watchGuards(input: Sequence<Event>): MutableMap<GuardId, MutableMap<MinuteOfTheHour, NumberOfTimesAsleep>> {
    val guardsSleepTimes =
      mutableMapOf<GuardId, MutableMap<MinuteOfTheHour, NumberOfTimesAsleep>>()
        .withDefault { mutableMapOf() }

    var currentGuardId = -1
    var startSleepMinute = -1
    input.sortedBy { it.eventTime }.forEach { event ->
      when (event.action) {
        is BeginShift -> currentGuardId = event.action.guardId
        FallAsleep -> startSleepMinute = event.eventTime.minute
        WakesUp -> {
          (startSleepMinute until event.eventTime.minute).forEach { minute ->
            val guardSleepTimes = guardsSleepTimes.getValue(currentGuardId)
            guardSleepTimes.merge(minute, 1, Int::plus)
            guardsSleepTimes[currentGuardId] = guardSleepTimes
          }
        }
      }
    }

    return guardsSleepTimes
  }

  private fun event(line: String): Event {
    val timeString = line.substringBefore("]").drop(1)
    val eventTime = LocalDateTime.parse(timeString, formatter)

    val parts = line.split(" ")
    val action = when (parts[2]) {
      "Guard" -> BeginShift(parts[3].drop(1).toInt())
      "falls" -> FallAsleep
      "wakes" -> WakesUp
      else -> throw IllegalArgumentException("Unknown Event: $line")
    }

    return Event(eventTime, action)
  }

  data class Event(val eventTime: LocalDateTime, val action: Action)

  sealed class Action {
    data class BeginShift(val guardId: Int) : Action()
    object FallAsleep : Action()
    object WakesUp: Action()
  }

  companion object {
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
  }
}
