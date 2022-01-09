package me.peckb.aoc

import dagger.Module
import dagger.Provides
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import me.peckb.aoc.generators.PermutationGenerator
import javax.inject.Singleton

@Module
internal class InputModule {
  @Provides
  @Singleton
  fun inputGeneratorFactory() = InputGeneratorFactory()

  @Provides
  @Singleton
  fun permutationGenerator() = PermutationGenerator()
}
