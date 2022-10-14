package qr3

fun main() {
	val t = readln().toInt()
	for (case in 1..t) {
		val n = readln().toInt()
		val s = readln().split(' ').map { it.toInt() }.sorted()
		var j = 1
		for (si in s)
			if (si >= j)
				j++
		println("Case #$case: ${j - 1}")
	}
}
