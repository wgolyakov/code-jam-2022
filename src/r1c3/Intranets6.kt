package r1c3

import java.math.BigInteger

fun main() {
	val t = readln().toInt()
	for (case in 1..t) {
		val (m, k0) = readln().split(' ').map { it.toInt() }
		val result = Intranets6(m, k0).calculate()
		println("Case #$case: $result")
	}
}

class Intranets6(private val m: Int, private val k0: Int) {
	private val mod = 1000000007

	fun calculate(): Int {
		// Table of priority by vertices and intranets
		var dp = Array(3) { Array(k0 + 1) { Rational.ZERO } }
		dp[0][0] = Rational.ONE
		for (j in 0 until m) {
			for (k in 0..k0) {
				val p = dp[0][k]
				if (j < m - 1 && k < k0) {
					// Probability of new intranet after assigning next highest priority to the next edge
					dp[2][k + 1] = p * Rational(m - j - 1, m + j - 1)
				}
				// Probability of no new intranet after assigning next highest priority to the next edge
				dp[1][k] += p * Rational(j * 2, m + j - 1)
			}
			dp = arrayOf(dp[1], dp[2], dp[0])
			dp[2][0] = Rational.ZERO
		}
		val res = dp[0][k0]
		return ((res.n.toLong() * modInverse(res.d)) % mod).toInt()
	}

	private fun modInverse(x: Int) = BigInteger.valueOf(x.toLong()).modInverse(BigInteger.valueOf(mod.toLong())).toInt()
}
