package me.peckb.aoc.generators

internal object CombinationsGenerator {
  // Function to print all distinct combinations of length `k`
  private fun <T> findCombinations(
    A: Array<T>, i: Int, k: Int,
    subarrays: MutableSet<List<T>>,
    out: MutableList<T>
  ) {
    // invalid input
    if (A.isEmpty() || k > A.size) {
      return
    }

    // base case: combination size is `k`
    if (k == 0) {
      subarrays.add(ArrayList(out))
      return
    }

    // start from the next index till the last index
    for (j in i until A.size) {
      // add current element `A[j]` to the solution and recur for next index
      // `j+1` with one less element `k-1`
      out.add(A[j])
      findCombinations(A, j + 1, k - 1, subarrays, out)
      out.removeAt(out.size - 1) // backtrack
    }
  }

  fun <T> findCombinations(A: Array<T>, k: Int): Set<List<T>?> {
    val subarrays: MutableSet<List<T>> = HashSet()
    findCombinations(A, 0, k, subarrays, ArrayList())
    return subarrays
  }
}