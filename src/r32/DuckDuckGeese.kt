package r32

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

		val cn = IntArray(c)
		for (j in 0 until n)
			cn[p[j] - 1]++
		var goodColors = 0
		var badColors = 0
		for (i in 0 until c) {
			if (cn[i] == 0 || (a[i] <= 1 && cn[i] <= b[i]))
				goodColors++
			if (cn[i] != 0 && cn[i] < a[i])
				badColors++
		}

		var result = 0L
		if (goodColors == c) {
			result = n.toLong() * (n - 2)
		} else if (badColors == c) {
			result = 0
		} else {
			for (j in 0 until n) {
				val r = IntArray(c)
				var validColors = c
				var k = j
				var i = p[j] - 1
				r[i]++
				if (r[i] < a[i] || r[i] > b[i])	validColors--
				for (size in 2 until n) {
					k++
					if (k >= n) k -= n
					i = p[k] - 1
					val validBefore = r[i] == 0 || (r[i] >= a[i] && r[i] <= b[i])
					r[i]++
					if (r[i] > b[i]) break
					val validAfter = r[i] >= a[i] && r[i] <= b[i]
					if (validBefore) {
						if (!validAfter) validColors--
					} else {
						if (validAfter) validColors++
					}
					if (validColors == c) result++
				}
			}
		}

		println("Case #$case: $result")
	}
}
