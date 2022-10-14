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

// {0=1, 1=8, 2=28, 3=56, 4=70, 5=56, 6=28, 7=8, 8=1}
//val bc = mutableMapOf<Int, Int>()
//for (b in 0 until 256) {
//	val key = b.countOneBits()
//	bc[key] = bc.getOrDefault(key, 0) + 1
//}
//println(bc)
//return

//class ASeDatAb {
//}

//	val num = listOf(
//		listOf('1', '0', '0', '0', '0', '0', '0', '0'),
//		listOf('1', '1', '0', '0', '0', '0', '0', '0'),
//		listOf('1', '1', '1', '0', '0', '0', '0', '0'),
//		listOf('1', '1', '1', '1', '0', '0', '0', '0'),
//		listOf('1', '1', '1', '1', '1', '0', '0', '0'),
//		listOf('1', '1', '1', '1', '1', '1', '1', '0'),
//		listOf('1', '1', '1', '1', '1', '1', '1', '0'),
//	)
//num[n - 1].shuffled().joinToString("")
