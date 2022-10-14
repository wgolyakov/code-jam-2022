package r1c2

fun main() {
	val t = readln().toInt()
	for (case in 1..t) {
		val (n, k) = readln().split(' ').map { it.toInt() }
		val e = readln().split(' ').map { it.toLong() }
		var m = 0L
		for (i in 0 until n)
			for (j in i + 1 until n)
				m += e[i] * e[j]
		if (m == 0L) {
			println("Case #$case: 0")
			continue
		}
		val s = e.sum()
		if (s == 0L) {
			println("Case #$case: IMPOSSIBLE")
			continue
		}
		if (m % s == 0L) {
			println("Case #$case: ${-m / s}")
			continue
		}
		if (k == 1) {
			println("Case #$case: IMPOSSIBLE")
			continue
		}
		val z1 = 1L - s
		val z2 = -m - s * z1
		println("Case #$case: $z1 $z2")
	}
}
