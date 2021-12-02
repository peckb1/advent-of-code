package me.peckb.aoc._2021

import dagger.Component
import me.peckb.aoc.InputModule
import me.peckb.aoc._2021.calendar.Day1
import javax.inject.Singleton

@Singleton
@Component(modules = [InputModule::class])
internal interface DayComponent {
  fun day1(): Day1
}