package me.peckb.aoc._2021

import dagger.Component
import me.peckb.aoc._2021.calendar.Day01
import me.peckb.aoc._2021.calendar.Day02
import javax.inject.Singleton

@Singleton
@Component(modules = [InputModule::class])
internal interface DayComponent {
  fun day01(): Day01
  fun day02(): Day02
}
