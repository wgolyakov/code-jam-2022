package r23

fun main() {
	val t = readln().toInt()
	for (case in 1..t) {
		val n = readln().toInt()
		val a = Array(n) { intArrayOf() }
		for (i in 0 until n)
			a[i] = readln().split(' ').map { it.toInt() }.toIntArray()
		val b = Array(n + 1) { intArrayOf() }
		for (j in 0 until n + 1)
			b[j] = readln().split(' ').map { it.toInt() }.toIntArray()
		val result = SavingTheJelly(n, a, b).run()
		if (result.isEmpty()) {
			println("Case #$case: IMPOSSIBLE")
		} else {
			println("Case #$case: POSSIBLE")
			for (r in result)
				println("${r.first + 1} ${r.second + 1}")
		}
	}
}

class SavingTheJelly(private val n: Int, private val a: Array<IntArray>, private val b: Array<IntArray>) {
	inner class Child(val number: Int, val sweets: MutableList<Sweet>) {
		override fun toString() = "$number: $sweets"
	}

	inner class Sweet(val number: Int, val distance: Long) {
		override fun toString() = number.toString()
	}

	private val pairU = IntArray(n)
	private val pairV = IntArray(n + 1)
	private val dist = IntArray(n + 2)
	private val queue = ArrayDeque<Int>()
	private var adj = listOf<List<Int>>()
	private val nil = n + 1
	private val infinity = Int.MAX_VALUE

	fun run() : List<Pair<Int, Int>> {
		var children = a.withIndex().map { (i, ai) ->
			Child(i, b.withIndex().map { (j, bj) -> Sweet(j, distance(ai, bj)) }
				.sortedWith(compareBy({ it.distance }, { -it.number })).toMutableList()) }.toMutableList()
		if (children.any { it.sweets.first().number == 0 })
			return emptyList()
		// Remove edges to Mr. Jolly's sweet and farther
		children = children.map { Child(it.number,
			it.sweets.subList(0, it.sweets.indexOfFirst { s -> s.number == 0 }).toMutableList()) }.toMutableList()
		adj = children.map { it.sweets.map { s -> s.number } }
		val matching = hopcroftKarp()
		if (matching < n)
			return emptyList()
		val result = mutableListOf<Pair<Int, Int>>()
		while (children.isNotEmpty()) {
			removeMatchedClosestEdges(children, result)
			if (children.isEmpty()) break
			swapCycleEdges(children)
		}
		return result
	}

	private fun distance(a: IntArray, b: IntArray) =
		(a[0] - b[0]).toLong() * (a[0] - b[0]) + (a[1] - b[1]).toLong() * (a[1] - b[1])

	private fun removeMatchedClosestEdges(children: MutableList<Child>, result: MutableList<Pair<Int, Int>>) {
		do {
			var found = false
			for (i in children.indices.reversed()) {
				val child = children[i]
				if (child.sweets.isEmpty()) continue
				val j = pairU[child.number]
				val sweet = child.sweets.first()
				if (sweet.number == j) {
					result.add(child.number to sweet.number)
					children.removeAt(i)
					children.forEach { ch -> ch.sweets.removeIf { it.number == sweet.number } }
					found = true
				}
			}
		} while (found)
	}

	private fun swapCycleEdges(children: List<Child>) {
		val cycle = mutableSetOf<Int>()
		val path = mutableListOf<Pair<Int, Int>>()
		var child = children.first()
		while (!cycle.contains(child.number)) {
			val sweet = child.sweets.first()
			val edgeUnmatched = child.number to sweet.number
			path.add(edgeUnmatched)
			cycle.add(child.number)
			val i = pairV[sweet.number]
			child = children.find { it.number == i }!!
			val edgeMatched = child.number to sweet.number
			path.add(edgeMatched)
		}
		for (e in path.indices.reversed() step 2) {
			val edgeUnmatched = path[e - 1]
			val edgeMatched = path[e]
			pairU[edgeUnmatched.first] = edgeMatched.second
			pairV[edgeMatched.second] = edgeUnmatched.first
			if (edgeUnmatched.first == child.number) break
		}
	}

	private fun hopcroftKarp(): Int {
		for (u in 0 until n)
			pairU[u] = nil
		for (v in 0 until n + 1)
			pairV[v] = nil
		var matching = 0
		while (bfs()) {
			for (u in 0 until n)
				if (pairU[u] == nil)
					if (dfs(u))
						matching++
		}
		return matching
	}

	private fun bfs(): Boolean {
		for (u in 0 until n) {
			if (pairU[u] == nil) {
				dist[u] = 0
				queue.addLast(u)
			} else {
				dist[u] = infinity
			}
		}
		dist[nil] = infinity
		while (queue.isNotEmpty()) {
			val u = queue.removeFirst()
			if (dist[u] < dist[nil]) {
				for (v in adj[u]) {
					if (dist[pairV[v]] == infinity) {
						dist[pairV[v]] = dist[u] + 1
						queue.addLast(pairV[v])
					}
				}
			}
		}
		return dist[nil] != infinity
	}

	private fun dfs(u: Int): Boolean {
		if (u != nil) {
			for (v in adj[u]) {
				if (dist[pairV[v]] == dist[u] + 1) {
					if (dfs(pairV[v])) {
						pairV[v] = u
						pairU[u] = v
						return true
					}
				}
			}
			dist[u] = infinity
			return false
		}
		return true
	}
}
