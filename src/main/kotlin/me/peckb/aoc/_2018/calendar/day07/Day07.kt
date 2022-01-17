package me.peckb.aoc._2018.calendar.day07

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

typealias StepId = String

class Day07 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val steps = generateSteps(input)

    val completed = linkedSetOf<StepId>()

    while (completed.size != steps.size) {
      fun Step.notCompleted() = !completed.contains(id)
      fun Step.notBlocked() = blockedBy.all { completed.contains(it.id) }

      val nextSteps = steps.values
        .filter { it.notCompleted() && it.notBlocked() }
        .sortedBy { it.id }

      // for part one - we do one at a time
      val nextStep = nextSteps.first()
      completed.add(nextStep.id)
    }

    completed.joinToString("")
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val steps = generateSteps(input)

    var time = 0
    var workers = 5
    val completed = mutableSetOf<StepId>()
    val processing = mutableSetOf<StepId>()
    val jobs = mutableMapOf<StepId, (Int) -> Boolean>()

    fun Step.notBeingProcessed() = !processing.contains(id)
    fun Step.notCompleted() = !completed.contains(id)
    fun Step.notBlocked() = blockedBy.all { completed.contains(it.id) }

    fun createJob(timeToStop: Int) = { currentTime: Int -> currentTime == timeToStop }

    while (completed.size != steps.size) {
      // we also need to check if a step is being processed, to not do double work
      val nextSteps = steps.values
        .filter { it.notCompleted() && it.notBeingProcessed() && it.notBlocked() }
        .sortedBy { it.id }

      // for part two - we need to do many at a time, so instead of just taking the first
      // iterate over the entire list, and make a job that will finish after processing
      nextSteps.take(workers).forEach { step ->
        workers--
        processing.add(step.id)
        val timeToStop = time + 60 + (step.id[0].code - 64)
        jobs[step.id] = createJob(timeToStop)
      }

      // once all available items are being worked on (or we ran out of workers) increment the time
      time++

      // at every time interval, check to see if we have any workers finished, and ready to
      // mark their jbo as done, and then go back into the worker pool
      jobs.filter { (_, job) -> job(time) }.forEach { (stepId, _) ->
        workers++
        jobs.remove(stepId)
        completed.add(stepId)
      }
    }

    time
  }

  private fun generateSteps(input: Sequence<String>): MutableMap<StepId, Step> {
    val steps = mutableMapOf<StepId, Step>()

    input.forEach { stepString ->
      val parts = stepString.split(" ")

      val myId = parts[1]
      val idIBlock = parts[7]

      val myStep: Step = steps[myId] ?: Step(myId).also { steps[myId] = it }
      val stepIBlock: Step = steps[idIBlock] ?: Step(idIBlock).also { steps[idIBlock] = it }

      myStep.blocks.add(stepIBlock)
      stepIBlock.blockedBy.add(myStep)
    }

    return steps
  }

  data class Step(
    val id: StepId,
    val blockedBy: MutableList<Step> = mutableListOf(),
    val blocks: MutableList<Step> = mutableListOf()
  )
}
