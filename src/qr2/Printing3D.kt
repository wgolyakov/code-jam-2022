package qr2

fun main() {
	val t = readln().toInt()
	for (case in 1..t) {
		val min = Array(4) { 1_000_000L }
		for (printer in 0 until 3) {
			val colors = readln().split(' ').map { it.toLong() }.toTypedArray()
			for ((i, c) in colors.withIndex())
				if (c < min[i])
					min[i] = c
		}
		val sum = min.sum()
		val delta = sum - 1_000_000L
		val result = if (delta < 0)
			"IMPOSSIBLE"
		else {
			if (delta > 0) {
				var d = delta
				for (i in min.indices) {
					val x = min[i] * delta / sum
					min[i] -= x
					d -= x
				}
				if (d > 0) {
					for (i in min.indices) {
						if (min[i] > 0 && d > 0) {
							min[i]--
							d--
						}
					}
				}
			}
			min.joinToString(" ")
		}
		println("Case #$case: $result")
	}
}
