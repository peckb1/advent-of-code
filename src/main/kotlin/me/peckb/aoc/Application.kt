package me.peckb.aoc

import me.peckb.aoc.generators.SkeletonGenerator

class Application {
  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      var year: String? = args.elementAtOrNull(0)
      while(year == null) {
        println("Enter year: e.g. 2015, 2021, 2017, etc...: ")
        year = readLine()
      }

      var day: String? = args.elementAtOrNull(1)
      while(day == null) {
        println("Enter day: e.g. 09, 23, 17, etc...: ")
        day = readLine()
      }

      var confirmed: String? = null
      if (args.elementAtOrNull(2) == "--force") {
        confirmed = "yes"
      } else {
        while (confirmed != "yes" && confirmed != "no") {
          println("Confirm generation of new Advent Of Code Skeleton for $year/$day? (yes/no)")
          confirmed = readLine()
        }
      }

      if (confirmed == "yes") {
        SkeletonGenerator(year, day).generateAdventSkeleton()
      } else {
        println("Not creating skeleton.")
      }
    }
  }
}
