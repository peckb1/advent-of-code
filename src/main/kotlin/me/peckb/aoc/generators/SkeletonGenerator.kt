package me.peckb.aoc.generators

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier.PRIVATE
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import javax.inject.Inject

class SkeletonGenerator(val year: String, val day: String) {
  companion object {
    private val INPUT_GENERATOR_FACTORY_PACKAGE = InputGenerator::class.java.let { "${it.packageName}.${it.simpleName}" }
    private val INPUT_GENERATOR_FACTORY_NAME = InputGeneratorFactory::class.java.simpleName

    private const val SRC_DIRECTORY = "src/main/kotlin"
  }

  fun generateAdventSkeleton() {
    generateDayClass()
    generateREADME()
    generateTestClass()
    generateInput()
  }

  private fun generateDayClass() {
    val factoryClassName = ClassName(INPUT_GENERATOR_FACTORY_PACKAGE, INPUT_GENERATOR_FACTORY_NAME)
    val file = FileSpec.builder("me.peckb.aoc._$year.calendar.day$day", "Day$day")
      .addImport(INPUT_GENERATOR_FACTORY_PACKAGE, INPUT_GENERATOR_FACTORY_NAME)
      .addType(
        TypeSpec.classBuilder("Day$day")
          .primaryConstructor(
            FunSpec.constructorBuilder()
              .addAnnotation(Inject::class)
              .addParameter("generatorFactory", factoryClassName)
              .build()
          ).addProperty(
            PropertySpec.builder("generatorFactory", factoryClassName)
              .initializer("generatorFactory")
              .addModifiers(PRIVATE)
              .build()
          )
          .addFunction(
            FunSpec.builder("partOne")
              .addParameter("filename", String::class)
              .addModifiers()
              .addStatement("return·generatorFactory.forFile(filename).readAs(::day$day)·{·input·->\n  -1\n}")
              .build()
          )
          .addFunction(
            FunSpec.builder("partTwo")
              .addParameter("filename", String::class)
              .addStatement("return·generatorFactory.forFile(filename).readAs(::day$day)·{·input·->\n  -1\n}")
              .build()
          )
          .addFunction(
            FunSpec.builder("day$day")
              .addModifiers(PRIVATE)
              .addParameter("line", String::class)
              .addStatement("return 4")
              .build()
          )
          .build()
      )
      .build()

    file.writeTo(File(SRC_DIRECTORY))

    // remove all the extraneous `public` modifiers
    // and the safety import of `kotlin.String`
    val path = Paths.get("$SRC_DIRECTORY/me/peckb/aoc/_$year/calendar/day$day/Day$day.kt")
    val content = String(Files.readAllBytes(path))
      .replace("public ".toRegex(), "")
      .replace("import kotlin.String".toRegex(), "")
    Files.write(path, content.toByteArray())
  }

  fun generateREADME() {
  }

  fun generateTestClass() {
  }

  fun generateInput() {
  }
}
