package me.peckb.aoc._2021.calendar.day19

import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

class Day19 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {

  companion object {
    val inverseAngles = mapOf<Int, Int>(
      0 to 0, 90 to 270, 180 to 180, 270 to 90
    )
  }


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

    // val directions: MutableMap<Int, MutableMap<Int, Pair<Triple<Int, Int, Int>, Point>>> = mutableMapOf()

    val translationData: MutableMap<Int, MutableMap<Int, Triple<Int, Triple<Int, Int, Int>, Int>>> = mutableMapOf()

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
                      val sourceReferencePoint = beacon.id
                      val rotationData = Triple(rotationX, rotationY, rotationZ)
                      val targetReferencePoint = neighborBeacon.id
                      // LEGIT!
                      translationData.compute(myScannerId) { _, destination ->
                        (destination ?: mutableMapOf()).also {
                          it[theirScannerId] = Triple(sourceReferencePoint, rotationData, targetReferencePoint)
                        }
                      }


                      // val sourceBeacon = scannerData[0]!!.beaconPoints[0]
                      // scannerData[0]!!.beaconPoints.forEach { sourceBeacon ->
                      //   val sourceReferencePoint = beacon.id
                      //   val rotationData = Triple(rotationX, rotationY, rotationZ)
                      //   val targetReferencePoint = neighborBeacon.id
                      //
                      //   val meInReferenceToBeacon = scannerData[0]!!.beaconPoints[sourceReferencePoint].subtract(sourceBeacon)
                      //   val meRotated = rotateAroundZ(rotationData.third,
                      //     rotateAroundY(rotationData.second,
                      //       rotateAroundX(rotationData.first,
                      //         meInReferenceToBeacon
                      //       )
                      //     )
                      //   )
                      //   val targetBeacon = scannerData[1]!!.beaconPoints[targetReferencePoint].subtract(meRotated)
                      //   -1
                      // }



                      -1
                      // val theirLocationRelativeToMeOriginal =
                      //   (rotateAroundZ(rotationZ, rotateAroundY(rotationY, rotateAroundX(rotationX, (rotateAroundX(rotationX, rotateAroundY(rotationY, rotateAroundZ(rotationZ, beacon.scannerPoint.reverseDirection(myScannerId))))).add(neighborBeacon.scannerPoint)))))
                      // val theirLocationRelativeToMeNew =  (
                      //   rotateAroundX((rotationX + 180) % 360,
                      //     rotateAroundY((rotationY + 180) % 360,
                      //       rotateAroundZ((rotationZ + 180) % 360,
                      //         rotateAroundZ(rotationZ,
                      //           rotateAroundY(rotationY,
                      //             rotateAroundX(rotationX,
                      //               beacon.scannerPoint.reverseDirection(myScannerId)
                      //             )
                      //           )
                      //         ).add(neighborBeacon.scannerPoint)
                      //       )
                      //     )
                      //   )
                      // )
                      // val theirLocationRelativeToMeThree =  (
                      //   rotateAroundX((rotationX),
                      //     rotateAroundY((rotationY),
                      //       rotateAroundZ((rotationZ),
                      //         rotateAroundZ(rotationZ,
                      //           rotateAroundY(rotationY,
                      //             rotateAroundX(rotationX,
                      //               beacon.scannerPoint.reverseDirection(myScannerId)
                      //             )
                      //           )
                      //         ).add(neighborBeacon.scannerPoint)
                      //       )
                      //     )
                      //   )
                      // )
                      // directions.compute(myScannerId) { _, directions ->
                      //   (directions ?: mutableMapOf()).apply {
                      //     this.put(
                      //       theirScannerId,
                      //       Triple(rotationX, rotationY, rotationZ) to
                      //         rotateAroundX(rotationX,
                      //           rotateAroundY(rotationY,
                      //             rotateAroundZ(rotationZ,
                      //               beacon.scannerPoint.reverseDirection(0)
                      //             )
                      //           )
                      //         ).add(neighborBeacon.scannerPoint).reverseDirection(myScannerId)
                      //     )
                      //   }
                      // }



