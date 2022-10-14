package r1a2

import java.io.IOException
import kotlin.math.max
import kotlin.random.Random
import kotlin.system.exitProcess

const val RANDOM_SEED = 123456789
const val TEST_CASES = 100
const val MIN_AB = 1
const val MAX_AB = 1_000_000_000
const val MIN_N = 100
const val MAX_N = MIN_N
const val MAX_LINE_LENGTH = 100_000

const val INVALID_LINE_ERROR = "Couldn't read a valid line"
val TOO_LONG_LINE_ERROR = { s: Any -> "Line too long: $s characters" }
val WRONG_NUM_TOKENS_ERROR = { s1: Any, s2: Any, s3: Any -> "Wrong number of tokens, expected between $s1 and $s2, but got $s3" }
val NOT_INTEGER_ERROR = { s: Any -> "Not an integer: $s" }
val OUT_OF_BOUNDS_ERROR = { s: Any -> "$s is out of bounds" }
val CASE_ERROR = { s1: Any, s2: Any? -> "Case #$s1 failed: $s2" }
const val EXCEPTION_AFTER_END_ERROR = "Exception raised while reading input after all cases finish."
val ADDITIONAL_INPUT_ERROR = { s: Any -> "Additional input after all cases finish: $s" }
const val NUMBERS_ARE_NOT_UNIQUE = "Some provided numbers are not unique"
const val NUMBER_NOT_FROM_THE_SET = "Some provided numbers are missing in the original set"
const val WRONG_ANSWER = "Wrong answer"
const val INVALID_OUTPUT = -1

var random = Random(RANDOM_SEED)

fun input(): String? {
	try {
		return readLine()
	} catch (e: Exception) {
		error("$INVALID_LINE_ERROR: ${e.message}")
	}
}

fun choices(range: IntRange, k: Int): Set<Int> {
	val s = mutableSetOf<Int>()
	while (s.size < k) {
		s.add(range.random(random))
	}
	return s
}

fun sample(list: Collection<Int>, k: Int): MutableList<Int> {
	val s = mutableSetOf<Int>()
	while (s.size < k) {
		s.add(list.random(random))
	}
	return s.toMutableList()
}

fun runCase(testCase: Int): Boolean {
	random = Random(RANDOM_SEED + testCase)

	val n = random.nextInt(MIN_N, MAX_N + 1)

	output(n)

	// Read the set of N numbers provided by the solution.
	val a = readInts(
		input() ?: error(qr5.INVALID_LINE_ERROR),
		inclusiveMinN = n,
		inclusiveMaxN = n,
		inclusiveMinValue = MIN_AB,
		inclusiveMaxValue = MAX_AB
	)
	val aAsSet = a.toSet()

	if (a.size != aAsSet.size)
		error(NUMBERS_ARE_NOT_UNIQUE)

	// Print a set of N numbers which are:
	// 1. Different from whatever the solution provided
	// 2. Add up to an even number with the numbers provided by the solution.

	// N random numbers.
	val b = pickFromPool(n, choices((MIN_AB..MAX_AB), 2 * n), aAsSet)
	ensureParity(aAsSet, b)
	validateB(aAsSet, b, n)

	// Normalize B to ensure that shuffling produces consistent results.
	b.sort()
	b.shuffle(random)

	output(b.joinToString(" "))

	// Read the set of numbers the solution chosen for us.
	val c = readInts(
		input() ?: error(qr5.INVALID_LINE_ERROR),
		inclusiveMinN = 1,
		inclusiveMaxN = 2 * n - 1,
		inclusiveMinValue = MIN_AB,
		inclusiveMaxValue = MAX_AB
	)

	// Numbers should be unique.
	if (c.size != c.toSet().size)
		error(NUMBERS_ARE_NOT_UNIQUE)

	// Numbers should only come from the sets of the numbers above.
	val diff = (a + b).groupingBy { it }.eachCount().toMutableMap()
	for ((k, v) in c.groupingBy { it }.eachCount())
		diff[k] = (diff[k] ?: 0) - v
	if ((diff.values.minOrNull() ?: 0) < 0)
		error(NUMBER_NOT_FROM_THE_SET)

	// The sum of the chosen numbers should be equal to the half of the total sum.
	val aSum = a.sumOf { it.toLong() }
	val bSum = b.sumOf { it.toLong() }
	val cSum = c.sumOf { it.toLong() }
	val res = aSum + bSum == cSum * 2
	if (res)
		System.err.println("Case #$testCase: Correct -- a: $aSum, b: $bSum, c: $cSum")
	else
		System.err.println("Case #$testCase: Wrong -- a: $aSum, b: $bSum, c: ${(aSum + bSum) / 2} != $cSum")

	return res
}

