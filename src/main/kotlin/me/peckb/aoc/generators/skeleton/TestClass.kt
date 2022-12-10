package me.peckb.aoc.generators.skeleton

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier.CONST
import com.squareup.kotlinpoet.KModifier.INTERNAL
import com.squareup.kotlinpoet.KModifier.LATEINIT
import com.squareup.kotlinpoet.KModifier.PRIVATE
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import me.peckb.aoc.generators.skeleton.SkeletonGenerator.Companion.INPUT_DIRECTORY
import me.peckb.aoc.generators.skeleton.SkeletonGenerator.Companion.TEST_DIRECTORY
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import javax.inject.Inject

object TestClass {
  fun generateTestClass(year: String, day: String) {
    val file = FileSpec.builder("me.peckb.aoc._$year.calendar.day$day", "Day${day}Test")
      .addImport(ClassName("me.peckb.aoc", "_$year"), "DaggerTestDayComponent")
      .addImport(ClassName("org.junit.jupiter.api", "Assertions"), "assertEquals")
      .addType(
        TypeSpec.classBuilder("Day${day}Test")
          .addModifiers(INTERNAL)
          .addType(
            TypeSpec.companionObjectBuilder()
              .addProperty(
                PropertySpec.builder("DAY_$day", String::class)
                  .initializer("%S", "$INPUT_DIRECTORY/$year/day$day.input")
                  .addModifiers(PRIVATE, CONST)
                  .build()
              )
              .build()
          )
          .addFunction(
            FunSpec.builder("setup")
              .addAnnotation(ClassName("org.junit.jupiter.api", "BeforeEach"))
              .addStatement("DaggerTestDayComponent.create().inject(this)")
              .build()
          )
          .addProperty(
            PropertySpec.builder("day$day", ClassName("me.peckb.aoc._$year.calendar.day$day", "Day$day"))
              .addAnnotation(Inject::class)
              .addModifiers(LATEINIT)
              .mutable(true)
              .build()
          ).addFunction(
            FunSpec.builder("testDay${day}PartOne")
              .addAnnotation(ClassName("org.junit.jupiter.api", "Test"))
              .addStatement("assertEquals(-1, day${day}.partOne(DAY_${day}))")
              .build()
          ).addFunction(
            FunSpec.builder("testDay${day}PartTwo")
              .addAnnotation(ClassName("org.junit.jupiter.api", "Test"))
              .addStatement("assertEquals(-1, day${day}.partTwo(DAY_${day}))")
              .build()
          )
          .build()
      )
      .build()

    file.writeTo(File("$TEST_DIRECTORY/kotlin"))

    // remove all the extraneous `public` and `:Unit` modifiers
    // and the safety imports of `kotlin.String` and `kotlin.Unit`
    val path = Paths.get("$TEST_DIRECTORY/kotlin/me/peckb/aoc/_$year/calendar/day$day/Day${day}Test.kt")
    val content = String(Files.readAllBytes(path))
      .replace("public ".toRegex(), "")
      .replace(": Unit".toRegex(), "")
      .replace("import kotlin.String".toRegex(), "")
      .replace("import kotlin.Unit".toRegex(), "")
    Files.write(path, content.toByteArray())
  }
}
