package qr5

import java.io.IOException
import kotlin.random.Random
import kotlin.system.exitProcess

const val NUM_CASES = 100
const val N = 1000000
const val K = 8000
const val NEED_CORRECT = 90

const val START_CAVE = true

val WRONG_NUM_TOKENS_ERROR = { s1: Any, s2: Any -> "Wrong number of tokens: expected $s1, found $s2." }
val NOT_INTEGER_ERROR = { s: Any -> "Not an integer: $s." }
const val INVALID_LINE_ERROR = "Couldn't read a valid line."
val ADDITIONAL_INPUT_ERROR = { s: Any -> "Additional input after all cases finish: $s." }
val OUT_OF_BOUNDS_ERROR = { s: Any -> "Request out of bounds: $s." }
val TOO_FEW_CORRECT_ERROR = { s: Any -> "Too few correct answers: $s." }
val INVALID_COMMAND_ERROR = { s: Any -> "couldn't understand player command: $s." }
const val DID_NOT_GIVE_AN_ESTIMATE_ERROR = "Player did not give an estimate after K rounds."

val random = Random(12345)

fun readValues(line: String) = line.split(' ')

fun convertToInt(token: String, min: Int, max: Int): Int {
	val v: Int
	try {
		v = token.toInt()
	} catch (e: NumberFormatException) {
		error(NOT_INTEGER_ERROR(token.take(100)))
	}
	if (v < min || v > max)
		error(OUT_OF_BOUNDS_ERROR(v))
	return v
}

fun convertToAnyInt(token: String): Long {
	val v: Long
	try {
		v = token.toLong()
	} catch (e: NumberFormatException) {
		error(NOT_INTEGER_ERROR(token.take(100)))
	}
	return v
}

fun input(): String? {
	try {
		return readLine()
	} catch (e: Exception) {
		error(INVALID_LINE_ERROR)
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

fun runCases() {
	output("$NUM_CASES")
	var correct = 0
	for (caseNumber in 0 until NUM_CASES) {
		output("$N $K")

		// Construct a graph in adj.
		val adj = Array(N) { mutableListOf<Int>() }
		var correctTotalEdges = 0L
		val order = Array(N) { it }
		order.shuffle(random)
		if (START_CAVE) {
			val v1 = order[0]
			for (i in 1 until N) {
				val v2 = order[i]
				adj[v1].add(v2)
				adj[v2].add(v1)
				correctTotalEdges++
			}
		} else {
			for (i in 0 until N step 2) {
				val v1 = order[i]
				val v2 = order[i + 1]
				adj[v1].add(v2)
				adj[v2].add(v1)
				correctTotalEdges++
			}
		}
		var add = random.nextInt(4 * N + 1)
		add = random.nextInt(add + 1)
		for (j in 0 until add) {
			val v1 = random.nextInt(N)
			val v2 = random.nextInt(N)
			if (v1 != v2 && v2 !in adj[v1] && adj[v1].size < N - 2 && adj[v2].size < N - 2) {
				adj[v1].add(v2)
				adj[v2].add(v1)
				correctTotalEdges++
			}
		}
		val complement = random.nextBoolean()
		if (complement)
			correctTotalEdges = (N.toLong() * (N.toLong() - 1)) / 2 - correctTotalEdges

		// Play the game.
		var where = random.nextInt(N)
		for (move_number in 0 until K + 1) {
			// Output current room number (1-based) && number of adjacent passages.
			if (complement)
				output("${where + 1} ${N - 1 - adj[where].size}")
			else
				output("${where + 1} ${adj[where].size}")

			// Get the user's move.
			val line = input() ?: error(INVALID_LINE_ERROR)
			val move = readValues(line)

			if (move.isEmpty())
				error(INVALID_LINE_ERROR)

			if (move[0] == "E") {
				// The user provided an estimate.
				if (move.size != 2)
					error(WRONG_NUM_TOKENS_ERROR(2, move.size))
				val estimate = convertToAnyInt(move[1])
				val lo = (correctTotalEdges * 2 + 2) / 3
				val hi = (correctTotalEdges * 4) / 3
				if (estimate in lo..hi) {
					System.err.println("Case #${caseNumber + 1}: Correct -- got $estimate; exact answer is $correctTotalEdges.")
					correct += 1
				} else
					System.err.println("Case #${caseNumber + 1}: Wrong -- got $estimate; exact answer is $correctTotalEdges; acceptable range is [$lo, $hi].")
				// Go to the next test case.
				break
			} else if (move_number == K) {
				// The cave is now closed!
				error(DID_NOT_GIVE_AN_ESTIMATE_ERROR)
			} else if (move[0] == "W") {
				// The user took a random exit.
				if (move.size != 1)
					error(WRONG_NUM_TOKENS_ERROR(1, move.size))
				if (complement) {
					while (true) {
						val next = random.nextInt(N)
						// NOTE: The check for (next != where) was not present at the
						// beginning of contest. This would, in rare occasions, introduce
						// self-loops. This bug was never present in the real judge.
						if (next != where && next !in adj[where]) {
							where = next
							break
						}
					}
				} else {
					val l = adj[where]
					where = l[random.nextInt(l.size)]
				}
			} else if (move[0] == "T") {
				// The user teleported to a room.
				if (move.size != 2)
					error(WRONG_NUM_TOKENS_ERROR(1, move.size))
				where = convertToInt(move[1], 1, N)
				where -= 1
			} else
				error(INVALID_COMMAND_ERROR(move[0].take(1000)))
		}
	}

	// Check there is no extraneous input from the user.
	val extraInput = input()
	if (extraInput != null)
		error(ADDITIONAL_INPUT_ERROR(extraInput.take(100)))

	// Finished.
	System.err.println("User got $correct cases correct.")
	if (correct < NEED_CORRECT)
		error(TOO_FEW_CORRECT_ERROR(correct))
}

fun main(argv: Array<String>) {
	if (argv.size == 2 && argv[1].toInt() < 0)
		exitProcess(0)
	try {
		runCases()
	} catch (error: Exception) {
		System.err.println(error.message)
		exitProcess(1)
	}
}
