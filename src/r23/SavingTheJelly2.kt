package r23

fun main() {
	class Sweet(val number: Int, val distance: Long)
	class Child(val number: Int, val sweets: MutableList<Sweet>)

	fun distance(a: IntArray, b: IntArray) = (a[0] - b[0]).toLong() * (a[0] - b[0]) + (a[1] - b[1]).toLong() * (a[1] - b[1])

	val t = readln().toInt()
	for (case in 1..t) {
		val n = readln().toInt()
		val a = Array(n) { intArrayOf() }
		for (i in 0 until n)
			a[i] = readln().split(' ').map { it.toInt() }.toIntArray()
		val b = Array(n + 1) { intArrayOf() }
		for (j in 0 until n + 1)
			b[j] = readln().split(' ').map { it.toInt() }.toIntArray()

		val allChildren = a.withIndex().map { (i, ai) -> Child(i, b.withIndex().map { (j, bj) -> Sweet(j, distance(ai, bj)) }
			.sortedWith(compareBy({ it.distance }, { -it.number })).toMutableList()) }
		if (allChildren.any { it.sweets.first().number == 0 }) {
			println("Case #$case: IMPOSSIBLE")
			continue
		}
		val children = allChildren.map { Child(it.number, it.sweets.subList(0, it.sweets.indexOfFirst { s -> s.number == 0 })
			.toMutableList()) }.sortedBy { it.sweets.size }.toMutableList()
		val result = mutableListOf<Pair<Int, Int>>()
		var possible = true
		for (i in 0 until n) {
			val child = children.first()
			if (child.sweets.isEmpty()) {
				possible = false
				break
			}
			val sweet = child.sweets.first()
			result.add(child.number to sweet.number)
			children.removeAt(0)
			children.forEach { ch -> ch.sweets.removeIf { it.number == sweet.number } }
			children.sortBy { it.sweets.size }
		}

		if (possible) {
			println("Case #$case: POSSIBLE")
			for (r in result)
				println("${r.first + 1} ${r.second + 1}")
		} else {
			println("Case #$case: IMPOSSIBLE")
		}
	}
}
