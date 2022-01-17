package me.peckb.aoc._2018.calendar.day07

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

typealias StepId = String

class Day07 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val steps = generateSteps(input)

    val stepsReadyToComplete = sortedSetOf<StepId>()
    val stepsNeedingHelp = sortedSetOf<StepId>()
    val completedSteps = linkedSetOf<StepId>()
    steps.values.filter { it.blockedBy.isEmpty() }.forEach { stepsReadyToComplete.add(it.id) }

    while (stepsReadyToComplete.isNotEmpty() || stepsNeedingHelp.isNotEmpty()) {
      if (stepsReadyToComplete.isNotEmpty()) {
        val stepId = stepsReadyToComplete.first().also {
          completedSteps.add(it)
          stepsReadyToComplete.remove(it)
        }
        steps[stepId]?.blocks?.forEach { step ->
          if (step.blockedBy.all { completedSteps.contains(it.id) }) {
            stepsReadyToComplete.add(step.id)
            stepsNeedingHelp.remove(step.id)
          } else {
            stepsNeedingHelp.add(step.id)
          }
        }
      } else { // stepsNeedingHelp.isNotEmpty()
        val stepId = stepsNeedingHelp.first().also {
          stepsNeedingHelp.remove(it)
        }
        steps[stepId]?.blockedBy?.forEach { step ->
          if (step.blockedBy.all { completedSteps.contains(it.id) }) {
            stepsReadyToComplete.add(step.id)
            stepsNeedingHelp.remove(step.id)
          } else {
            stepsNeedingHelp.add(step.id)
          }
        }
      }
    }

    completedSteps.joinToString("")
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val steps = generateSteps(input)

    val stepsReadyToComplete = sortedSetOf<StepId>()
    val stepsNeedingHelp = sortedSetOf<StepId>()
    val completedSteps = linkedSetOf<StepId>()
    val stepsBeingProcessed = mutableSetOf<StepId>()
    steps.values.filter { it.blockedBy.isEmpty() }.forEach { stepsReadyToComplete.add(it.id) }

    var time = 0
    var workers = 5
    val jobs = mutableListOf<(Int) -> Boolean>()

    while (stepsReadyToComplete.isNotEmpty() || stepsNeedingHelp.isNotEmpty() || completedSteps.size != steps.size) {
      val jobsToClean = jobs.filter { it(time) }
      jobs.removeAll(jobsToClean)

      while (stepsReadyToComplete.isNotEmpty() && workers > 0) {
        val stepId = stepsReadyToComplete.first()
        stepsReadyToComplete.remove(stepId)
        stepsBeingProcessed.add(stepId)
        val timeToEnd = time + 60 + (stepId[0].code - 64)
        workers--
        val job: (Int) -> Boolean = { t ->
          val allDone = t == timeToEnd
          if (allDone) {
            workers++
            completedSteps.add(stepId)
            stepsReadyToComplete.remove(stepId)
            steps[stepId]?.blocks?.forEach { step ->
              if (step.blockedBy.all { completedSteps.contains(it.id) }) {
                if (!completedSteps.contains(step.id) && !stepsBeingProcessed.contains(step.id)) {
                  stepsReadyToComplete.add(step.id)
                }
                stepsNeedingHelp.remove(step.id)
              } else {
                stepsNeedingHelp.add(step.id)
              }
            }
          }
          allDone
        }
        jobs.add(job)
      }

      if(stepsNeedingHelp.isNotEmpty() && workers > 0) {
        val ids = stepsNeedingHelp.toList()
        ids.forEach { stepId ->
          stepsNeedingHelp.remove(stepId)
          steps[stepId]?.blockedBy?.forEach { step ->
            if (step.blockedBy.all { completedSteps.contains(it.id) }) {
              if (!completedSteps.contains(step.id) && !stepsBeingProcessed.contains(step.id)) {
                stepsReadyToComplete.add(step.id)
              }
              stepsNeedingHelp.remove(step.id)
            } else {
              stepsNeedingHelp.add(step.id)
            }
          }
        }
      }

      time++
    }

    // one over - since we always increment time, even the last loop which finished
    time - 1
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
    val blocks: MutableList<Step> = mutableListOf(),
  )
}
