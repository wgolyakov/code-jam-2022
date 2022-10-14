package r32

import kotlin.math.min

fun main() {
	val t = readln().toInt()
	for (case in 1..t) {
		val (n, c) = readln().split(' ').map { it.toInt() }
		val a = IntArray(c)
		val b = IntArray(c)
		for (i in 0 until c) {
			val (ai, bi) = readln().split(' ').map { it.toInt() }
			a[i] = ai
			b[i] = bi
		}
		val p = readln().split(' ').map { it.toInt() }

		val maxSize = min(b.sum(), p.size - 1)
		var result = 0
		for (size in 2..maxSize) {
			for (j in 0 until n) {
				val geese = if (j + size <= n)
					p.subList(j, j + size)
				else
					p.subList(j, n) + p.subList(0, size - (n - j))
				var valid = true
				val r = IntArray(c)
				for (g in geese)
					r[g - 1]++
				for (i in 0 until c) {
					if (r[i] != 0 && (r[i] < a[i] || r[i] > b[i])) {
						valid = false
						break
					}
				}
				if (valid) result++
			}
		}

		println("Case #$case: $result")
	}
}
