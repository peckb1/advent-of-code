package me.peckb.aoc._2024

import dagger.Component
import me.peckb.aoc._2024.calendar.day02.Day02Test
import javax.inject.Singleton
import me.peckb.aoc.DayComponent
import me.peckb.aoc.InputModule
import me.peckb.aoc._2024.calendar.day01.Day01Test

@Singleton
@Component(modules = [InputModule::class])
internal interface TestDayComponent : DayComponent {
  fun inject(day01Test: Day01Test)
  fun inject(day02Test: Day02Test)
}