                      // val theirMatch = matchingNeighborBeacons.first()
                      // val theirRaw = scannerData[theirScannerId]!!.beaconPoints[theirMatch.id]
                      // val ourMatch = myRotatedNeighbors.first { it.x == theirMatch.x && it.y == theirMatch.y && it.z == theirMatch.z }
                      // val ourRaw = scannerData[myScannerId]!!.beaconPoints[ourMatch.id]

                      // val maths = rotateAroundX(rotationX,
                      //   rotateAroundY(rotationY,
                      //     rotateAroundZ(rotationZ,
                      //       ourRaw
                      //     )
                      //   )
                      // ).add(theirRaw)
                      //
                      // val usToThem = rotateAroundZ(rotationZ,
                      //   rotateAroundY(rotationY,
                      //     rotateAroundX(rotationX,
                      //       ourRaw
                      //     )
                      //   )
                      // ).add(theirRaw)//.reverseDirection(myScannerId)

                      // directions.compute(theirScannerId) { _, map ->
                      //   (map ?: mutableMapOf()).also {
                      //     it[myScannerId] = Triple(rotationX, rotationY, rotationZ) to theirRaw.subtract(ourRaw)
                      //   }
                      // }

                      -1

                      // val rotateAroundX(rotationX,
                      //   rotateAroundY(rotationY,
                      //     rotateAroundZ(rotationZ,
                      //       ourRaw
                      //     )
                      //   )
                      // ).subtract(zeroToOne)



                      // val fromNeighborToUsNeighborReference = neighborBeacon.scannerPoint.reverseDirection(theirScannerId)
                      //   .add(neighborBeacon.neighbors[theirMatch.id])
                      //   .add(
                      //     beacon.scannerPoint.reverseDirection(myScannerId)
                      //       .add(beacon.neighbors[ourMatch.id])
                      //   )
                      //
                      // val fromNeighborToUsAndReferenceSwap =
                      //   rotateAroundX(inverseAngles[rotationX]!!,
                      //     rotateAroundY(inverseAngles[rotationY]!!,
                      //       rotateAroundZ(inverseAngles[rotationZ]!!,
                      //         fromNeighborToUsNeighborReference
                      //       )
                      //     )
                      //   )

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

    // // time to BFS directions and get all the nodes into beacon Zero's reference Frame
    // val beacons = directions.mapValues {
    //   linkedSetOf<Point>().apply {
    //     // addAll(scannerData[0]!!.beaconPoints)
    //   }
    // }
    // beacons[0]!!.addAll(scannerData[0]!!.beaconPoints)

    // exploreAndAdd(scannerData[0]!!, scannerData, directions, mutableSetOf(0), beacons)

    // val rootOfTheBFS = directions[0]
    // rootOfTheBFS?.forEach { (scanner, data) ->
    //   val (oneToZeroRotation, _) = data
    //   val (xRotation, yRotation, zRotation) = oneToZeroRotation
    //   val oneToZeroDirections = directions[scanner]!![0]!!.second
    //   val beaconsToPutIntoZerosReferenceFrame = scannerData[scanner]!!.beaconPoints
    //   val scannerOneBeaconsInZerosReferenceFrame = beaconsToPutIntoZerosReferenceFrame.map {
    //     rotateAroundZ(zRotation, rotateAroundY(yRotation, rotateAroundX(xRotation, it.subtract(oneToZeroDirections))))
    //   }
    //   beacons.addAll(scannerOneBeaconsInZerosReferenceFrame)
    // }

