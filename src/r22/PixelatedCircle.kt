package r22

import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

fun main() {
	// round to the nearest integer, breaking ties towards zero
	fun round(a: Double): Int {
		val n = a.toInt()
		val d = a - n
		return if (d > 0.5) n + 1 else n
	}

	val t = readln().toInt()
	for (case in 1..t) {
		val r = readln().toInt()
		var filledCount = 0L
		for (x in -r..r)
			filledCount += floor(sqrt((0.5 + r) * (0.5 + r) - x.toDouble() * x.toDouble())).toLong() * 2 + 1
		var filledWrongCount = 0L
		for (ri in 1 .. r) {
			val xt1 = ceil(ri / sqrt(2.0)).toInt()
			val xt2 = floor(ri / sqrt(2.0)).toInt()
			val yt1 = round(sqrt(ri.toDouble() * ri.toDouble() - xt1.toDouble() * xt1.toDouble()))
			val yt2 = round(sqrt(ri.toDouble() * ri.toDouble() - xt2.toDouble() * xt2.toDouble()))
			val (xt, yt) = if (abs(xt1 - yt1) < abs(xt2 - yt2)) xt1 to yt1 else xt2 to yt2
			filledWrongCount += if (xt == yt) 2 * xt else 2 * xt + 1
		}
		filledWrongCount *= 4
		filledWrongCount += 1
		println("Case #$case: ${filledCount - filledWrongCount}")
	}
}
