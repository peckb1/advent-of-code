package me.peckb.aoc._2015

import dagger.Component
import me.peckb.aoc.DayComponent
import me.peckb.aoc.InputModule
import me.peckb.aoc._2015.calendar.day01.Day01Test
import javax.inject.Singleton

@Singleton
@Component(modules = [InputModule::class])
internal interface TestDayComponent : DayComponent {
  fun inject(day01Test: Day01Test)
}