    /*

    val oneToThree = (rotateAroundZ(rotationZ, rotateAroundY(rotationY, rotateAroundX(rotationX, beacon.scannerPoint.reverseDirection(myScannerId))))).add(neighborBeacon.scannerPoint)

val zeroToThree = (rotateAroundZ(0, rotateAroundY(180, rotateAroundX(0, (rotateAroundZ(0, rotateAroundY(180, rotateAroundX(0, directions[0]!![1]!!.second)))).add(oneToThree)))))
zeroToThree
// rotateAroundZ(0, rotateAroundY(180, rotateAroundX(0, zeroToThree))).subtract(scannerData[3]!!.beaconPoints[0])

     */

    // val x = {
    //   val (threeToOneRotation, threeToOneDirections) = directions[3]!![1]!!
    //   val (xRotation, yRotation, zRotation) = threeToOneRotation
    //   val beaconsToPutIntoZerosReferenceFrame = scannerData[3]!!.beaconPoints
    //   val scannerThreeBeaconsInOnesReferenceFrame = beaconsToPutIntoZerosReferenceFrame.map {
    //     rotateAroundZ(zRotation,rotateAroundY(yRotation, rotateAroundX(xRotation, it.subtract(threeToOneDirections))))
    //   }
    //   scannerData[1]!!.beaconPoints.addAll(scannerThreeBeaconsInOnesReferenceFrame)
    // }
    //
    // val z = {
    //   val (threeToOneRotation, threeToOneDirections) = directions[2]!![4]!!
    //   val (xRotation, yRotation, zRotation) = threeToOneRotation
    //   val beaconsToPutIntoZerosReferenceFrame = scannerData[2]!!.beaconPoints
    //   val scannerThreeBeaconsInOnesReferenceFrame = beaconsToPutIntoZerosReferenceFrame.map {
    //     rotateAroundZ(zRotation,rotateAroundY(yRotation, rotateAroundX(xRotation, it.subtract(threeToOneDirections))))
    //   }
    //   scannerData[4]!!.beaconPoints.addAll(scannerThreeBeaconsInOnesReferenceFrame)
    // }
    //
    // val q = {
    //   val (threeToOneRotation, threeToOneDirections) = directions[4]!![1]!!
    //   val (xRotation, yRotation, zRotation) = threeToOneRotation
    //   val beaconsToPutIntoZerosReferenceFrame = scannerData[4]!!.beaconPoints
    //   val scannerThreeBeaconsInOnesReferenceFrame = beaconsToPutIntoZerosReferenceFrame.map {
    //     rotateAroundZ(zRotation,rotateAroundY(yRotation, rotateAroundX(xRotation, it.subtract(threeToOneDirections))))
    //   }
    //   scannerData[1]!!.beaconPoints.addAll(scannerThreeBeaconsInOnesReferenceFrame)
    // }
    //
    // val y = {
    //   val (oneToZeroRotation, oneToZeroDirections) = directions[1]!![0]!!
    //   val (xRotation, yRotation, zRotation) = oneToZeroRotation
    //   val beaconsToPutIntoZerosReferenceFrame = scannerData[1]!!.beaconPoints
    //   val scannerOneBeaconsInZerosReferenceFrame = beaconsToPutIntoZerosReferenceFrame.map {
    //     rotateAroundZ(zRotation,rotateAroundY(yRotation, rotateAroundX(xRotation, it.subtract(oneToZeroDirections))))
    //   }
    //   scannerData[0]!!.beaconPoints.addAll(scannerOneBeaconsInZerosReferenceFrame)
    // }
    //
    // val (fourToOneRotation, fourToOneDirections) = directions[4]!![1]!!
    // val (xRotation, yRotation, zRotation) = fourToOneRotation
    // val beaconsToPutIntoOnesReferenceFrame = scannerData[4]!!.beaconPoints
    // val scannerThreeBeaconsInOnesReferenceFrame = beaconsToPutIntoOnesReferenceFrame.map {
    //   rotateAroundZ(0,rotateAroundY(180, rotateAroundX(0, rotateAroundZ(zRotation,rotateAroundY(yRotation, rotateAroundX(xRotation, it.subtract(fourToOneDirections)))))))
    // }
    //
    // val (oneToZeroRotation, oneToZeroDirections) = directions[1]!![0]!!
    // val (xRotation2, yRotation2, zRotation2) = oneToZeroRotation
    // // val beaconsToPutIntoZerosReferenceFrame = scannerData[1]!!.beaconPoints
    // val scannerOneBeaconsInZerosReferenceFrame = scannerThreeBeaconsInOnesReferenceFrame.map {
    //   rotateAroundZ(zRotation,rotateAroundY(yRotation, rotateAroundX(xRotation, it.subtract(oneToZeroDirections))))
    // }
    // scannerData[0]!!.beaconPoints.addAll(scannerOneBeaconsInZerosReferenceFrame)

