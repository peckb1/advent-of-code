package me.peckb.aoc.generators

class PermutationGenerator {
  fun <T> generatePermutations(data: Array<T>): MutableList<Array<T>> {
    return generatePermutations(data, 0, data.size - 1)
  }

  private fun <T> generatePermutations(data: Array<T>, l: Int, r: Int): MutableList<Array<T>> {
    val permutations = mutableListOf<Array<T>>()

    if (l == r) {
      permutations.add(data.clone())
    } else {
      (l..r).map { i ->
        swap(data, l, i)
        permutations.addAll(generatePermutations(data, l + 1, r))
        swap(data, l, i)
      }
    }

    return permutations
  }

  private fun <T> swap(data: Array<T>, i: Int, j: Int) {
    val t = data[i]
    data[i] = data[j]
    data[j] = t
  }
}
