package com.lakroft.kotlin

import picocli.CommandLine.*
import picocli.CommandLine.Option
import picocli.CommandLine.Command
import java.io.File

@Command(name = "ideone-getter", mixinStandardHelpOptions = true, version = arrayOf("0.0.1"))
class CLITool: Runnable {
    @Option(names = arrayOf("-v", "--verbose"), description = arrayOf("Verbose mode. Helpful for troubleshooting. " +
            "Multiple -v options increase the verbosity."))
    var verbose = emptyArray<Boolean>()

    @Parameters(arity = "1..*", paramLabel = "LANGs", description = arrayOf("Language(s) to process."))
    var langs = ArrayList<String>(0)

    override fun run() {
        if (verbose.isNotEmpty()) {
            println(langs.size.toString() + " languages to process")
        }
        if (verbose.size > 1) {
            for (l in langs) {
                println(l)
            }
        }
    }
}
