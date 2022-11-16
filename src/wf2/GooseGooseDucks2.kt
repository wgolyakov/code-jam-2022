package wf2

import java.util.*
import kotlin.math.min

private class Location(val t: Int, val x: Int, val y: Int): Comparable<Location> {
	override fun toString() = "($t, $x, $y)"
	override fun compareTo(other: Location): Int {
		if (t != other.t) return t.compareTo(other.t)
		if (x != other.x) return x.compareTo(other.x)
		return y.compareTo(other.y)
	}
}

private class Statement(val location: Location, val a: Int, val b: Int): Comparable<Statement> {
	override fun toString() = "[$location, $a, $b]"
	override fun compareTo(other: Statement): Int {
		val locCmp = location.compareTo(other.location)
		if (locCmp != 0) return locCmp
		if (a != other.a) return a.compareTo(other.a)
		return b.compareTo(other.b)
	}
}

private fun check(a: Location, b: Location): Boolean {
	val dt = (a.t - b.t).toLong()
	val dx = (a.x - b.x).toLong()
	val dy = (a.y - b.y).toLong()
	return dx * dx + dy * dy <= dt * dt
}

private fun addStatement(s: Statement, bst: TreeSet<Statement>, isDuck: BooleanArray) {
	bst.add(s)
	val iterBack = bst.descendingSet().tailSet(s, false).iterator()
	while (iterBack.hasNext()) {
		val prev = iterBack.next()
		if (check(prev.location, s.location)) {
			break
		} else {
			isDuck[prev.a] = true
			iterBack.remove()
		}
	}
	val iterForward = bst.tailSet(s, false).iterator()
	while (iterForward.hasNext()) {
		val next = iterForward.next()
		if (check(next.location, s.location)) {
			break
		} else {
			isDuck[next.a] = true
			iterForward.remove()
		}
	}
}

fun bfs(adj: List<List<Int>>, isDuck: BooleanArray): Int {
	var q = mutableListOf<Int>()
	for (u in isDuck.indices) {
		if (isDuck[u]) {
			q.add(u)
		}
	}
	while (q.isNotEmpty()) {
		val newQ = mutableListOf<Int>()
		for (u in q) {
			for (v in adj[u]) {
				if (isDuck[v]) {
					continue
				}
				isDuck[v] = true
				newQ.add(v)
			}
		}
		q = newQ
	}
	return isDuck.count { it }
}

fun minSizeOfLeafStronglyConnectedComponents(adj: List<List<Int>>): Int {
	var indexCounter = 0
	val index = MutableList(adj.size) { -1 }
	val lowLinks = MutableList(adj.size) { -1 }
	val stack = mutableListOf<Int>()
	val stackSet = BooleanArray(adj.size)
	val isLeafFound = BooleanArray(adj.size)
	var result = adj.size
	for (i in adj.indices) {
		if (index[i] == -1) {
			val stk = mutableListOf(listOf(1, i))
			while (stk.isNotEmpty()) {
				val args = stk.removeLast()
				if (args[0] == 1) {
					val v = args[1]
					index[v] = indexCounter
					lowLinks[v] = indexCounter++
					stackSet[v] = true
					stack.add(v)
					stk.add(listOf(4, v))
					for (w in adj[v]) {
						stk.add(listOf(2, v, w))
					}
				} else if (args[0] == 2) {
					val v = args[1]
					val w = args[2]
					if (index[w] == -1) {
						stk.add(listOf(3, v, w))
						stk.add(listOf(1, w))
					} else if (stackSet[w]) {
						lowLinks[v] = min(lowLinks[v], index[w])
					} else { // visited child but not in curr stack
						isLeafFound[v] = true
					}
				} else if (args[0] == 3) {
					val v = args[1]
					val w = args[2]
					if (isLeafFound[w]) {
						isLeafFound[v] = true
					}
					lowLinks[v] = min(lowLinks[v], lowLinks[w])
				} else if (args[0] == 4) {
					val v = args[1]
					if (lowLinks[v] != index[v]) {
						continue
					}
					var w = -1
					var cnt = 0
					while (w != v) {
						w = stack.removeLast()
						stackSet[w] = false
						cnt++
					}
					if (!isLeafFound[v]) { // only keep leaf SCCs
						isLeafFound[v] = true
						result = min(result, cnt)
					}
				}
			}
		}
	}
	return result
}

private fun gooseGooseDucks(): Int {
	val (N, M, S) = readln().split(' ').map { it.toInt() }
	val meetings = mutableListOf<Location>()
	for (i in 0 until M) {
		val (X, Y, C) = readln().split(' ').map { it.toInt() }
		meetings.add(Location(C, X, Y))
	}
	val isDuck = BooleanArray(N)
	val adj = List<MutableList<Int>>(N) { mutableListOf() }
	val bsts = List<TreeSet<Statement>>(N) { sortedSetOf() }
	for (j in 0 until S) {
		var (A, B, U, V, D) = readln().split(' ').map { it.toInt() }
		A--
		B--
		val s = Statement(Location(D, U, V), A, B)
		val cit = Collections.binarySearch(meetings, s.location)
		if ((cit >= 0 && !check(meetings[cit], s.location)) ||
			(cit < 0 && -(cit + 1) > 0 && !check(meetings[-(cit + 1) - 1], s.location)) ||
			(cit < 0 && -(cit + 1) < meetings.size && !check(meetings[-(cit + 1)], s.location))) {
			adj[B].add(A)
		}
		addStatement(s, bsts[A], isDuck)
		addStatement(s, bsts[B], isDuck)
	}
	if (isDuck.count { it } > 0) {
		return bfs(adj, isDuck)
	}
	return minSizeOfLeafStronglyConnectedComponents(adj)
}

fun main() {
	val t = readln().toInt()
	for (case in 1..t) {
		println("Case #$case: ${gooseGooseDucks()}")
	}
}
