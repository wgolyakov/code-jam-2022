package wf4

import java.math.BigInteger

private const val CAT = 'C'
private const val NO = '.'
private const val UNKNOWN = '?'
private const val MOD = 1000000007

fun main() {
	val t = readln().toInt()
	for (case in 1..t) {
		val n = readln().toInt()
		val s = readln()
		val b = readln().split(' ').map { it.toInt() - 1 }
		val result = schrodingerAndPavlov(n, s, b)
		println("Case #$case: $result")
	}
}

private fun schrodingerAndPavlov(n: Int, s: String, b: List<Int>): Int {
	val bin = s.map { if (it == CAT) '1' else '0' }.toCharArray().concatToString().reversed()
	val state0 = BigInteger(bin, 2)
	val uCount = s.count { it == UNKNOWN }
	if (uCount == 0)
		return if (check(state0, n, b)) 1 else 0
	var uState = BigInteger.valueOf(0)
	val uIndex = IntArray(uCount)
	var j = 0
	for (i in 0 until n)
		if (s[i] == UNKNOWN) uIndex[j++] = i
	var lastCount = 0
	while (!uState.testBit(uCount)) {
		var state = state0
		for (i in 0 until uCount)
			if (uState.testBit(i)) state = state.setBit(uIndex[i])
		if (check(state, n, b)) {
			lastCount++
			if (lastCount == MOD) lastCount = 0
		}
		uState++
	}
	return lastCount
}

private fun check(state: BigInteger, n: Int, b: List<Int>): Boolean {
	var st = state
	for (i in 0 until n) {
		if (st.testBit(i) && !st.testBit(b[i]))
			st = st.clearBit(i).setBit(b[i])
	}
	return st.testBit(n - 1)
}
