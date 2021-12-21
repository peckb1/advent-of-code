package me.peckb.aoc._2021.calendar.day19

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention.VECTOR_OPERATOR
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder.ZYX
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt
import org.apache.commons.math3.geometry.euclidean.threed.Rotation as MathRotation

class Day19 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {

  companion object {
    private fun Int.toRad() = this * (Math.PI / 180.0)

    private val ROTATION_MATRICES = mutableMapOf<Rotation, MathRotation>().apply {
      val rs = listOf(0, 90, 180, 270).associateWith { it.toRad() }

      rs.forEach { (xDeg, xRad) ->
        rs.forEach { (yDeg, yRad) ->
          rs.forEach { (zDeg, zRad) ->
            this[Rotation(xDeg, yDeg, zDeg)] = MathRotation(ZYX, VECTOR_OPERATOR, zRad, yRad, xRad)
          }
        }
      }
    }

    fun Point.rotate(rotation: Rotation) : Point {
      val r = ROTATION_MATRICES[rotation]!!
      val v = Vector3D(x.toDouble(), y.toDouble(), z.toDouble())
      val b = r.applyTo(v)

      return Point(b.x.roundToInt(), b.y.roundToInt(), b.z.roundToInt()).also { it.id = this.id }
    }
  }

  fun partOne(fileName: String) = generatorFactory.forFile(fileName).read { input ->
   val (scannerData, translationData) = setupData(input)

    val beacons = mutableMapOf<Int, MutableSet<Point>>().also {
      it.compute(0) { _, setMaybe ->
        (setMaybe ?: mutableSetOf()).also { set ->
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

  private fun setupData(input: Sequence<String>): Pair<MutableMap<Int, Scanner>, MutableMap<Int, MutableMap<Int, Instruction>>> {
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

    val translationData: MutableMap<Int, MutableMap<Int, Instruction>> = mutableMapOf()
    val rotations = listOf(0, 90, 180, 270)
    scannerIdToBeaconReferenceViews.forEach { (myScannerId, myBeacons) ->
      (myScannerId until scannerIdToBeaconReferenceViews.size).forEach scannerSearch@ { theirScannerId ->
        val theirBeacons = scannerIdToBeaconReferenceViews[theirScannerId]!!

        if (myScannerId == theirScannerId) return@scannerSearch

        var match: Pair<Beacon, Beacon>? = null

        myBeacons.forEach search@ { beacon ->
          match?.let { (destination, source) ->
            rotations.forEach { rotationX ->
              rotations.forEach { rotationY ->
                if (overlap(translationData, source, destination, rotationX, rotationY, 0)) return@scannerSearch
              }
              if (overlap(translationData, source, destination, rotationX, 0, 90)) return@scannerSearch
              if (overlap(translationData, source, destination, rotationX, 0, 270)) return@scannerSearch
            }
          }

          theirBeacons.forEach { neighborBeacon ->
            rotations.forEach { rotationX ->
              rotations.forEach { rotationY ->
                if (overlap(translationData, beacon, neighborBeacon, rotationX, rotationY, 0)) {
                  match = beacon to neighborBeacon
                  return@search
                }
              }
              if (overlap(translationData, beacon, neighborBeacon, rotationX, 0, 90)) {
                match = beacon to neighborBeacon
                return@search
              }
              if (overlap(translationData, beacon, neighborBeacon, rotationX, 0, 270)) {
                match = beacon to neighborBeacon
                return@search
              }
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
      translationData: MutableMap<Int, MutableMap<Int, Instruction>>,
      costs: MutableMap<Int, MutableMap<Int, Point>>
    ) {
      var p = parent
      var cursorId = id
      var distance = Point(0,0,0)
      while(p != null) {
        val (sourceReferencePoint, rotationData, targetReferencePoint) = translationData[cursorId]!![p.id]!!
        val meInReferenceToBeacon = scannerData[cursorId]!!.beaconPoints[sourceReferencePoint].subtract(distance)
        val meRotated = meInReferenceToBeacon.rotate(rotationData)
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

  private fun overlap(
    translationData: MutableMap<Int, MutableMap<Int, Instruction>>,
    sourceBeacon: Beacon,
    destinationBeacon: Beacon,
    rotationX: Int,
    rotationY: Int,
    rotationZ: Int
  ): Boolean {
    val rotatedNeighbors = sourceBeacon.neighbors.map { it.rotate(Rotation(rotationX, rotationY, rotationZ)) }
    val matchingNeighborBeacons = destinationBeacon.neighbors.intersect(rotatedNeighbors)

    return if (matchingNeighborBeacons.size == 11) {
      val sourceReferencePoint = sourceBeacon.id
      val rotationData = Rotation(rotationX, rotationY, rotationZ)
      val targetReferencePoint = destinationBeacon.id

      translationData.compute(sourceBeacon.scannerPoint.id) { _, destination ->
        (destination ?: mutableMapOf()).also {
          it[destinationBeacon.scannerPoint.id] = Instruction(sourceReferencePoint, rotationData, targetReferencePoint)
        }
      }
      true
    } else {
      false
    }
  }

  private fun findManhattanDistances(
    scanner: Scanner,
    scannerData: MutableMap<Int, Scanner>,
    translationData: MutableMap<Int, MutableMap<Int, Instruction>>,
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
    scannerData: Map<Int, Scanner>,
    translationData: Map<Int, Map<Int, Instruction>>,
    exploredNodes: MutableSet<Int>,
    beacons: MutableMap<Int, MutableSet<Point>>
  ) {
    val scannersNotExploredYet = translationData[scanner.id]!!.keys.filterNot { exploredNodes.contains(it) }

    scannersNotExploredYet.forEach { scannerIndex ->
      exploredNodes.add(scannerIndex)
      exploreAndAdd(scannerData[scannerIndex]!!, scannerData, translationData, exploredNodes, beacons)

      // get instructions from my child to me
      val (sourceReferencePoint, rotationData, targetReferencePoint) = translationData[scannerIndex]!![scanner.id]!!
      val convertedBeacons = scannerData[scannerIndex]!!.beaconPoints.map { sourceBeacon ->
        val meInReferenceToBeacon = scannerData[scannerIndex]!!.beaconPoints[sourceReferencePoint].subtract(sourceBeacon)
        val meRotated = meInReferenceToBeacon.rotate(rotationData)
        scannerData[scanner.id]!!.beaconPoints[targetReferencePoint].subtract(meRotated)
      }
      scannerData[scanner.id]!!.beaconPoints.addAll(convertedBeacons)
    }
    beacons.compute(scanner.id) { _, maybeSet ->
      (maybeSet ?: mutableSetOf()).also { it.addAll(scannerData[scanner.id]!!.beaconPoints) }
    }
  }

  data class Beacon(val id: Int, val neighbors: List<Point>, val scannerPoint: Point)

  data class Scanner(val id: Int, val beaconPoints: MutableList<Point>)

  data class Point(var x: Int, var y: Int, var z: Int) {
    var id: Int = -1

    fun reverseDirection(scannerId: Int) = Point(-x, -y, -z).also { it.id = scannerId }

    fun subtract(other: Point) = Point(x-other.x, y-other.y, z-other.z).also { it.id = id }
  }

  data class Rotation(val xRotation: Int, val yRotation: Int, val zRotation: Int)

  data class Instruction(val sourceReferencePoint: Int, val rotationData: Rotation, val targetReferencePoint: Int)
}
