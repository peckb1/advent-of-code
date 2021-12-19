package me.peckb.aoc._2021.calendar.day19

import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

class Day19 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {

  fun partOne(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    val iterator = input.iterator()

    // val scannerData: MutableMap<Int, MutableSet<Point>> = mutableMapOf()

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
            (scanner ?: Scanner(mutableListOf())).apply {
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

    val directions: MutableMap<Int, MutableMap<Int, Point>> = mutableMapOf()

    // val sensorMappings: MutableMap<Int, MutableMap<Int, Triple<Int, Int, Triple<Int, Int, Int>>>> = mutableMapOf()
    scannerIdToBeaconReferenceViews.forEach { (myScannerId, myBeacons) ->
      // println()
      // println("Checking for an overlapping Scanner from Scanner ID: $myScannerId")
      // println()
      scannerIdToBeaconReferenceViews.forEach scannerSearch@ { (theirScannerId, theirBeacons) ->
        // val theirBeacons = scannerIdToBeaconReferenceViews[theirScannerId]!!
        myBeacons.forEach { beacon ->
          val neighbors = beacon.neighbors
          val rotations = listOf(0, 90, 180, 270)
          theirBeacons.forEach { neighborBeacon ->
            if (!(neighborBeacon === beacon)) {
              rotations.forEach { rotationX ->
                // val a = neighbors.map { rotateAroundX(rotationX, it) }
                rotations.forEach { rotationY ->
                  // val b = a.map { rotateAroundY(rotationY, it) }
                  rotations.forEach { rotationZ ->
                    // val x = b.map { rotateAroundZ(rotationZ, it) }
                    val myRotatedNeighbors = neighbors.map { p ->
                      rotateAroundZ(rotationZ, rotateAroundY(rotationY, rotateAroundX(rotationX, p)))
                    }

                    val matchingNeighborBeacons = neighborBeacon.neighbors.intersect(myRotatedNeighbors)

                    if (matchingNeighborBeacons.size >= 11) {
                      // LEGIT!
                      val theirLocationRelativeToMe = (rotateAroundZ(rotationZ, rotateAroundY(rotationY, rotateAroundX(rotationX, beacon.scannerPoint.reverseDirection(myScannerId))))).add(neighborBeacon.scannerPoint)
                      directions.compute(myScannerId) { _, directions ->
                        (directions ?: mutableMapOf()).apply {
                          this.put(theirScannerId, rotateAroundZ(rotationZ, rotateAroundY(rotationY, rotateAroundX(rotationX, theirLocationRelativeToMe))))
                        }
                      }


                      -1

                      // val theirVersionOfMatch = matchingNeighborBeacons.first()
                      // val myVersionOfMatch = myRotatedNeighbors.find {
                      //   it.x == theirVersionOfMatch.x &&
                      //   it.y == theirVersionOfMatch.y &&
                      //   it.z == theirVersionOfMatch.z
                      // }!!
                      //
                      // val myScannerToCommonPoint = beacon.scannerPoint.reverseDirection(myScannerId).add(beacon.neighbors[myVersionOfMatch.id])
                      // val theirScannerToCommonPoint = neighborBeacon.scannerPoint.reverseDirection(theirScannerId).add(neighborBeacon.neighbors[theirVersionOfMatch.id])

                      // val myScannerToCommonPoint = beacon.scannerPoint.add(myVersionOfMatch)
                      // val theirScannerToCommonPoint = neighborBeacon.scannerPoint.reverseDirection(theirScannerId).add(theirVersionOfMatch)
                      //
                      // val derotatedThem = rotateAroundX(-rotationX, rotateAroundY(-rotationY, rotateAroundZ(-rotationZ, theirScannerToCommonPoint)))
                      //
                      // val myScannerToTheirScanner = myScannerToCommonPoint.add(derotatedThem.reverseDirection(theirScannerId))

                      -1

                      // val test = neighborBeacon.neighbors.map { p ->
                      //   rotateAroundZ(-rotationZ, rotateAroundY(-rotationY, rotateAroundX(-rotationX, p)))
                      // }
                      // val test2 = scannerData[0]!!.beaconPoints

                      // sensorMappings.compute(myScannerId) { _, matches ->
                      //   (matches ?: mutableMapOf()).apply {
                      //     put(theirScannerId, Triple(beacon.id, neighborBeacon.id, Triple(rotationX, rotationY, rotationZ)))
                      //   }
                      // }
                      // println("$myScannerId $theirScannerId")

                      // val myBeacon = scannerData[myScannerId]!!.beaconPoints[beacon.id]
                      // val theirBeacon = scannerData[theirScannerId]!!.beaconPoints[neighborBeacon.id]



                      // val translationVector = neighborBeacon.scannerPoint.subtract(beacon.scannerPoint)
                      // val rotatedTranslationVector = rotateAroundX(-rotationX, rotateAroundY(-rotationY, rotateAroundZ(-rotationZ, translationVector)))
                      // val mappedPoints = scannerData[theirScannerId]!!.beaconPoints.map {
                      //   it.add(rotatedTranslationVector)
                      // }

                      // println("Scanner $myScannerId's point ${beacon.id} matches Scanner $theirScannerId's point ${neighborBeacon.id} ")
                      // println()
                      // matchingNeighborBeacons.forEach { matchingNeighbor ->
                      //   myRotatedNeighbors.forEach { myNeighbor ->
                      //     if (myNeighbor == matchingNeighbor) {
                      //       println("Scanner $myScannerId's point ${myNeighbor.id} matches Scanner $theirScannerId's point ${matchingNeighbor.id} ")
                      //       println(beacon.scannerPoint.subtract(neighborBeacon.scannerPoint))
                      //     }
                      //   }
                      // }
                      // println()
                      return@scannerSearch
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    // val beacons = mutableListOf<Point>().apply {
    //   addAll(scannerData[0]!!.beaconPoints)
    // }
    //
    // val connectedNeighbors = mutableSetOf(0)
    // val sensorZerosNeighbors = sensorMappings[0]!!
    // sensorZerosNeighbors.forEach { (connectedScanner, data) ->
    //   val (myReferenceBeacon, theirReferenceBeacon, rotationData) = data
    //
    //   val theirBeacons = scannerIdToBeaconReferenceViews[connectedScanner]!!
    //
    //   // val rotatedPoints = scannerData[0]!!.beaconPoints.map { p ->
    //   //   rotateAroundZ(rotationZ, rotateAroundY(rotationY, rotateAroundX(rotationX, p)))
    //   // }
    //   // val t = rotatedPoints.intersect(scannerData[connectedBeacon]!!.beaconPoints)
    //   -1
    // }

    // rotateAroundZ(0, rotateAroundY(180, rotateAroundX(0, scannerData[1].beaconPoints[0].subtract(directions[1][0]!!))))
    val randomScanner1Beacon = scannerData[1]!!.beaconPoints[24]
    val rotatedRandom = rotateAroundZ(0, rotateAroundY(180, rotateAroundX(0, randomScanner1Beacon.subtract(directions[1]!![0]!!))))

    -1
  }

  private fun rotateAroundX(angle: Int, point: Point): Point {
    val (x, y, z) = point
    val theta = angle * (Math.PI / 180)

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

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).readAs(::day19) { input ->
    -1
  }

  fun day19(line: String) = -4

  data class Beacon(val id: Int, val neighbors: List<Point>, val scannerPoint: Point)

  data class Scanner(val beaconPoints: MutableList<Point>)

  data class Point(val x: Int, val y: Int, val z: Int) {
    var id: Int = -1

    fun reverseDirection(scannerId: Int) = Point(-x, -y, -z).also { it.id = scannerId }

    fun subtract(other: Point) = Point(x-other.x, y-other.y, z-other.z).also { it.id = id }

    fun add(other: Point) = Point(x+other.x, y+other.y, z+other.z).also { it.id = id }
  }
}
