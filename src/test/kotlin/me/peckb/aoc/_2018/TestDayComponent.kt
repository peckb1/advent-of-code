package me.peckb.aoc._2018

import dagger.Component
import me.peckb.aoc._2018.calendar.day02.Day02Test
import me.peckb.aoc._2018.calendar.day03.Day03Test
import me.peckb.aoc._2018.calendar.day04.Day04Test
import me.peckb.aoc._2018.calendar.day05.Day05Test
import me.peckb.aoc._2018.calendar.day06.Day06Test
import me.peckb.aoc._2018.calendar.day07.Day07Test
import me.peckb.aoc._2018.calendar.day08.Day08Test
import me.peckb.aoc._2018.calendar.day09.Day09Test
import javax.inject.Singleton

import me.peckb.aoc.DayComponent
import me.peckb.aoc.InputModule
import me.peckb.aoc._2018.calendar.day01.Day01Test

@Singleton
@Component(modules = [InputModule::class])
internal interface TestDayComponent : DayComponent {
  fun inject(day01Test: Day01Test)
  fun inject(day02Test: Day02Test)
  fun inject(day03Test: Day03Test)
  fun inject(day04Test: Day04Test)
  fun inject(day05Test: Day05Test)
  fun inject(day06Test: Day06Test)
  fun inject(day07Test: Day07Test)
  fun inject(day08Test: Day08Test)
  fun inject(day09Test: Day09Test)
}
