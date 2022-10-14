package r1a2

fun main() {
	val max = 1_000_000_000
	val t = readln().toInt()
	for (case in 1..t) {
		val n = readln().toInt()

		val a = mutableSetOf<Int>()
		var p = 1
		while (p <= max && a.size < n) {
			a.add(p)
			p *= 2
		}
		p = 3
		while (p <= max && a.size < n) {
			a.add(p)
			p++
		}
		println(a.joinToString(" "))

		val b = readln().split(' ').map { it.toInt() }
		val ab = (a + b).sorted().reversed()
		val abSum = ab.sumOf { it.toLong() }
		val cSum = abSum / 2
		val c = mutableListOf<Int>()
		var s = cSum
		for (e in ab) {
			if (e <= s) {
				s -= e
				c.add(e)
				if (s == 0L)
					break
			}
		}
		println(c.joinToString(" "))
	}
}
