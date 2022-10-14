package r31

fun main() {
	val (t, n, k) = readln().split(' ').map { it.toInt() }
	for (case in 1..t) {
		for (i in 0 until k) {
			val b = readln().split(' ').map { it.toInt() }.toIntArray()

			val c = IntArray(n)
			val chains = mutableListOf<MutableList<Int>>()
			val inChain = BooleanArray(n)
			for (j in 0 until n) {
				if (b[j] == j + 1) {
					c[j] = j + 1
				} else if (!inChain[b[j] - 1]) {
					var p = b[j]
					val chain = mutableListOf<Int>()
					do {
						chain.add(p)
						inChain[p - 1] = true
						p = b[p - 1]
					} while (p != chain[0])
					chains.add(chain)
				}
			}
			val s = 7
			for (chain in chains)
				for (j in chain.indices step s)
					for (r in 0 until s)
						if (j + r < chain.size)
							c[chain[j + r] - 1] = chain[j]
			println(c.joinToString(" "))

			val res = readln().toInt()
			if (res == -1) System.exit(-1)
			if (res == 1) break
		}
	}
}
