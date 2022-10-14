// judge for ASeDatAb.
package r1b3

import kotlin.random.Random
import kotlin.system.exitProcess

const val T = 100
const val S = 8
const val MAX_TURNS = 300
const val INVALID_OUTPUT = -1
const val RUN_OUT_OF_TURNS = -1

class Error(message: String): Exception(message)

const val INVALID_LINE_ERROR = "Couldn't read a valid line."
const val RUN_OUT_OF_TURNS_ERROR = "Run out of turns"
val UNEXPECTED_LENGTH = { s1: Any, s2: Any -> "Expect line with length $s1, but actually get $s2" }
val UNEXPECTED_CHAR = { s: Any -> "Input contains unexpected character $s" }
val ADDITIONAL_INPUT_ERROR = { s: Any -> "Additional input after all cases finish: $s" }
val CASE_FAILED_ERROR = { s1: Any, s2: Any? -> "Case #$s1 failed: $s2" }
var random = Random(12345)

fun readValue(line: String): String {
	val str = line.trim()
	if (str.length != S)
		throw Error(UNEXPECTED_LENGTH(S, str.length))
	for (c in str)
		if (c != '1' && c != '0')
			throw Error(UNEXPECTED_CHAR(c))
	return str
}

fun getNewRecord(old_record: String, newp: String): String {
	var newRecord = ""
	for (i in 0 until S)
		newRecord += if (old_record[i] != newp[i]) '1' else '0'
	return newRecord
}

fun getNumberOfOne(record: String): Int {
	var numberOfOne = 0
	for (i in 0 until S)
		numberOfOne += if (record[i] == '1') 1 else 0
	return numberOfOne
}

fun output(line: Any) {
	println(line)
	System.out.flush()
}

fun runCase(): Boolean {
	// choose a random record that is not all 0
	var record = "0".repeat(S)
	while (record == "0".repeat(S)) {
		record = ""
		for (i in 0 until S)
			record += ('0'..'1').random(random)
	}
	//System.err.println(record)

	for (i in 0 until MAX_TURNS) {
		try {
			val line = readLine() ?: error(INVALID_LINE_ERROR)
			val p = readValue(line)
			var r = random.nextInt(S)
			// right rotate r is same as left rotate (S - r)
			r = S - r
			val newp = p.substring(r) + p.substring(0, r)
			record = getNewRecord(record, newp)
			val numberOfOne = getNumberOfOne(record)
			//System.err.println("$i - $p - $newp - $record - $numberOfOne")
			if (numberOfOne == 0) {
				// output 0 if the record is set to 0 and mark the test as completed.
				output(0)
				return true
			} else if (i < MAX_TURNS - 1) {
				// output the number of 1s in the record if it isn't the last turn
				output(numberOfOne)
			} else {
				// output -1 (Run out of turns) if it is the last turn and the record
				// is not yet set to 0. Also mark the test as failed
				output(RUN_OUT_OF_TURNS)
				return false
			}
		} catch (err: Error) {
			output(INVALID_OUTPUT)
			throw err
		} catch (exception: Throwable) {
			output(INVALID_OUTPUT)
			throw Error(INVALID_LINE_ERROR)
		}
	}
	return false
}

fun runCases(t: Int) {
	for (i in 1 until t + 1) {
		//System.err.println("####### $i ########")
		// The implementation of randomness here is not guaranteed to match the
		// implementation of randomness in the real judge.
		random = Random(2 + i)
		try {
			val res = runCase()
			System.err.println("Case #$i: $res")
			if (!res)
				throw Error(RUN_OUT_OF_TURNS_ERROR)
		} catch (err: Error) {
			throw Error(CASE_FAILED_ERROR(i, err.message))
		}
	}

	val extraInput = readLine()
	if (extraInput != null)
		throw Error(ADDITIONAL_INPUT_ERROR(extraInput.take(100)))
}

fun main() {
	try {
		output(T)
		try {
			runCases(T)
		} catch (err: Error) {
			System.err.println(err.message?.take(1000))
			exitProcess(1)
		}
	} catch (exception: Throwable) {
		output(INVALID_OUTPUT)
		System.err.println("JUDGE_ERROR! Internal judge exception: $exception".take(1000))
	}
}
