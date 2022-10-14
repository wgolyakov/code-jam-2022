package qr1

fun main() {
	val t = readln().toInt()
	for (case in 1..t) {
		val (r, c) = readln().split(' ').map { it.toInt() }
		println("Case #$case:")
		for (y in 0 until r) {
			if (y == 0) {
				print("..+")
				for (x in 0 until c - 1)
					print("-+")
				println()
				print(".")
			} else
				print("|")
			for (x in 0 until c)
				print(".|")
			println()
			print("+")
			for (x in 0 until c)
				print("-+")
			println()
		}
	}
}
