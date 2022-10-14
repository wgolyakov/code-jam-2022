package qr4

fun main() {
	val t = readln().toInt()
	for (case in 1..t) {
		val n = readln().toInt()
		val f = readln().split(' ').map { it.toInt() }
		val p = readln().split(' ').map { it.toInt() }
		val initiators = Array(n) { it + 1 }.toList() - p.toSet()
		var maxFun = 0
		val iniIndexes = Array(initiators.size) { 0 }
		var done = false
		while (!done) {
			if (iniIndexes.toSet().size == iniIndexes.size) {
				val usedModules = mutableSetOf<Int>()
				var sum = 0
				for (i in iniIndexes) {
					var m = initiators[i]
					var max = 0
					while (m != 0 && !usedModules.contains(m)) {
						if (f[m - 1] > max)
							max = f[m - 1]
						usedModules.add(m)
						m = p[m - 1]
					}
					sum += max
				}
				if (sum > maxFun)
					maxFun = sum
			}
			for (j in initiators.indices) {
				iniIndexes[j]++
				if (iniIndexes[j] <= initiators.size - 1)
					break
				iniIndexes[j] = 0
				if (j == initiators.size - 1)
					done = true
			}
		}
		println("Case #$case: $maxFun")
	}
}
