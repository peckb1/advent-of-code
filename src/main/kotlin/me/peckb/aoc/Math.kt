package me.peckb.aoc

object Math {
  data class CoPrimeValues(val number: Long, val remainder: Long)

  fun chineseRemainderTheorem(values: List<CoPrimeValues>): Long {
    val product = values.fold(1L) { acc, (number, _) -> acc * number }
    val productParts = values.map { it to product / it.number }
    val inverses = productParts.map { it to modularMultiplicativeInverse(it.second, it.first.number) }

    val result = inverses.fold(0L) { acc, (productPartData, inverse) ->
      val remainder = productPartData.first.remainder
      val productPart = productPartData.second

      inverse
        ?.let { remainder * productPart * it }
        ?.plus(acc)
        ?: acc
    }

    return result % product
  }

  private fun modularMultiplicativeInverse(productPart: Long, number: Long): Long? {
    var x = 1L
    while (x < number) {
      if (productPart * x % number == 1L) return x
      x++
    }
    return null
  }
}
