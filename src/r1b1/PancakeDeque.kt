package r1b1

fun main() {
	val t = readln().toInt()
	for (case in 1..t) {
		val n = readln().toInt()
		val d = readln().split(' ').map { it.toInt() }.toMutableList()
		var payCount = 0
		var maxLevel = 0
		while (d.isNotEmpty()) {
			val level = if (d.first() < d.last()) d.removeFirst() else d.removeLast()
			if (level >= maxLevel) {
				payCount++
				maxLevel = level
			}
		}
		println("Case #$case: $payCount")
	}
}
