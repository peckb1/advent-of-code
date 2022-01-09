package me.peckb.aoc.generators.skeleton

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier.ABSTRACT
import com.squareup.kotlinpoet.KModifier.INTERNAL
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import dagger.Component as DaggerComponent
import me.peckb.aoc.DayComponent
import me.peckb.aoc.InputModule
import me.peckb.aoc.generators.skeleton.SkeletonGenerator.Companion.TEST_DIRECTORY
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import javax.inject.Singleton

object Component {
  fun setupTestComponent(year: String, day: String) {
    val path = Paths.get("$TEST_DIRECTORY/kotlin/me/peckb/aoc/_$year/TestDayComponent.kt")
    if (Files.exists(path)) {
      alterTestDayComponent(year, day)
    } else {
      createTestDayComponent(year, day)
    }
  }

  private fun alterTestDayComponent(year: String, day: String) {
    val path = Paths.get("$TEST_DIRECTORY/kotlin/me/peckb/aoc/_$year/TestDayComponent.kt")
    val content = String(Files.readAllBytes(path))
      .replace("}".toRegex(), "  fun inject(day${day}Test: Day${day}Test)\n}")
      .replace("import javax.inject.Singleton".toRegex(), "import me.peckb.aoc._$year.calendar.day$day.Day${day}Test\nimport javax.inject.Singleton")
    Files.write(path, content.toByteArray())
  }

  private fun createTestDayComponent(year: String, day: String) {
    val file = FileSpec.builder("me.peckb.aoc._$year", "TestDayComponent")
      .addType(
        TypeSpec.interfaceBuilder("TestDayComponent")
          .addAnnotation(Singleton::class)
          .addAnnotation(
            AnnotationSpec.builder(DaggerComponent::class)
              .addMember("modules = [%T::class]", InputModule::class)
              .build())
          .addModifiers(INTERNAL)
          .addSuperinterface(DayComponent::class)
          .addFunction(
            FunSpec.builder("inject")
              .addModifiers(ABSTRACT)
              .addParameter("day${day}Test", ClassName("me.peckb.aoc._$year.calendar.day$day", "Day${day}Test"))
              .build()
          )
          .build()
      )
      .build()

    file.writeTo(File("$TEST_DIRECTORY/kotlin"))

    // remove all the extraneous `public` modifiers
    // and extraeous `Unit`
    val path = Paths.get("${TEST_DIRECTORY}/kotlin/me/peckb/aoc/_$year/TestDayComponent.kt")
    val content = String(Files.readAllBytes(path))
      .replace("public ".toRegex(), "")
      .replace("import kotlin.Unit".toRegex(), "")
      .replace(": Unit".toRegex(), "")
    Files.write(path, content.toByteArray())
  }
}
