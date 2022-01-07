package me.peckb.aoc

import java.math.BigInteger
import java.security.MessageDigest
import javax.inject.Inject

class MD5 @Inject constructor() {
  fun hash(key: String): String {
    return BigInteger(1, MD.digest(key.toByteArray())).toHexString()
  }

  private fun BigInteger.toHexString() = toString(16).padStart(32, '0')

  companion object {
    private val MD = MessageDigest.getInstance("MD5")
  }
}