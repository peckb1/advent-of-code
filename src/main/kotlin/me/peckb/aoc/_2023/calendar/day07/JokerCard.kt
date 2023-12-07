package me.peckb.aoc._2023.calendar.day07;

enum class JokerCard(val rank: Int) {
  ACE(14),
  KING(13),
  QUEEN(12),
  TEN(10),
  NINE(9),
  EIGHT(8),
  SEVEN(7),
  SIX(6),
  FIVE(5),
  FOUR(4),
  THREE(3),
  TWO(2),
  JOKER(1);

  companion object {
    fun fromSymbol(symbol: Char): JokerCard {
      return when (symbol) {
        'A' -> ACE
        'K' -> KING
        'Q' -> QUEEN
        'J' -> JOKER
        'T' -> TEN
        '9' -> NINE
        '8' -> EIGHT
        '7' -> SEVEN
        '6' -> SIX
        '5' -> FIVE
        '4' -> FOUR
        '3' -> THREE
        '2' -> TWO
        else -> throw IllegalArgumentException("Unknown card type $symbol")
      }
    }
  }
}
