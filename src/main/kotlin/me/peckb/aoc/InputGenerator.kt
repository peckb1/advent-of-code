package me.peckb.aoc

import java.util.stream.Stream

interface InputGenerator<T> {
  fun getInput(filename: String): Sequence<T>
}