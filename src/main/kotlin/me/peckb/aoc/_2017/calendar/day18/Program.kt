package me.peckb.aoc._2017.calendar.day18

import me.peckb.aoc._2017.calendar.day18.Day18.Companion.fromRegisters

data class Program(
  val id: Long,
  val instructions: List<Instruction>,
  val receiver: (Long) -> Unit
) {
  var readyToReceive = false

  private val registers = mutableMapOf('p' to id).withDefault { 0 }
  private var instructionIndex = 0
  private var receiveRegister: Char? = null

  fun start() {
    while(!readyToReceive) {
      when (val next = instructions[instructionIndex]) {
        is Instruction.Add -> {
          registers[next.register] = registers.getValue(next.register) + next.value.fromRegisters(registers)
          instructionIndex++
        }
        is Instruction.JumpGreaterThanZero -> {
          if (next.value.fromRegisters(registers) > 0) {
            instructionIndex += next.steps.fromRegisters(registers).toInt()
          } else {
            instructionIndex++
          }
        }
        is Instruction.Modulo -> {
          registers[next.register] = registers.getValue(next.register) % next.value.fromRegisters(registers)
          instructionIndex++
        }
        is Instruction.Multiply -> {
          registers[next.register] = registers.getValue(next.register) * next.value.fromRegisters(registers)
          instructionIndex++
        }
        is Instruction.Receive -> {
          receiveRegister = next.register
          readyToReceive = true
        }
        is Instruction.Send -> {
          receiver(registers.getValue(next.register))
          instructionIndex++
        }
        is Instruction.Set -> {
          registers[next.register] = next.value.fromRegisters(registers)
          instructionIndex++
        }
      }
    }
  }

  fun send(value: Long) {
    val r = receiveRegister ?: throw IllegalStateException("Asked to get a value, but no register set to receive.")
    registers[r] = value
    readyToReceive = false
    instructionIndex++
    start()
  }
}
