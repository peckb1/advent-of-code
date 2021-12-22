package me.peckb.aoc._2015

import dagger.Component
import me.peckb.aoc.DayComponent
import me.peckb.aoc.InputModule
import me.peckb.aoc._2015.calendar.day01.Day01Test
import me.peckb.aoc._2015.calendar.day02.Day02Test
import me.peckb.aoc._2015.calendar.day03.Day03Test
import me.peckb.aoc._2015.calendar.day04.Day04Test
import javax.inject.Singleton

@Singleton
@Component(modules = [InputModule::class])
internal interface TestDayComponent : DayComponent {
  fun inject(day01Test: Day01Test)
  fun inject(day02Test: Day02Test)
  fun inject(day03Test: Day03Test)
  fun inject(day04Test: Day04Test)
}
