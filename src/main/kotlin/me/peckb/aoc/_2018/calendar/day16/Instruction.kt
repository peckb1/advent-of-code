package me.peckb.aoc._2018.calendar.day16

@Suppress("unused")
sealed class Instruction(val a: Int, val b: Int, val c: Int) {
  abstract fun possibility(beforeRegisters: List<Int>, afterRegisters: List<Int>): Boolean
  abstract fun performAction(registers: List<Int>): Int

  // Add Operations
  class AddRegister(_a: Int, _b: Int, _c: Int) : Instruction(_a, _b, _c) {
    /**
     * stores into register C the result of adding register A and register B.
     */
    override fun possibility(beforeRegisters: List<Int>, afterRegisters: List<Int>): Boolean {
      return afterRegisters[c] == performAction(beforeRegisters)
    }

    override fun performAction(registers: List<Int>): Int {
      return registers[a] + registers[b]
    }
  }
  class AddImmediate(_a: Int, _b: Int, _c: Int) : Instruction(_a, _b, _c) {
    /**
     * stores into register C the result of adding register A and value B.
     */
    override fun possibility(beforeRegisters: List<Int>, afterRegisters: List<Int>): Boolean {
      return afterRegisters[c] == beforeRegisters[a] + b
    }
  }

  // Multiple Operations
  class MultiplyRegister(_a: Int, _b: Int, _c: Int) : Instruction(_a, _b, _c) {
    /**
     * stores into register C the result of multiplying register A and register B.
     */
    override fun possibility(beforeRegisters: List<Int>, afterRegisters: List<Int>): Boolean {
      return afterRegisters[c] == beforeRegisters[a] * beforeRegisters[b]
    }
  }
  class MultipleImmediate(_a: Int, _b: Int, _c: Int) : Instruction(_a, _b, _c) {
    /**
     * stores into register C the result of multiplying register A and value B.
     */
    override fun possibility(beforeRegisters: List<Int>, afterRegisters: List<Int>): Boolean {
      return afterRegisters[c] == beforeRegisters[a] * b
    }
  }

  // AND operations
  class BitwiseAndRegister(_a: Int, _b: Int, _c: Int) : Instruction(_a, _b, _c) {
    /**
     * stores into register C the result of the bitwise AND of register A and register B.
     */
    override fun possibility(beforeRegisters: List<Int>, afterRegisters: List<Int>): Boolean {
      return afterRegisters[c] == beforeRegisters[a].and(beforeRegisters[b])
    }
  }
  class BitwiseAndImmediate(_a: Int, _b: Int, _c: Int) : Instruction(_a, _b, _c) {
    /**
     * stores into register C the result of the bitwise AND of register A and value B.
     */
    override fun possibility(beforeRegisters: List<Int>, afterRegisters: List<Int>): Boolean {
      return afterRegisters[c] == beforeRegisters[a].and(b)
    }
  }

  // OR operations
  class BitwiseOrRegister(_a: Int, _b: Int, _c: Int) : Instruction(_a, _b, _c) {
    /**
     *  stores into register C the result of the bitwise OR of register A and register B.
     */
    override fun possibility(beforeRegisters: List<Int>, afterRegisters: List<Int>): Boolean {
      return afterRegisters[c] == beforeRegisters[a].or(beforeRegisters[b])
    }
  }
  class BitwiseOrImmediate(_a: Int, _b: Int, _c: Int) : Instruction(_a, _b, _c) {
    /**
     * stores into register C the result of the bitwise OR of register A and value B.
     */
    override fun possibility(beforeRegisters: List<Int>, afterRegisters: List<Int>): Boolean {
      return afterRegisters[c] == beforeRegisters[a].or(b)
    }
  }

  // Assignment operations
  class AssignmentRegister(_a: Int, _b: Int, _c: Int) : Instruction(_a, _b, _c) {
    /**
     *  copies the contents of register A into register C. (Input B is ignored.)
     */
    override fun possibility(beforeRegisters: List<Int>, afterRegisters: List<Int>): Boolean {
      return afterRegisters[c] == beforeRegisters[a]
    }
  }
  class AssignmentImmediate(_a: Int, _b: Int, _c: Int) : Instruction(_a, _b, _c) {
    /**
     * stores value A into register C. (Input B is ignored.)
     */
    override fun possibility(beforeRegisters: List<Int>, afterRegisters: List<Int>): Boolean {
      return afterRegisters[c] == a
    }
  }

  // Greater-than testing
  class GreaterThanImmediateRegister(_a: Int, _b: Int, _c: Int) : Instruction(_a, _b, _c) {
    /**
     * sets register C to 1 if value A is greater than register B. Otherwise, register C is set to 0.
     */
    override fun possibility(beforeRegisters: List<Int>, afterRegisters: List<Int>): Boolean {
      val result = a > beforeRegisters[b]
      return afterRegisters[c] == if (result) 1 else 0
    }
  }
  class GreaterThanRegisterImmediate(_a: Int, _b: Int, _c: Int) : Instruction(_a, _b, _c) {
    /**
     * sets register C to 1 if register A is greater than value B. Otherwise, register C is set to 0.
     */
    override fun possibility(beforeRegisters: List<Int>, afterRegisters: List<Int>): Boolean {
      val result = beforeRegisters[a] > b
      return afterRegisters[c] == if (result) 1 else 0
    }
  }
  class GreaterThanRegisterRegister(_a: Int, _b: Int, _c: Int) : Instruction(_a, _b, _c) {
    /**
     * sets register C to 1 if register A is greater than register B. Otherwise, register C is set to 0.
     */
    override fun possibility(beforeRegisters: List<Int>, afterRegisters: List<Int>): Boolean {
      val result = beforeRegisters[a] > beforeRegisters[b]
      return afterRegisters[c] == if (result) 1 else 0
    }
  }

  // Equality testing
  class EqualImmediateRegister(_a: Int, _b: Int, _c: Int) : Instruction(_a, _b, _c) {
    /**
     *  sets register C to 1 if value A is equal to register B. Otherwise, register C is set to 0.
     */
    override fun possibility(beforeRegisters: List<Int>, afterRegisters: List<Int>): Boolean {
      val result = a == beforeRegisters[b]
      return afterRegisters[c] == if (result) 1 else 0
    }
  }
  class EqualRegisterImmediate(_a: Int, _b: Int, _c: Int) : Instruction(_a, _b, _c) {
    /**
     * sets register C to 1 if register A is equal to value B. Otherwise, register C is set to 0.
     */
    override fun possibility(beforeRegisters: List<Int>, afterRegisters: List<Int>): Boolean {
      val result = beforeRegisters[a] == b
      return afterRegisters[c] == if (result) 1 else 0
    }
  }
  class EqualRegisterRegister(_a: Int, _b: Int, _c: Int) : Instruction(_a, _b, _c) {
    /**
     * sets register C to 1 if register A is equal to register B. Otherwise, register C is set to 0.
     */
    override fun possibility(beforeRegisters: List<Int>, afterRegisters: List<Int>): Boolean {
      val result = beforeRegisters[a] == beforeRegisters[b]
      return afterRegisters[c] == if (result) 1 else 0
    }
  }
}
