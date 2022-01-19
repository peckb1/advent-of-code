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

### Favourite problem(s):

#### 2015

* Day 14
  * A good example of when coroutines can actually make things slower
* Day 15
  * The first `lockpicking` type problem to find the max total for ingredients.
* Day 22
  * Modeling out the game was nice, and a bit more complex to solve than the similar Day 21
    
#### 2016

* Day 11
  * Doing the problem manually helped track down the limitations on allowable moves for Dijkstra
* Day 21
  * By doing the forwards steps one at a time, you have a perfect set of steps for checking your going backwards algorithm
* Day 25
  * Continuously building on the assembunny code was nice through the year 

#### 2017

* Day 07
  * Building up the tree without going over the list multiple times was a decent experiment
* Day 14
  * A proper "call the method you wrote in a previous day" build a solution

#### 2018

* Day 09
  * Implementing a new data structure ([CircularList](https://en.wikipedia.org/wiki/Linked_list#Circular_linked_list)) to solve a problem trivially is always nice learning experience
* Day 11
  * Just like Day 09, learning about new data structures! A [Summed-area table](https://en.wikipedia.org/wiki/Summed-area_table) this time.

#### 2021

* Day 07
  * A nice little bit of maths.
* Day 08
  * Having to figure out the encoding, was just fun!
* Day 12
  * This problem turned into an optimization problem after finding the solution. Starting at 2-3s search times for part two, into 200-300ms search times.
* Day 13
  * The paper folding to get messages was cool!
* Day 17
  * I miss maths sometimes, and spending the time to get the mathmatical solution for part 1 was quite nice. 

### Interesting approaches:

#### 2015

* Day 14 utilizes coroutines to calculate each reindeer's speed, and at every second. However, since each reindeer's math has no actual slow down involved, and is all just maths, using coroutines actually slows things down.
* Day 15 was the first `lockpicking` problem of AoC it seems. Where you just alter one value at a time from a base, checking that you are getting closer to your goal instead of further away.
* Day 22 Decomposing an existing problem into a known problem and solving the known. Game -> Dijkstra

#### 2016

* Day 11 Manually solving it first, made creating neighbors for Dijkstra trivial.

#### 2017

* Day 07 Building up the tree piece meal, having a secondary list of nodes that needed to be filled out as we came across them during traversal

#### 2018

* Day 09 Creating a circular linked was trivial, and simplified the problem immensely.  

#### 2021

* The double window for Day01 part 02
* The guarantee for Day08 part 02 that you can find one of [2, 3, 5] and [0, 6, 9] and the other unknowns can be ignored
* Keeping track of the single index as I parsed through the bit string for Day16 
  * This ended up turning into a re-learning of AtomicInteger, and "invisible" mutability of parameters
* Aside from the maths solution for Day 17 part1, finding the set of valid X and Y coordinates for part two was clever. 
    

## Takeaways

* Setting up a Kotlin project with Dagger should have more examples than the multitude of Android examples out there
  * And subsequent nice looking test output for the command line.
* Coding up a better abstraction in Part one *often* leads to a much simpler part two instead of just hacking together a solution.
* Don't try to recreate Dikjstra, or A* from scratch. If you know you need to find a path, just check the pseudocode in Wikipedia
  * Similarly, check for common "weird" data structures on wikipedia, like circular lists
* Sometimes manually solving the problem can help limit your neighbor generation for pathing algorithms
* Coroutines are a good way to run slow, non pure-maths asynchronously.
