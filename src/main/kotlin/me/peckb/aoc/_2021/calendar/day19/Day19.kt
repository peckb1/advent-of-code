package me.peckb.aoc._2021.calendar.day19

import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sin

class Day19 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {

  companion object {
    fun Point.rotate(xAngle: Int, yAngle: Int, zAngle: Int) : Point {
      return rotateAroundZ(zAngle, rotateAroundY(yAngle, rotateAroundX(xAngle, this)))
    }

    private fun rotateAroundX(angle: Int, point: Point): Point {
      val theta = angle * (Math.PI / 180)
      val (x, y, z) = point

      val newX = x
      val newY = (y.toDouble() * cos(theta) - z.toDouble() * sin(theta)).roundToInt()
      val newZ = (y.toDouble() * sin(theta) + z.toDouble() * cos(theta)).roundToInt()

      return Point(newX, newY, newZ).also { it.id = point.id }
    }

    private fun rotateAroundY(angle: Int, point: Point): Point {
      val (x, y, z) = point
      val theta = angle * (Math.PI / 180)

      val newX = (x.toDouble()*cos(theta) + z.toDouble()*sin(theta)).roundToInt()
      val newY = y
      val newZ = (z.toDouble()*cos(theta) - x.toDouble()*sin(theta)).roundToInt()

      return Point(newX, newY, newZ).also { it.id = point.id }
    }

    private fun rotateAroundZ(angle: Int, point: Point): Point {
      val (x, y, z) = point
      val theta = angle * (Math.PI / 180)

      val newX = (x.toDouble()*cos(theta) - y.toDouble()*sin(theta)).roundToInt()
      val newY = (x.toDouble()*sin(theta) + y.toDouble()*cos(theta)).roundToInt()
      val newZ = z

      return Point(newX, newY, newZ).also { it.id = point.id}
    }
  }

  fun partOne(fileName: String) = generatorFactory.forFile(fileName).read { input ->
   val (scannerData, translationData) = setupData(input)

    val beacons = mutableMapOf<Int, java.util.LinkedHashSet<Point>>().also {
      it.compute(0) { _, setMaybe ->
        (setMaybe ?: linkedSetOf()).also { set ->
          set.addAll(scannerData[0]!!.beaconPoints)
        }
      }
    }
    exploreAndAdd(scannerData[0]!!, scannerData, translationData, mutableSetOf(0), beacons)

    beacons[0]!!.size
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    val (scannerData, translationData) = setupData(input)

    val data = findManhattanDistances(
      scannerData[0]!!,
      scannerData,
      translationData,
      mutableSetOf(0)
    )

    val costs: MutableMap<Int, MutableMap<Int, Point>> = mutableMapOf()
    data.updateCosts(scannerData, translationData, costs)

    var maxCost = Int.MIN_VALUE
    costs[0]!!.entries.drop(1).forEach { (_, aVector) ->
      costs[0]!!.entries.drop(1).forEach { (_, bVector) ->
        if (aVector != bVector) {
          val cost = abs(aVector.x -bVector.x) + abs(aVector.y - bVector.y) + abs(aVector.z - bVector.z)
          maxCost = max(cost, maxCost)
        }
      }
    }

    maxCost
  }

  private fun setupData(input: Sequence<String>): Pair<MutableMap<Int, Scanner>, MutableMap<Int, MutableMap<Int, Triple<Int, Rotation, Int>>>> {
    val iterator = input.iterator()

    val scannerData: MutableMap<Int, Scanner> = mutableMapOf()

    while(iterator.hasNext()) {
      val scannerId = iterator.next().split(" ")[2].toInt()
      var doneWithScanner = false
      var beaconId = 0
      while(!doneWithScanner && iterator.hasNext()) {
        val pointLine = iterator.next()

        if (pointLine.isEmpty()) {
          doneWithScanner = true
        } else {
          val (x, y, z) = pointLine.split(",")
          scannerData.compute(scannerId) { _, scanner ->
            (scanner ?: Scanner(scannerId, mutableListOf())).apply {
              this.beaconPoints.add(Point(x.toInt(), y.toInt(), z.toInt()).also { it.id = beaconId })
            }
          }
        }
        beaconId++
      }
    }

    val scannerIdToBeaconReferenceViews = scannerData.mapValues { (scannerId, scanner) ->
      scanner.beaconPoints.mapIndexed { beaconIndex, point ->
        val neighbors = (0 until scanner.beaconPoints.size).mapNotNull { newNeighborBeaconIndex ->
          if (newNeighborBeaconIndex == beaconIndex) {
            null
          } else {
            val neighborBeacon = scanner.beaconPoints[newNeighborBeaconIndex]
            neighborBeacon.subtract(point)
          }
        }
        Beacon(point.id, neighbors, point.reverseDirection(scannerId))
      }
    }

    val translationData: MutableMap<Int, MutableMap<Int, Triple<Int, Rotation, Int>>> = mutableMapOf()
    val rotations = listOf(0, 90, 180, 270)
    scannerIdToBeaconReferenceViews.forEach { (myScannerId, myBeacons) ->
      scannerIdToBeaconReferenceViews.forEach scannerSearch@ { (theirScannerId, theirBeacons) ->
        myBeacons.forEach { beacon ->
          val neighbors = beacon.neighbors
          theirBeacons.forEach { neighborBeacon ->
            if (!(neighborBeacon === beacon)) {
              val s = System.currentTimeMillis()
              rotations.forEach { rotationX ->
                rotations.forEach { rotationY ->
                  rotations.forEach { rotationZ ->
                    val rotatedNeighbors = neighbors.map { it.rotate(rotationX, rotationY, rotationZ) }

                    val matchingNeighborBeacons = neighborBeacon.neighbors.intersect(rotatedNeighbors)

                    if (matchingNeighborBeacons.size >= 11) {
                      println("Found an overlap between $myScannerId and $theirScannerId")
                      val sourceReferencePoint = beacon.id
                      val rotationData = Rotation(rotationX, rotationY, rotationZ)
                      val targetReferencePoint = neighborBeacon.id

                      translationData.compute(myScannerId) { _, destination ->
                        (destination ?: mutableMapOf()).also {
                          it[theirScannerId] = Triple(sourceReferencePoint, rotationData, targetReferencePoint)
                        }
                      }
                      return@scannerSearch
                    }
                  }
                }
              }
              val e = System.currentTimeMillis()
              println("rotation time: ${e-s}")
            }
          }
        }
      }
    }

    return scannerData to translationData
  }

