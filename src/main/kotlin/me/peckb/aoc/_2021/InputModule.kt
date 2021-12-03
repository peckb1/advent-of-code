package me.peckb.aoc._2021

import dagger.Binds
import dagger.Module
import me.peckb.aoc._2021.generators.InputGenerator
import me.peckb.aoc._2021.generators.IntGenerator
import me.peckb.aoc._2021.generators.Path
import me.peckb.aoc._2021.generators.PathGenerator

@Module
internal abstract class InputModule {
  @Binds
  abstract fun intInputGenerator(intGenerator: IntGenerator): InputGenerator<Int>

  @Binds
  abstract fun pathInputGenerator(pathGenerator: PathGenerator): InputGenerator<Path>
}
