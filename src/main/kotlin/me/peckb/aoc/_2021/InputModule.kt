package me.peckb.aoc._2021

import dagger.Module
import dagger.Provides
import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Singleton

@Module
internal class InputModule {
  @Provides
  @Singleton
  fun inputGeneratorFactory() = InputGeneratorFactory()
}
