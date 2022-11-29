package r1b3

fun main() {
	val t = readln().toInt()
	for (case in 1..t) {
		var n = 0
		for (i in 0 until 300) {
			val v = when (n) {
				0 -> "00000000"
				8 -> "11111111"
				else -> List(8) { idx -> if (idx < n) '1' else '0' }.shuffled().joinToString("")
			}
			println(v)
			n = readln().toInt()
			when (n) {
				0 -> break
				-1 -> System.exit(0)
			}
		}
	}
}
