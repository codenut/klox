package com.craftinginterpreters.tool

import java.io.PrintWriter
import kotlin.system.exitProcess

class GenerateAst {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            if (args.size != 1) {
                System.err.println("Usage: generate_ast <output directory>")
                exitProcess(64)
            }
            val outputDir = args[0]
            defineAst(
                outputDir, "Expr", listOf(
                    "Binary; val left: Expr, val operator: Token , val right: Expr ",
                    "Grouping; val expression: Expr",
                    "Literal; val value: Any",
                    "Unary; val operator: Token , val right: Expr"
                )
            )
        }

        private fun defineAst(outputDir: String, baseName: String, types: List<String>) {
            val path = "$outputDir/$baseName.kt"
            val writer = PrintWriter(path, "UTF-8")

            writer.println("package com.craftinginterpreters.lox")
            writer.println()

            writer.println("import java.util.List")
            writer.println()
            writer.println("abstract class $baseName {")

            defineVisitor(writer, baseName, types)

            for (type in types) {
                val className = type.split(";")[0].trim()
                val fields = type.split(";")[1].trim()
                defineType(writer, baseName, className, fields)
            }

            writer.println()
            writer.println("    abstract fun <R> accept(visitor: Visitor<R>) : R")
            writer.println("}")
            writer.close()
        }

        private fun defineType(
            writer: PrintWriter,
            baseName: String,
            className: String,
            fieldList: String
        ) {
            writer.println("    data class $className ($fieldList) : $baseName() {")
            writer.println("        override fun <R> accept(visitor: Visitor<R>) : R {")
            writer.println("            return visitor.visit$className$baseName(this)")
            writer.println("        }")
            writer.println("    }")
        }

        private fun defineVisitor(
            writer: PrintWriter,
            baseName: String,
            types: List<String>
        ) {
            writer.println("    interface Visitor<R> {")

            for (type in types) {
                val typeName = type.split(";")[0].trim()
                writer.println("        fun visit$typeName$baseName(${baseName.lowercase()}: $typeName) : R")
            }

            writer.println("    }")
        }
    }
}