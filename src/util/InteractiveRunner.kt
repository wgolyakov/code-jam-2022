// This is a small program that runs two processes, connecting the stdin of each
// one to the stdout of the other.
// It doesn't perform a lot of checking, so many errors may
// be caught internally by Kotlin (e.g., if your command line has incorrect
// syntax) or not caught at all (e.g., if the judge or solution hangs).
//
// Run this as:
// InteractiveRunner.kt <cmd_line_judge> -- <cmd_line_solution>
//
// For example, if you have a LocalTestingTool.kt (that takes a single
// integer as a command line parameter) to use as judge -- like one
// downloaded from a problem statement -- and you would run your solution
// in a standalone using:
//   java Solution.kt
// Then you could run the judge and solution together, using this, as:
//   java InteractiveRunner.kt java TestingTool.kt -- java Solution.kt
// Notice that the solution would usually have a compilation step before running,
// which you should run in your usual way before using this tool.
//
// This is only intended as a convenient tool to help contestants test solutions
// locally. In particular, it is not identical to the implementation on our
// server, which is more complex.
//
// The standard streams are handled the following way:
// - judge's stdin is connected to the solution's stdout;
// - judge's stdout is connected to the solution's stdin;
// - stderrs of both judge and solution are piped to standard error stream, with
//   lines prepended by "judge: " or "sol: " respectively (note, no
//   synchronization is done so it's possible for the messages from both programs
//   to overlap with each other).
package util

import java.io.*
import kotlin.system.exitProcess

fun redirectStreams(inputStream: InputStream, outputStream: OutputStream): Thread {
	val thread = Thread {
		val bufferedReader = BufferedReader(InputStreamReader(inputStream))
		val bufferedWriter = BufferedWriter(OutputStreamWriter(outputStream))
		try {
			var lineToPipe: String? = bufferedReader.readLine()
			while (lineToPipe != null) {
				bufferedWriter.write(lineToPipe)
				bufferedWriter.newLine()
				bufferedWriter.flush()
				lineToPipe = bufferedReader.readLine()
			}
		} catch (e: IOException) {
			e.printStackTrace()
		}
		try {
			bufferedWriter.close()
		} catch (e: IOException) {
			// ignore
		}
	}
	thread.start()
	return thread
}

class SubprocessThread(args: Array<String>,
					   val stderrPrefix: String? = null,
					   stdinPipe: InputStream? = null,
					   stdoutPipe: OutputStream? = null) : Thread() {
	val process: Process
	var returnCode = 0
	var errorMessage: String? = null
	private var inThread: Thread? = null
	private var outThread: Thread? = null

	init {
		val pb = ProcessBuilder(*args)
		process = pb.start()
		if (stdinPipe != null)
			outThread = redirectStreams(stdinPipe, process.outputStream!!)
		if (stdoutPipe != null)
			inThread = redirectStreams(process.inputStream!!, stdoutPipe)
	}

	override fun run() {
		try {
			pipeToStdErr(process.errorStream)
			returnCode = process.waitFor()
			errorMessage = null
		} catch (e: Exception) {
			returnCode = -1
			errorMessage = "The process crashed or produced too much output."
		}
	}

	// Reads bytes from the stream and writes them to System.err prepending lines
	// with stderrPrefix.
	private fun pipeToStdErr(stream: InputStream) {
		val bufferedReader = BufferedReader(InputStreamReader(stream))
		while (true) {
			var chunk = bufferedReader.readLine() ?: return
			if (stderrPrefix != null)
				chunk = stderrPrefix + chunk
			System.err.println(chunk)
			System.err.flush()
		}
	}
}

fun main(argv: Array<String>) {
	if (argv.count { it == "--" } != 1) {
		println("There should be exactly one instance of '--' in the command line.")
		exitProcess(1)
	}
	val sepIndex = argv.indexOf("--")
	val judgeArgs = argv.copyOfRange(0, sepIndex)
	val solArgs = argv.copyOfRange(sepIndex + 1, argv.size)

	val tSol = SubprocessThread(solArgs, stderrPrefix = "  sol: ")
	val tJudge = SubprocessThread(
		judgeArgs,
		stdinPipe = tSol.process.inputStream,
		stdoutPipe = tSol.process.outputStream,
		stderrPrefix = "judge: "
	)
	tSol.start()
	tJudge.start()
	tSol.join()
	tJudge.join()

	// Print an empty line to handle the case when stderr doesn't print EOL.
	println()
	println("Judge return code: ${tJudge.returnCode}")
	if (tJudge.errorMessage != null)
		println("Judge error message: ${tJudge.errorMessage}")

	println("Solution return code: ${tSol.returnCode}")
	if (tSol.errorMessage != null)
		println("Solution error message: ${tSol.errorMessage}")

	if (tSol.returnCode != 0)
		println("A solution finishing with exit code other than 0 (without exceeding " +
			"time or memory limits) would be interpreted as a Runtime Error " +
			"in the system.")
	else if (tJudge.returnCode != 0)
		println("A solution finishing with exit code 0 (without exceeding time or " +
			"memory limits) and a judge finishing with exit code other than 0 " +
			"would be interpreted as a Wrong Answer in the system.")
	else
		println("A solution and judge both finishing with exit code 0 (without " +
			"exceeding time or memory limits) would be interpreted as Correct " +
			"in the system.")
}
