package wf3

fun main() {
	val t = readln().toInt()
	for (case in 1..t)
		println("Case #$case: ${slideParade()}")
}

/**
 * Breadth-first search
 * @param adj Adjacency list of graph
 * @return Number of reachable nodes
 */
private fun bfs(adj: List<List<Int>>): Int {
	val queue = ArrayDeque<Int>()
	queue.addLast(0)
	val visited = BooleanArray(adj.size)
	visited[0] = true
	while (queue.isNotEmpty()) {
		val u = queue.removeFirst()
		for (v in adj[u]) {
			if (visited[v]) continue
			visited[v] = true
			queue.addLast(v)
		}
	}
	return visited.count { it }
}

// Bipartite Matching. Hungarian algorithm.
private fun augment(adj: List<List<Int>>, u: Int, ignore: Int, lookup: MutableSet<Int>, match: MutableMap<Int, Int>): Boolean {
	for (v in adj[u]) {
		if (v == ignore || v in lookup) continue
		lookup.add(v)
		val mu = match[v]
		if (mu == null || augment(adj, mu, ignore, lookup, match)) {
			match[v] = u
			return true
		}
	}
	return false
}

private fun findAlternatingMatching(adj: List<List<Int>>, u: Int, v: Int, match: MutableMap<Int, Int>): Boolean {
	val mu = match[v]!!
	if (mu != u) {
		match.entries.removeIf { it.value == u }
		if (!augment(adj, mu, v, mutableSetOf(), match))
			return false
		match[v] = u
	}
	return true
}

// Find Eulerian cycle. Hierholzer algorithm.
private fun hierholzer(adj: List<MutableList<Int>>): List<Int> {
	val result = mutableListOf<Int>()
	val stk = mutableListOf(0)
	while (stk.isNotEmpty()) {
		while (adj[stk.last()].isNotEmpty())
			stk.add(adj[stk.last()].removeLast())
		result.add(stk.removeLast())
	}
	result.reverse()
	return result
}

private fun slideParade(): String {
	val (B, S) = readln().split(' ').map { it.toInt() }
	val adj = List(B) { mutableListOf<Int>() }
	for (i in 0 until S) {
		val (U, V) = readln().split(' ').map { it.toInt() }
		adj[U - 1].add(V - 1)
	}
	if (bfs(adj) != B)
		return "IMPOSSIBLE"
	val match = mutableMapOf<Int, Int>()
	for (u in 0 until B)
		augment(adj, u, -1, mutableSetOf(), match)
	if (match.size != B)
		return "IMPOSSIBLE"
	val adj2 = List(B) { mutableListOf<Int>() }
	for (u in 0 until B) {
		for (v in adj[u]) {
			if (!findAlternatingMatching(adj, u, v, match))
				return "IMPOSSIBLE"
			for (w in 0 until B)
				adj2[match[w]!!].add(w)
		}
	}
	val result = hierholzer(adj2)
	return "${result.size}\n${result.joinToString(" ") { "${it + 1}" }}"
}
