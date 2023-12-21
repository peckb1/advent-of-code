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
* Day 21
  * This has been one of the more enjoyable "look at input" items when determining the stopping point(s) and finding the first/last

#### 2019

* Day 07
  * This one took me a hot minute to figure out how I wanted to have the amplifiers talk to each other constantly.
* Day 09
  * Although just another operation for the Intcode computer, the self checking baked into the input is super impressive

#### 2020

* Day 10 
  * Keeping track of how many possible paths for each adapter makes finding how many steps it takes to get to an adapter it can touch nice and trivial.
* Day 16
  * Just a normal handle the data day. And the fact that each index had a unique number of possible fields (1-n) was a nice touch. 

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

#### 2022

* Day 03
  * A cute bit of set intersection
* Day 13
  * Realizing that part one was just a comparator for part two made me like the problem a lot more than I was when writing the comparator.
* Day 18
  * My first flood fill algorithm

#### 2023
* Day 01
  * Way to hard for a "normal" Day 01. Gave the year an "uh oh" vibe from the start!
* Day 08
  * This was my first "the AoC way to solve this is ..." where seeing that we had cycles and a forever brute force meant to try Least Common Multiple, which succeeded
* Day 13
  * A good difficulty problem, with a fun programming challenge, but no tricks.
* Day 21
  * A good reverse engineering problem, where you need to examine the input to understand the problem; without the reverse engineering logic being too hard to spot 

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

#### 2019

* Day 07 Using BlockingQueue(s) with my async coroutines simplified the communication between amplifiers.
* Day 14 Using a binary search on the fuel needed was a much needed speed up from a linear scan until we hit the fuel value.
* Day 24 Recursion hurt my brain, so I just spelled out every neighbor when I setup connections.

#### 2020

* Day 13 Finally had to code up the chinese remainder theorem
* Day 18 Created a generic solver that can easily include new operators with any priority for those operators
* Day 20 I enjoyed building the puzzle and then finding the sea monsters! It was fairly straight forward, just a lot of code.

#### 2021

* The double window for Day01 part 02
* The guarantee for Day08 part 02 that you can find one of [2, 3, 5] and [0, 6, 9] and the other unknowns can be ignored
* Keeping track of the single index as I parsed through the bit string for Day16 
  * This ended up turning into a re-learning of AtomicInteger, and "invisible" mutability of parameters
* Aside from the maths solution for Day 17 part1, finding the set of valid X and Y coordinates for part two was clever. 

#### 2022

* Day 09 The [-1, 0, 1] returned by `compareTo( ~ )` helped simplify the movement of a tail.
* Day 12 Reversing the Dijkstra to go from `E` to all `a` to save an order of magnitude of searching was a nice optimization.
* Day 15 Precomputing the shortest paths for every valve combination seemed key to being able to quickly go from node to node in a brute force solution.   

#### 2023
* Day 05 The fact that the numbers are "small" meant just starting at 0 and iterating over every location until we found one that matches a seed helps avoid the "forever" brute force solutions
* Day 14 Rather than trying to make sure you found the right indices for tilting rocks east, west, or south just rotate the mirror and keep tilting north
* Day 18 Learning about the Minkowski Sum for thick edge convex hull areas 

## Takeaways

* Setting up a Kotlin project with Dagger should have more examples than the multitude of Android examples out there
  * And subsequent nice looking test output for the command line.
* Coding up a better abstraction in Part one *often* leads to a much simpler part two instead of just hacking together a solution.
* Don't try to recreate Dikjstra, or A* from scratch. If you know you need to find a path, just check the pseudocode in Wikipedia
  * Similarly, check for common "weird" data structures on wikipedia, like circular lists
* Sometimes manually solving the problem can help limit your neighbor generation for pathing algorithms
* Coroutines are a good way to run slow, non pure-maths asynchronously.
