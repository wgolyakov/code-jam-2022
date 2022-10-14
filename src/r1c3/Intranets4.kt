package r1c3

import java.math.BigInteger

fun main() {
	val t = readln().toInt()
	for (case in 1..t) {
		val (m, k) = readln().split(' ').map { it.toInt() }
		val result = Intranets4(m, k).calculate()
		println("Case #$case: $result")
	}
}

class Intranets4(private val m: Int, private val k: Int) {
	private val mod = 1000000007
	private var pRes = Rational.ZERO

	fun calculate(): Int {
		dp(0, 0, Rational.ONE)
		return ((pRes.n.toLong() * modInverse(pRes.d)) % mod).toInt()
	}

	/**
	 * We assign next highest priority to the next edge
	 * @param s Assigned vertices count
	 * @param intranets Intranets count
	 * @param pp Previous probability
	 */
	private fun dp(s: Int, intranets: Int, pp: Rational) {
		val p = prob(s)
		if (p == Rational.ZERO) {
			if (intranets == k)
				pRes += pp
			return
		}
		if (s + 2 <= m && intranets < k)
			dp(s + 2, intranets + 1, pp * p)
		if (s + 1 <= m && p < Rational.ONE)
			dp(s + 1, intranets, pp * (Rational.ONE - p))
	}

	// The probability of there being a new intranet
	private fun prob(s: Int): Rational {
		if (s == m) return Rational.ZERO
		return Rational(m - s - 1, m + s - 1)
	}

	private fun modInverse(x: Int) = BigInteger.valueOf(x.toLong()).modInverse(BigInteger.valueOf(mod.toLong())).toInt()
}
