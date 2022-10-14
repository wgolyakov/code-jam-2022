package r1c3

fun main() {
	val t = readln().toInt()
	for (case in 1..t) {
		val (m, k) = readln().split(' ').map { it.toInt() }
		val result = Intranets3(m, k).calculate()
		println("Case #$case: $result")
	}
}

class Intranets3(private val m: Int, private val k: Int) {
	private val mod = 1000000007
	private val links = m * (m - 1) / 2
	private var pRes = 0.0

	fun calculate(): Int {
		dp(1, 2, 0, 1, 1.0)
		if (pRes == 0.0) return 0
		if (pRes == 1.0) return 1
		return (pRes * mod).toInt() + 1
	}

	/**
	 * We assign next highest priority to the next edge
	 * @param i Assigned edges count
	 * @param s Assigned vertices count
	 * @param sp Previous assigned vertices count
	 * @param intranets Intranets count
	 * @param pp Previous probability
	 */
	private fun dp(i: Int, s: Int, sp: Int, intranets: Int, pp: Double) {
		val prob = prob(i, s, sp)
		val p = pp * prob
		if (i == links && intranets == k)
			pRes += p
		if (i < links && prob > 0) {
			if (s + 2 <= m && intranets < k)
				dp(i + 1, s + 2, s, intranets + 1, p)
			if (s + 1 <= m)
				dp(i + 1, s + 1, s, intranets, p)
			dp(i + 1, s, s, intranets, p)
		}
	}

	// Probability to assign priority to the next edge
	private fun prob(i: Int, s: Int, sp: Int): Double {
		var p = when (s) {
			sp + 2 -> ((m - sp) * (m - sp - 1) / 2.0) / (links - (i - 1))
			sp + 1 -> (sp * (m - sp)).toDouble() / (links - (i - 1))
			sp -> (sp * (sp - 1) / 2.0 - (i - 1)) / (links - (i - 1))
			else -> error("Wrong s: $s")
		}
		if (p < 0) p = 0.0 else if (p > 1) p = 1.0
		return p
	}
}
