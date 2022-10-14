package r22

import kotlin.math.*

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
		var count = 0L
		var countDiag = 0L
		for (x in 0 .. r) {
			for (y in 0 .. x) {
				val x2 = x.toDouble() * x.toDouble()
				val y2 = y.toDouble() * y.toDouble()
				val ra = round(sqrt(x2 + y2))
				if (ra <= r) {
					val ra2 = ra.toDouble() * ra.toDouble()
					if (x != round(sqrt(ra2 - y2))) {
						if (x == y)
							countDiag++
						else
							count++
					}
				}
			}
		}
		println("Case #$case: ${countDiag * 4 + count * 8}")
	}
}
