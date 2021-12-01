package me.peckb.aoc._2021

import dagger.Component
import me.peckb.aoc._2021.calendar.Day1

@Component(modules = [AocModule::class])
internal interface DayFactory {
  fun day1(): Day1
}