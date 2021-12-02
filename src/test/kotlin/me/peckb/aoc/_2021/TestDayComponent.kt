package me.peckb.aoc._2021

import dagger.Component
import me.peckb.aoc.InputModule
import javax.inject.Singleton

@Singleton
@Component(modules = [InputModule::class])
internal interface TestDayComponent : DayComponent {
  fun inject(day1Test: Day1Test)
}