    /*

val beaconFoundByScannerThree = scannerData[4].beaconPoints[0]

// val threeToOne = directions[3][1]
// val oneToZero = directions[1][0]

val oneToThree = directions[1][4]
val zeroToOne = directions[0][1]

val beaconThreeInBeaconOneReferenceFrame = rotateAroundZ((oneToThree.first.third + 180) % 360,
  rotateAroundY((oneToThree.first.second + 180) % 360,
    rotateAroundX((oneToThree.first.first + 180) % 360,
      beaconFoundByScannerThree
    )
  )
).add(oneToThree.second)
val beaconThreeInBeaconZeroReferenceFrame = rotateAroundZ((zeroToOne.first.third + 180) % 360,
  rotateAroundY((zeroToOne.first.second + 180) % 360,
    rotateAroundX((zeroToOne.first.first + 180) % 360,
      beaconThreeInBeaconOneReferenceFrame
    )
  )
).add(zeroToOne.second)
beaconThreeInBeaconZeroReferenceFrame

     */

    // x()
    // z()
    // q()
    // y()


    val something = scannerData[0]!!.beaconPoints.toSet().toMutableList().sortedBy {
      it.x
    }

    something.forEach { println(it) }

    -1
  }

  private fun exploreAndAdd(
    root: Scanner,
    scannerData: MutableMap<Int, Scanner>,
    directions: MutableMap<Int, MutableMap<Int, Pair<Triple<Int, Int, Int>, Point>>>, // Scanner -> Scanner -> [rotation, direction]
    exploredNodes: MutableSet<Int>,
    beacons: Map<Int, LinkedHashSet<Point>>
  ): MutableList<Point> {
    val scannersNotExploredYet = directions[root.id]!!.keys.filterNot { exploredNodes.contains(it) }

    scannersNotExploredYet.forEach { scannerIndex ->
      exploredNodes.add(scannerIndex)
      val newBeacons = exploreAndAdd(scannerData[scannerIndex]!!, scannerData, directions, exploredNodes, beacons)
      val (rotation, direction) = directions[scannerIndex]!![root.id]!!
      val (xRotation, yRotation, zRotation) = rotation
      // val otherDirection = directions[scannerIndex]!![root.id]!!.second
      // val beaconsToConvert = scannerData[root.id]!!.beaconPoints.also { it.addAll(newBeacons) }
      val convertedBeacons = newBeacons.map {
        rotateAroundZ(zRotation, rotateAroundY(yRotation, rotateAroundX(xRotation, it.subtract(direction))))
      }
      -1
      // scannerData[root.id]!!.beaconPoints.addAll(convertedBeacons)
      // beacons[root.id]!!.addAll(convertedBeacons)
    }

    return scannerData[root.id]!!.beaconPoints
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

  data class Scanner(val id: Int, val beaconPoints: MutableList<Point>)

  data class Point(val x: Int, val y: Int, val z: Int) {
    var id: Int = -1

    fun reverseDirection(scannerId: Int) = Point(-x, -y, -z).also { it.id = scannerId }

    fun subtract(other: Point) = Point(x-other.x, y-other.y, z-other.z).also { it.id = id }

    fun add(other: Point) = Point(x+other.x, y+other.y, z+other.z).also { it.id = id }

    override fun toString(): String {
      return "$x,$y,$z"
    }
  }
}
