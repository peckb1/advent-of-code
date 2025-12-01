package me.peckb.aoc._2025

import dagger.Component
import javax.inject.Singleton
import me.peckb.aoc.DayComponent
import me.peckb.aoc.InputModule
import me.peckb.aoc._2025.calendar.day01.Day01Test

@Singleton
@Component(modules = [InputModule::class])
internal interface TestDayComponent : DayComponent {
  fun inject(day01Test: Day01Test)
}