  data class Node(val id: Int, var parent: Node?, val children: List<Node>, var costToRoot: Point? = null) {
    fun updateCosts(
      scannerData: MutableMap<Int, Scanner>,
      translationData: MutableMap<Int, MutableMap<Int, Triple<Int, Rotation, Int>>>,
      costs: MutableMap<Int, MutableMap<Int, Point>>
    ) {
      var p = parent
      var cursorId = id
      var distance = Point(0,0,0)
      while(p != null) {
        val (sourceReferencePoint, rotationData, targetReferencePoint) = translationData[cursorId]!![p.id]!!
        val meInReferenceToBeacon = scannerData[cursorId]!!.beaconPoints[sourceReferencePoint].subtract(distance)
        val meRotated = meInReferenceToBeacon.rotate(rotationData.xRotation, rotationData.yRotation, rotationData.zRotation)
        distance = scannerData[p.id]!!.beaconPoints[targetReferencePoint].subtract(meRotated)
        cursorId = p.id
        p = p.parent
      }
      costToRoot = distance
      costs.compute(0) { _, root ->
        (root ?: mutableMapOf()).apply {
          this.compute(id) { _, myCostVector ->
            (myCostVector ?: distance)
          }
        }
      }
      children.forEach { it.updateCosts(scannerData, translationData, costs) }
    }
  }


  private fun findManhattanDistances(
    scanner: Scanner,
    scannerData: MutableMap<Int, Scanner>,
    translationData: MutableMap<Int, MutableMap<Int, Triple<Int, Rotation, Int>>>,
    exploredNodes: MutableSet<Int>,
  ): Node {

    val scannersNotExploredYet = translationData[scanner.id]!!.keys.filterNot { exploredNodes.contains(it) }

    val children = scannersNotExploredYet.map { sourceIndex ->
      exploredNodes.add(scanner.id)
      findManhattanDistances(scannerData[sourceIndex]!!, scannerData, translationData, exploredNodes).apply {  }
    }

    return Node(scanner.id ,null, children).apply {
      this.children.map { it.parent = this }
    }
  }

  private fun exploreAndAdd(
    scanner: Scanner,
    scannerData: MutableMap<Int, Scanner>,
    translationData: MutableMap<Int, MutableMap<Int, Triple<Int, Rotation, Int>>>,
    exploredNodes: MutableSet<Int>,
    beacons: MutableMap<Int, java.util.LinkedHashSet<Point>>
  ) {
    val scannersNotExploredYet = translationData[scanner.id]!!.keys.filterNot { exploredNodes.contains(it) }

    scannersNotExploredYet.forEach { scannerIndex ->
      exploredNodes.add(scannerIndex)
      exploreAndAdd(scannerData[scannerIndex]!!, scannerData, translationData, exploredNodes, beacons)

      // get instructions from my child to me
      val (sourceReferencePoint, rotationData, targetReferencePoint) = translationData[scannerIndex]!![scanner.id]!!
      val convertedBeacons = scannerData[scannerIndex]!!.beaconPoints.map { sourceBeacon ->
        val meInReferenceToBeacon = scannerData[scannerIndex]!!.beaconPoints[sourceReferencePoint].subtract(sourceBeacon)
        val meRotated = meInReferenceToBeacon.rotate(rotationData.xRotation, rotationData.yRotation, rotationData.zRotation)
        scannerData[scanner.id]!!.beaconPoints[targetReferencePoint].subtract(meRotated)
      }
      scannerData[scanner.id]!!.beaconPoints.addAll(convertedBeacons)
    }
    beacons.compute(scanner.id) { _, maybeSet ->
      (maybeSet ?: linkedSetOf()).also { it.addAll(scannerData[scanner.id]!!.beaconPoints) }
    }
  }

  data class Beacon(val id: Int, val neighbors: List<Point>, val scannerPoint: Point)

  data class Scanner(val id: Int, val beaconPoints: MutableList<Point>)

  data class Point(var x: Int, var y: Int, var z: Int) {
    var id: Int = -1

    fun reverseDirection(scannerId: Int) = Point(-x, -y, -z).also { it.id = scannerId }

    fun subtract(other: Point) = Point(x-other.x, y-other.y, z-other.z).also { it.id = id }

    fun add(other: Point) = Point(x+other.x, y+other.y, z+other.z).also { it.id = id }
  }
  
  data class Rotation(val xRotation: Int, val yRotation: Int, val zRotation: Int)
}
