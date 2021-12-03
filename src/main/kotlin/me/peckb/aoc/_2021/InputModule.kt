package me.peckb.aoc._2021

import dagger.Module
import dagger.Provides
import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory

@Module
internal class InputModule {
  @Provides
  fun inputGeneratorFactory() = InputGeneratorFactory()
}
