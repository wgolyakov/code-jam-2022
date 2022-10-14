package r1c1

fun main() {
	fun valid(tower: String): Boolean {
		if (tower.length < 3)
			return true
		val letters = mutableSetOf<Char>()
		for (s in tower.windowed(2)) {
			val l1 = s[0]
			val l2 = s[1]
			letters.add(l1)
			if (l1 != l2 && letters.contains(l2))
				return false
		}
		return true
	}

	fun allSame(tower: String): Boolean {
		if (tower.length < 2)
			return true
		for (s in tower.windowed(2)) {
			val l1 = s[0]
			val l2 = s[1]
			if (l1 != l2)
				return false
		}
		return true
	}

	val t = readln().toInt()
	for (case in 1..t) {
		val n = readln().toInt()
		val s = readln().split(' ')
		var possible = true
		if (s.any { !valid(it) }) {
			possible = false
		} else {
			val byFirst = mutableMapOf<Char, String>()
			val byLast = mutableMapOf<Char, String>()
			val byAll = mutableMapOf<Char, String>()
			val same = mutableSetOf<String>()
			for (si in s) {
				if (allSame(si)) {
					byAll[si.first()] = byAll.getOrDefault(si.first(), "") + si
					same.add(si)
				} else {
					if (byFirst.put(si.first(), si) != null) {
						possible = false
						break
					}
					if (byLast.put(si.last(), si) != null) {
						possible = false
						break
					}
				}
			}
			if (possible) {
				val megaTower = mutableListOf<String>()
				for (si in s) {
					var tower = if (same.contains(si)) {
						byAll.remove(si.first())
					} else {
						val f = byFirst.remove(si.first())
						val l = byLast.remove(si.last())
						f ?: l
					}
					if (tower == null)
						continue
					do {
						var right = byAll.remove(tower.last())
						if (right != null)
							tower += right
						right = byFirst.remove(tower.last())
						if (right != null) {
							tower += right
							byLast.remove(right.last())
						}
					} while (right != null)
					do {
						var left = byAll.remove(tower!!.first())
						if (left != null)
							tower = left + tower
						left = byLast.remove(tower.first())
						if (left != null) {
							tower = left + tower
							byFirst.remove(left.first())
						}
					} while (left != null)
					megaTower.add(tower!!)
				}
				val result = megaTower.joinToString("")
				if (valid(result))
					println("Case #$case: $result")
				else
					possible = false
			}
		}
		if (!possible)
			println("Case #$case: IMPOSSIBLE")
	}
}
