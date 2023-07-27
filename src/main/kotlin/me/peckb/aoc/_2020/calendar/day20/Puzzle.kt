package me.peckb.aoc._2020.calendar.day20

data class Puzzle(
  val tiles: Map<Int, Tile>,
  val edgeMatches: MutableMap<String, MutableList<Tile>>,
  val edges: Map<Int, List<Tile>>,
  val corners: Map<Int, List<Tile>>,
)