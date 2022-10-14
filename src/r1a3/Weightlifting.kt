package r1a3

import kotlin.math.min

fun main() {
	val t = readln().toInt()
	for (case in 1..t) {
		val (e, w) = readln().split(' ').map { it.toInt() }
		val x = Array(e) { intArrayOf() }
		for (i in 0 until e)
			x[i] = readln().split(' ').map { it.toInt() }.toIntArray()
		val wl = Weightlifting(e, w, x)
		val result = wl.dp(1, e) + 2 * wl.mc(1, e)
		println("Case #$case: $result")
	}
}

class Weightlifting(private val e: Int, private val w: Int, x: Array<IntArray>) {
	private val mc = Array(e) { IntArray(e) }
	private val dp = Array(e) { IntArray(e) }

	init {
		// Calculate table of mc
		for (l in 1..e) {
			for (r in l..e) {
				var c = x[l - 1].copyOf()
				for (i in (l + 1)..r) {
					val xi = x[i - 1]
					c = c.zip(xi) { c1, c2 -> min(c1, c2) }.toIntArray()
				}
				mc[l - 1][r - 1] = c.sum()
			}
		}
	}

	// Number of weights in c(l, r)
	fun mc(l: Int, r: Int): Int {
		return mc[l - 1][r - 1]
	}

	// Min number of operations to start with c(l, r), go through the exercises, and end at c(l, r)
	fun dp(l: Int, r: Int): Int {
		if (l == r)
			return 0
		val result = dp[l - 1][r - 1]
		if (result != 0)
			return result
		var minDp = Int.MAX_VALUE
		for (t in l until r) {
			val dp = dp(l, t) + 2 * (mc(l, t) - mc(l, r)) + dp(t + 1, r) + 2 * (mc(t + 1, r) - mc(l, r))
			if (dp < minDp)
				minDp = dp
		}
		dp[l - 1][r - 1] = minDp
		return minDp
	}
}
