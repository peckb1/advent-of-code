package me.peckb.aoc

import java.io.File
import javax.inject.Inject

class IntInputGenerator @Inject constructor(): InputGenerator<Int> {
  override fun getInput(filename: String): Sequence<Int> = File(filename)
    .bufferedReader()
    .lineSequence()
    .map { it.toInt() }
}