package r1c3

import java.math.BigInteger

fun main() {
	val t = readln().toInt()
	for (case in 1..t) {
		val (m, k0) = readln().split(' ').map { it.toInt() }
		val result = Intranets5(m, k0).calculate()
		println("Case #$case: $result")
	}
}

class Intranets5(private val m: Int, private val k0: Int) {
	private val mod = 1000000007

	private fun modInverse(x: Int) = BigInteger.valueOf(x.toLong()).modInverse(BigInteger.valueOf(mod.toLong())).toInt()

	fun calculate(): Int {
		// Table of priority by vertices and intranets
		val dp = Array(m + 1) { Array(k0 + 1) { Rational.ZERO } }
		dp[0][0] = Rational.ONE
		for (j in 0 until m) {
			for (k in 0 .. k0) {
				val p = dp[j][k]
				if (j < m - 1 && k < k0) {
					// Probability of new intranet after assigning next highest priority to the next edge
					val pNew = p * Rational(m - j - 1, m + j - 1)
					val jNew = j + 2
					val kNew = k + 1
					dp[jNew][kNew] = dp[jNew][kNew] + pNew
				}
				// Probability of no new intranet after assigning next highest priority to the next edge
				val pNew = p * Rational(j * 2, m + j - 1)
				val jNew = j + 1
				val kNew = k
				dp[jNew][kNew] = dp[jNew][kNew] + pNew
			}
		}
		val res = dp[m][k0]
		return ((res.n.toLong() * modInverse(res.d)) % mod).toInt()
	}
}
