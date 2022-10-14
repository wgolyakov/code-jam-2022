package r1c3

import java.util.Collections

fun main() {
	val mod = 1000000007
	val t = readln().toInt()
	for (case in 1..t) {
		val (m, k) = readln().split(' ').map { it.toInt() }
		val counters = Intranets2().count(m)
		val result = counters[k] * mod / counters.sum() + 1
		println("Case #$case: $result")
	}
}

class Intranets2 {
	fun count(m: Int): LongArray {
		val linkCount = m * (m - 1) / 2
		val links = mutableListOf<Pair<Int, Int>>()
		for (i in 1..m)
			for (j in (i + 1)..m)
				links.add(i to j)
		println("m: $m")
		println("linkCount: $linkCount")
		println("links: $links")
		println("all: ${factorial(linkCount)}")

		val counters = LongArray(m / 2 + 2)
		val priorities = IntArray(links.size) { it + 1 }.toMutableList()
		do {
			val activeLinks = mutableSetOf<Int>()
			for (i in 1..m) {
				var maxPrior = 0
				var maxLink = -1
				for (j in links.indices) {
					val link = links[j]
					if (link.first == i || link.second == i) {
						val prior = priorities[j]
						if (prior > maxPrior) {
							maxPrior = prior
							maxLink = j
						}
					}
				}
				activeLinks.add(maxLink)
			}
			val groups = mutableListOf<MutableSet<Int>>()
			for (j in activeLinks) {
				val link = links[j]
				val linkGroups = groups.filter { it.contains(link.first) || it.contains(link.second) }
				when (linkGroups.size) {
					0 -> {
						val group = mutableSetOf(link.first, link.second)
						groups.add(group)
					}
					1 -> {
						val group = linkGroups.first()
						group.add(link.first)
						group.add(link.second)
					}
					else -> {
						val group1 = linkGroups.first()
						val group2 = linkGroups.last()
						group1.add(link.first)
						group1.add(link.second)
						group1.addAll(group2)
						groups.remove(group2)
					}
				}
			}
			val groupCount = groups.size
			counters[groupCount] += 1L
		} while (nextPermutation(priorities))
		println("counters: ${counters.toList()}")
		return counters
	}

	private fun factorial(num: Int): Long {
		var result = 1L
		for (i in 2..num) result *= i
		return result
	}

	private fun nextPermutation(a: MutableList<Int>): Boolean {
		val n = a.size
		if (n == 0) return false
		var j = n - 2
		while (j != -1 && a[j] >= a[j + 1]) j--
		if (j == -1) return false
		var k = n - 1
		while (a[j] >= a[k]) k--
		Collections.swap(a, j, k)
		var l = j + 1
		var r = n - 1
		while (l < r) Collections.swap(a, l++, r--)
		return true
	}
}
