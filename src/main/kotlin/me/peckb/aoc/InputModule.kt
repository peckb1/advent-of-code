package me.peckb.aoc

import dagger.Binds
import dagger.Module

@Module
internal abstract class InputModule {
  @Binds
  abstract fun intInputGenerator(intInputGenerator: IntInputGenerator): InputGenerator<Int>
}