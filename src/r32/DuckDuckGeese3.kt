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
		var result = 0L
		val r = IntArray(c)
		var s1 = 0
		var s2 = 0
		r[p[s1] - 1]++
		for (size in 2..maxSize) {
			s2++
			r[p[s2] - 1]++
			for (j in 0 until n) {
				var valid = true
				for (i in 0 until c) {
					if (r[i] != 0 && (r[i] < a[i] || r[i] > b[i])) {
						valid = false
						break
					}
				}
				if (valid) result++
				r[p[s1] - 1]--
				s1++
				s2++
				if (s1 >= n) s1 -= n
				if (s2 >= n) s2 -= n
				r[p[s2] - 1]++
			}
		}

		println("Case #$case: $result")
	}
}

//class DuckDuckGeese {
//}

//val r = mutableMapOf<Int, Int>()
//r.merge(p[g] - 1, 1, Int::plus)
//for ((i, v) in r) {
//	if (v != 0 && (v < a[i] || v > b[i])) {
//r.compute(p[s1] - 1) { _, v -> if (v == 1) null else v!! - 1 }
//r.merge(p[s2] - 1, 1, Int::plus)