/**
 * Returns n items from the pool which do not appear in aAsSet.
 * @param n number of items to return.
 * @param pool a sequence of elements to choose from.
 * @param aAsSet a set of elements which should not appear in the result.
 * @return List of n items from the pool which do not appear in aAsSet.
 */
fun pickFromPool(n: Int, pool: Set<Int>, aAsSet: Set<Int>): MutableList<Int> {
	// Remove the ones that are in A.
	val filteredPool = pool.filter { it !in aAsSet }
	// Pick N random numbers out of the pool.
	return sample(filteredPool, n)
}

/**
 * Modifies b in place to ensure that the sum of aAsSet and b is even.
 * While modifying b it ensures that modified elements do not also appear in aAsSet.
 * @param aAsSet a set of elements which should not appear in b.
 * @param b the list to modify.
 */
fun ensureParity(aAsSet: Set<Int>, b: MutableList<Int>) {
	val sumA = aAsSet.sum()
	if ((sumA + b.sum()) % 2 == 0)
		return

	var minB = b.minOrNull() ?: 0
	val n = b.size
	b.remove(minB)
	val bAsSet = b.toSet()
	minB += 1
	while (minB in bAsSet || minB in aAsSet && minB <= MAX_AB) {
		minB += 2
	}
	if (minB > MAX_AB)
		minB -= 2 * (n + 1)
	b.add(minB)
}

/**
 * Validate b to ensure that it complies with the problem statement.
 * @param aAsSet a set of elements which should not appear in b.
 * @param b the list to validate.
 * @param n the number of elements b should contain.
 */
fun validateB(aAsSet: Set<Int>, b: MutableList<Int>, n: Int) {
	assert(b.size == n) { "Incorrect length" }
	assert((b.minOrNull() ?: 0) >= MIN_AB && (b.maxOrNull() ?: 0) <= MAX_AB) { "Values out of range" }
	val bAsSet = b.toSet()
	assert(b.size == bAsSet.size) { "Non-unique elements" }
	assert(bAsSet.size == (bAsSet - aAsSet).size) { "Elements from A appear in B" }
	assert((aAsSet.sum() + b.sum()) % 2 == 0) { "Odd sum" }
}

fun parseInteger(line: String): Int {
	try {
		return line.toInt()
	} catch (error: NumberFormatException) {
		error(NOT_INTEGER_ERROR(line))
	}
}

fun readInts(line: String, inclusiveMinN: Int, inclusiveMaxN: Int,
		inclusiveMinValue: Int, inclusiveMaxValue: Int): List<Int> {
	if (line.length > max(MAX_LINE_LENGTH,
			inclusiveMaxN * (max(inclusiveMinValue.toString().length, inclusiveMaxValue.toString().length) + 5)))
		error(TOO_LONG_LINE_ERROR(line.length))

	val tokens = line.split(' ')
	if (tokens.size !in inclusiveMinN..inclusiveMaxN)
		error(WRONG_NUM_TOKENS_ERROR(inclusiveMinN, inclusiveMaxN, tokens.size))

	val ints = tokens.map { parseInteger(it) }

	val outOfBounds = ints.filter { it !in inclusiveMinValue..inclusiveMaxValue }
	if (outOfBounds.isNotEmpty())
		error(OUT_OF_BOUNDS_ERROR(outOfBounds))

	return ints
}

fun output(line: Any) {
	try {
		println(line)
		System.out.flush()
	} catch (e: Exception) {
		// If we let stdout be closed by the end of the program, then an unraisable
		// broken pipe exception will happen, and we won't be able to finish
		// normally.
		try {
			System.out.close()
		} catch (e: IOException) {
			// ignore
		}
	}
}

fun runCases() {
	var ok = true

	output(TEST_CASES)
	for (testCase in 1..TEST_CASES) {
		try {
			val testCaseOk = runCase(testCase)
			ok = ok && testCaseOk
		} catch (err: Throwable) {
			output(INVALID_OUTPUT)
			error(CASE_ERROR(testCase, err.message))
		}
	}

	val extraInput: String?
	try {
		extraInput = input()
	} catch (e: Exception) {
		error("$EXCEPTION_AFTER_END_ERROR ${e.message}")
	}
	if (extraInput == null) {
		if (!ok)
			error(WRONG_ANSWER)
		return
	}
	error(ADDITIONAL_INPUT_ERROR(extraInput.take(100)))
}

fun main() {
	try {
		runCases()
	} catch (err: IllegalStateException) {
		System.err.println(err.message?.take(1000))
		exitProcess(1)
	} catch (exception: Exception) {
		output(INVALID_OUTPUT)
		System.err.println("JUDGE_ERROR! Internal judge exception: $exception".take(1000))
		exitProcess(1)
	}
}
