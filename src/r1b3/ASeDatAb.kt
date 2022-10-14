package r1b3

fun main() {
	fun appendZero(s: String) = s + "0".repeat(s.length)

	fun expand(s: String) = s + s

	fun p(k: Int): List<String> {
		if (k == 0)
			return listOf("1")
		val seq = p(k - 1)
		val seqWithZero = seq.map { appendZero(it) }
		val seqWithCopy = seq.map { expand(it) }
		val res = seqWithCopy.toMutableList()
		for (ins in seqWithZero) {
			res.add(ins)
			res.addAll(seqWithCopy)
		}
		return res
	}

	val magicSequence = p(3)

	val t = readln().toInt()
	for (case in 1..t) {
		for (v in magicSequence) {
			println(v)
			val n = readln().toInt()
			when (n) {
				0 -> break
				-1 -> System.exit(0)
			}
		}
	}
}
