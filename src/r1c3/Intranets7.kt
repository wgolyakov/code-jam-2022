package r1c3

import java.math.BigInteger

fun main() {
	val t = readln().toInt()
	for (case in 1..t) {
		val (m, k0) = readln().split(' ').map { it.toInt() }
		val result = Intranets7(m, k0).calculate()
		println("Case #$case: $result")
	}
}

class Intranets7(private val m: Int, private val k0: Int) {
	private val mod = 1000000007
	private val modBig = BigInteger.valueOf(mod.toLong())

	fun calculate(): Int {
		// Table of priority by vertices and intranets
		var dp = Array(3) { Array(k0 + 1) { 0 } }
		dp[0][0] = 1
		for (j in 0 until m) {
			for (k in 0..k0) {
				val p = dp[0][k]
				if (j < m - 1 && k < k0) {
					// Probability of new intranet after assigning next highest priority to the next edge
					dp[2][k + 1] = mulMod(p, divMod(m - j - 1, m + j - 1))
				}
				// Probability of no new intranet after assigning next highest priority to the next edge
				dp[1][k] = addMod(dp[1][k], mulMod(p, divMod(j * 2, m + j - 1)))
			}
			dp = arrayOf(dp[1], dp[2], dp[0])
			dp[2][0] = 0
		}
		return dp[0][k0]
	}

	private fun addMod(a: Int, b: Int) = mod(a.toLong() + b)
	private fun mulMod(a: Int, b: Int) = mod(a.toLong() * b)
	private fun divMod(a: Int, b: Int) = mod(a.toLong() * modInverse(b))
	private fun mod(x: Long) = (x % mod).toInt()
	private fun modInverse(x: Int) = BigInteger.valueOf(x.toLong()).modInverse(modBig).toInt()
}
