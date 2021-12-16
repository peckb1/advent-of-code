# Advent Of Code

It's time to explore the [Advent of Code](http://adventofcode.com).

The solutions are written with the following goals:

1. **Readibility** 
   The code should be immediately understandable, even to those without knowledge of the language used. 

2. **Clean Abstractions**.
   The code should not have any extraneous copy/paste. 
   Everything should fall under the single responsibility principle.
   
   
## Running

The problems are run as unit tests, with their result matching the correct answer
as the value which passed the given day/phase combination

## Highlights

**Favourite problem(s):**

* Day 01.
  * A nice simple intro to get us started.

* Day 04.
  * After Day 03's shennanigans, this was a happy break.
    
* Day 07.
  * A nice little bit of maths.
    
* **Day 08**
  * **Having to figure out the encoding, was just fun!**
    
* Day 12
  * This problem turned into an optimization problem after finding the solution. Starting at 2-3s search times for part two, into 200-300ms search times.
    
* **Day 13**
  * **The paper folding to get messages was nice!**

**Interesting approaches:**

* The double window for Day01 part 02
* The guarantee for Day08 part 02 that you can find one of [2, 3, 5] and [0, 6, 9] and the other unknowns can be ignored
* The 
    

## Takeaways

* Setting up a Kotlin project with Dagger should have more examples than the multitude of Android examples out there
  * And subsequent nice looking test outout for the command line.
    

* Coding up a better abstraction in Part one *often* leads to a much simpler part two instead of just hacking together a solution.
  

* Don't try to recreate Dikjstra, or A* from scratch. If you know you need to find a path, just check the pseudocode in Wikipedia