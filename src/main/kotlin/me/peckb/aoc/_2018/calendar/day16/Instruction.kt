package me.peckb.aoc._2018.calendar.day16

sealed class Instruction(val a: Int, val b: Int, val c: Int) {
  abstract fun performAction(registers: List<Int>): Int

  fun possibility(before: List<Int>, after: List<Int>) = after[c] == performAction(before)

  // Add Operations
  class AddRegister(_a: Int, _b: Int, _c: Int) : Instruction(_a, _b, _c) {
    override fun performAction(registers: List<Int>) = registers[a] + registers[b]
  }
  class AddImmediate(_a: Int, _b: Int, _c: Int) : Instruction(_a, _b, _c) {
    override fun performAction(registers: List<Int>) = registers[a] + b
  }

  // Multiple Operations
  class MultiplyRegister(_a: Int, _b: Int, _c: Int) : Instruction(_a, _b, _c) {
    override fun performAction(registers: List<Int>) = registers[a] * registers[b]
  }
  class MultipleImmediate(_a: Int, _b: Int, _c: Int) : Instruction(_a, _b, _c) {
    override fun performAction(registers: List<Int>) = registers[a] * b
  }

  // AND operations
  class BitwiseAndRegister(_a: Int, _b: Int, _c: Int) : Instruction(_a, _b, _c) {
    override fun performAction(registers: List<Int>) = registers[a].and(registers[b])
  }
  class BitwiseAndImmediate(_a: Int, _b: Int, _c: Int) : Instruction(_a, _b, _c) {
    override fun performAction(registers: List<Int>) = registers[a].and(b)
  }

  // OR operations
  class BitwiseOrRegister(_a: Int, _b: Int, _c: Int) : Instruction(_a, _b, _c) {
    override fun performAction(registers: List<Int>) = registers[a].or(registers[b])
  }
  class BitwiseOrImmediate(_a: Int, _b: Int, _c: Int) : Instruction(_a, _b, _c) {
    override fun performAction(registers: List<Int>) = registers[a].or(b)
  }

  // Assignment operations
  class AssignmentRegister(_a: Int, _b: Int, _c: Int) : Instruction(_a, _b, _c) {
    override fun performAction(registers: List<Int>) = registers[a]
  }
  class AssignmentImmediate(_a: Int, _b: Int, _c: Int) : Instruction(_a, _b, _c) {
    override fun performAction(registers: List<Int>) = a
  }

  // Greater-than testing
  class GreaterThanImmediateRegister(_a: Int, _b: Int, _c: Int) : Instruction(_a, _b, _c) {
    override fun performAction(registers: List<Int>) = if (a > registers[b]) 1 else 0
  }
  class GreaterThanRegisterImmediate(_a: Int, _b: Int, _c: Int) : Instruction(_a, _b, _c) {
    override fun performAction(registers: List<Int>) = if (registers[a] > b) 1 else 0
  }
  class GreaterThanRegisterRegister(_a: Int, _b: Int, _c: Int) : Instruction(_a, _b, _c) {
    override fun performAction(registers: List<Int>) = if (registers[a] > registers[b]) 1 else 0
  }

  // Equality testing
  class EqualImmediateRegister(_a: Int, _b: Int, _c: Int) : Instruction(_a, _b, _c) {
    override fun performAction(registers: List<Int>) = if (a == registers[b]) 1 else 0
  }
  class EqualRegisterImmediate(_a: Int, _b: Int, _c: Int) : Instruction(_a, _b, _c) {
    override fun performAction(registers: List<Int>) = if (registers[a] == b) 1 else 0
  }
  class EqualRegisterRegister(_a: Int, _b: Int, _c: Int) : Instruction(_a, _b, _c) {
    override fun performAction(registers: List<Int>) = if (registers[a] == registers[b]) 1 else 0
  }
}
