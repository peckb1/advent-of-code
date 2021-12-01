package me.peckb.aoc._2021

import dagger.Binds
import dagger.Module
import me.peckb.aoc.InputGenerator
import me.peckb.aoc.IntInputGenerator

@Module
internal abstract class AocModule {
  @Binds abstract fun intInputGenerator(intInputGenerator: IntInputGenerator): InputGenerator<Int>
}