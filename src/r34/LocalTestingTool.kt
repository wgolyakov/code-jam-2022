// "Win As Second" local testing tool.

// Usage: `LocalTestingTool test_number`, where the argument test_number
// is 0 (Test Set 1) or 1 (Test Set 2).
package r34

import java.io.IOException
import kotlin.random.Random
import kotlin.system.exitProcess

val MIN_N = intArrayOf(30, 31)
val MAX_N = intArrayOf(30, 40)
const val M = 50

class Error(message: String): Exception(message)

val WRONG_NUM_TOKENS_ERROR = { s1: Any, s2: Any -> "Wrong number of tokens: expected $s1, found $s2." }
val NOT_INTEGER_ERROR = { s: Any -> "Not an integer: $s." }
val OUT_OF_BOUNDS_ERROR = { s: Any -> "Request out of bounds: $s." }
const val INVALID_LINE_ERROR = "Couldn't read a valid line."
val ADDITIONAL_INPUT_ERROR = { s: Any -> "Additional input after all cases finish: $s." }
val CYCLE_ERROR = { s1: Any, s2: Any -> "The chosen edges form a cycle after edge $s1-$s2" }
const val UELI_WON_ERROR = "Ueli won"
val ALREADY_RED_ERROR = { s: Any -> "Trying to color vertex $s that is already red" }
val NOT_NEIGHBOR_ERROR = { s1: Any, s2: Any ->
	"Trying to color vertex $s1 that is not a neighbor of vertex $s2 that was colored first" }

val random = Random(12345)

fun readValues(line: String) = line.split(' ')

fun convertToInt(token: String, min: Int, max: Int): Int {
	val v: Int
	try {
		v = token.toInt()
	} catch (e: NumberFormatException) {
		throw Error(NOT_INTEGER_ERROR(token.take(100)))
	}
	if (v < min || v > max)
		throw Error(OUT_OF_BOUNDS_ERROR(v))
	return v
}

fun input(): String? {
	try {
		return readLine()
	} catch (e: Exception) {
		throw Error(INVALID_LINE_ERROR)
	}
}

fun output(line: String) {
	try {
		println(line)
		System.out.flush()
	} catch (e: Exception) {
		try {
			System.out.close()
		} catch (e: IOException) {
			// ignore
		}
	}
}

fun readKInts(k: Int, min: Int, max: Int): List<Int> {
	val line = input() ?: throw Error(INVALID_LINE_ERROR)
	val tokens = readValues(line)
	if (tokens.size != k)
		throw Error(WRONG_NUM_TOKENS_ERROR(k, tokens.size))
	return tokens.map { convertToInt(it, min, max) }
}

fun canWinInOneMove(adj: List<List<Int>>, alive: List<Boolean>): Boolean {
	val n = alive.size
	val aliveCount = alive.count { it }
	for (main in 0 until n) {
		if (alive[main]) {
			var got = 1
			for (other in adj[main])
				if (alive[other])
					got += 1
			if (got == aliveCount)
				return true
		}
	}
	return false
}

fun getRandomMove(adj: List<List<Int>>, alive: List<Boolean>): List<Int> {
	val main = alive.withIndex().filter { it.value }.map { it.index }.random(random)
	val res = mutableListOf(main)
	for (other in adj[main])
		if (alive[other] && random.nextBoolean())
			res.add(other)
	return res
}

fun runCases(min_n: Int, max_n: Int) {
	output("${max_n - min_n + 1}")
	for (n in min_n..max_n) {
		System.err.println("+++ N = $n +++")
		output("$n")

		val component = MutableList(n) { it }
		val adj = List(n) { mutableListOf<Int>() }
		repeat(n - 1) {
			var (p, q) = readKInts(2, 1, n)
			p -= 1
			q -= 1
			val cp = component[p]
			val cq = component[q]
			if (cp == cq)
				throw Error(CYCLE_ERROR(p + 1, q + 1))
			for (i in 0 until n)
				if (component[i] == cp)
					component[i] = cq
			adj[p].add(q)
			adj[q].add(p)
		}

		output("$M")
		repeat(M) { g ->
			System.err.println("### Game ${g + 1} ###")
			val alive = MutableList(n) { true }
			while (alive.any { it }) {
				if (canWinInOneMove(adj, alive))
					throw Error(UELI_WON_ERROR)
				val us = getRandomMove(adj, alive)
				for (x in us) {
					if (!alive[x]) error("")
					alive[x] = false
				}
				System.err.println("us: " + us.joinToString(" ") { "${it + 1}" })
				output("${us.size}")
				output(us.joinToString(" ") { "${it + 1}" })
				val (k) = readKInts(1, 1, n)
				val them = readKInts(k, 1, n).map { it - 1 }
				System.err.println("them: " + them.joinToString(" ") { "${it + 1}" })
				for (x in them) {
					if (!alive[x])
						throw Error(ALREADY_RED_ERROR(x + 1))
					alive[x] = false
					if (x != them[0])
						if (!adj[them[0]].any { it == x })
							throw Error(NOT_NEIGHBOR_ERROR(x + 1, them[0] + 1))
				}
			}
		}
	}

	val extraInput = input()
	if (extraInput != null)
		throw Error(ADDITIONAL_INPUT_ERROR(extraInput.take(100)))
}

fun main(argv: Array<String>) {
	if (argv.size != 1) {
		System.err.println("Please pass a single test_number argument")
		exitProcess(1)
	}
	val datasetIndex = argv[0].toInt()
	val minN = MIN_N[datasetIndex]
	val maxN = MAX_N[datasetIndex]
	try {
		val time = System.currentTimeMillis()
		runCases(minN, maxN)
		System.err.println("Time: ${(System.currentTimeMillis() - time) / 1000} sec")
	} catch (error: Error) {
		System.err.println(error.message)
		output("-1")
		System.err.println("status: INVALID")
		exitProcess(1)
	}
	System.err.println("status: VALID")
}
