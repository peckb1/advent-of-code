package me.peckb.aoc._2021

import dagger.Component
import me.peckb.aoc._2021.calendar.day01.Day01
import me.peckb.aoc._2021.calendar.day02.Day02
import me.peckb.aoc._2021.calendar.day03.Day03
import javax.inject.Singleton

@Singleton
@Component(modules = [InputModule::class])
internal interface DayComponent {
  fun day01(): Day01
  fun day02(): Day02
  fun day03(): Day03
}
