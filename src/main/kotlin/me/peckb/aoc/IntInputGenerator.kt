package me.peckb.aoc

import java.util.stream.Stream
import javax.inject.Inject

class IntInputGenerator @Inject constructor(): InputGenerator<Int> {
  override fun getInput(): Stream<Int> = Stream.of(4, 5, 6)
}