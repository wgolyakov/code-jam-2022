// judge for Revenge of Goro sort.

// Usage: `LocalTestingTool test_number`, where the argument test_number is
// either 0 (test set 1), 1 (test set 2), 2 (test set 3).
package r31

import kotlin.random.Random
import kotlin.system.exitProcess

val T_ARR = intArrayOf(1000, 1000, 1000)
val N_ARR = intArrayOf(100, 100, 100)
val K_ARR = intArrayOf(16500, 12500, 11500)

const val NOT_YET_SORTED = 0
const val IS_SORTED = 1
const val INVALID_OUTPUT = -1
const val RUN_OUT_OF_TURNS = -1

class Error(message: String): Exception(message)

const val INVALID_LINE_ERROR = "Couldn't read a valid line."
const val RAN_OUT_OF_TURNS_ERROR = "Ran out of turns."
val UNEXPECTED_LENGTH_ERROR = { s1: Any, s2: Any -> "Expected line with $s1 tokens, but actually got $s2" }
const val INVALID_INTEGER_ERROR = "Got an incorrectly formatted integer"
val INVALID_PARTITION_VALUE_ERROR =
	{ s1: Any, s2: Any, s3: Any -> "Expected partition value in range [$s1, $s2], but actually got $s3" }
val ADDITIONAL_INPUT_ERROR = { s: Any -> "Additional input after all cases finish: $s" }
val CASE_FAILED_ERROR = { s1: Any, s2: Any -> "Case #$s1 failed: $s2" }
var random = Random(12345)

fun shuffle(arr: IntArray, partitions: Collection<List<Int>>) {
	// The real judge may implement Shuffle() in a different way and use random
	// numbers differently.
	for (partition in partitions) {
		val vals = partition.map { arr[it] }.toIntArray()
		vals.shuffle(random)
		for (i in partition.indices)
			arr[partition[i]] = vals[i]
	}
}

fun isSorted(arr: IntArray): Boolean {
	for (i in 0 until arr.size - 1)
		if (arr[i] > arr[i + 1])
			return false
	return true
}

fun output(line: Any) {
	println(line)
	System.out.flush()
}

// Returns the number of turns used.
fun runCase(n: Int, turnsRemaining: Int, isLastTestCase: Boolean): Int {
	val arr = IntArray(n) { it + 1 }
	while (isSorted(arr))
		arr.shuffle(random)

	var turnsUsed = 0
	while (true) {
		turnsUsed += 1
		// Print the array
		output(arr.joinToString(" "))

		// Read the partitions
		val partitionsMap = mutableMapOf<Int, MutableList<Int>>()
		try {
			val line = readLine() ?: error(INVALID_LINE_ERROR)
			val tokens = line.trim().split(' ')
			if (tokens.size != n)
				throw Error(UNEXPECTED_LENGTH_ERROR(n, tokens.size))
			for (i in 0 until n) {
				// Any token with more than 50 characters is assumed to not be a valid integer
				if (tokens[i].length > 50)
					throw Error(INVALID_INTEGER_ERROR)
				val x = tokens[i].toInt()
				if (x < 1 || x > n)
					throw Error(INVALID_PARTITION_VALUE_ERROR(1, n, x))
				if (x !in partitionsMap)
					partitionsMap[x] = mutableListOf()
				partitionsMap[x]?.add(i)
			}
		} catch (err: Error) {
			output(INVALID_OUTPUT)
			throw err
		} catch (exception: Throwable) {
			output(INVALID_OUTPUT)
			throw Error(INVALID_LINE_ERROR)
		}

		// Shuffle the array using the partitions
		shuffle(arr, partitionsMap.values)

		if (turnsUsed == turnsRemaining && (!isLastTestCase || !isSorted(arr))) {
			output(RUN_OUT_OF_TURNS)
			throw Error(RAN_OUT_OF_TURNS_ERROR)
		}

		// If the array is sorted, return the number of turns we used.
		if (isSorted(arr)) {
			output(IS_SORTED)
			return turnsUsed
		}
		// Otherwise, output that the array is not yet sorted.
		output(NOT_YET_SORTED)
	}
}

fun runCases(t: Int, n: Int, maxTurns: Int) {
	var turnsRemaining = maxTurns
	for (i in 1..t) {
		// Reset the seed for each case for stability.
		// The real judge may generate random numbers differently than the local
		// testing tool.
		random = Random(12345 + t * t * t + i)
		val turnsBefore = turnsRemaining
		try {
			turnsRemaining -= runCase(n, turnsRemaining, i == t)
			System.err.println("Case #$i:  ${turnsBefore - turnsRemaining}  $turnsRemaining")
		} catch (err: Error) {
			throw Error(CASE_FAILED_ERROR(i, err))
		}
	}

	val extraInput = readLine()
	if (extraInput != null)
		throw Error(ADDITIONAL_INPUT_ERROR(extraInput.take(100)))
}

fun main(argv: Array<String>) {
	if (argv.size != 1) {
		System.err.println("Bad usage. See the comment at the top of the file for usage instructions.")
		exitProcess(1)
	}
	val index = argv[0].toInt()
	val t = T_ARR[index]
	val n = N_ARR[index]
	val k = K_ARR[index]
	try {
		output("$t $n $k")
		try {
			runCases(t, n, k)
		} catch (err: Error) {
			System.err.println(err.message?.take(1000))
			exitProcess(1)
		}
	} catch (exception: Throwable) {
		output(INVALID_OUTPUT)
		System.err.println("JUDGE_ERROR! Internal judge exception: $exception".take(1000))
	}
}
