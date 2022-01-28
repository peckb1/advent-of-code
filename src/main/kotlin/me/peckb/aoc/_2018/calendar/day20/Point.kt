package me.peckb.aoc._2018.calendar.day20

data class Point(var y: Int, var x: Int) {
  fun moveUp(): Pair<Point, Point> {
    y -= 2
    return this.copy() to this.copy(y = y + 1)
  }
  fun moveLeft(): Pair<Point, Point> {
    x -= 2
    return this.copy() to this.copy(x = x + 1)
  }
  fun moveRight(): Pair<Point, Point> {
    x += 2
    return this.copy() to this.copy(x = x - 1)
  }
  fun moveDown(): Pair<Point, Point> {
    y += 2
    return this.copy() to this.copy(y = y - 1)
  }
}
