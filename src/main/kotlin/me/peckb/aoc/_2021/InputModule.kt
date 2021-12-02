package me.peckb.aoc._2021

import dagger.Binds
import dagger.Module
import me.peckb.aoc._2021.generators.InputGenerator
import me.peckb.aoc._2021.generators.IntInputGenerator
import me.peckb.aoc._2021.generators.Path
import me.peckb.aoc._2021.generators.PathGenerator

@Module
internal abstract class InputModule {
  @Binds
  abstract fun intInputGenerator(intInputGenerator: IntInputGenerator): InputGenerator<Int>

  @Binds
  abstract fun pathInputGenerator(intInputGenerator: PathGenerator): InputGenerator<Path>
}
