package wf1

fun main() {
	val t = readln().toInt()
	for (case in 1..t) {
		val (j, c, a, q) = readln().split(' ').map { it.toInt() }
		val u = IntArray(c)
		val v = IntArray(c)
		for (i in 0 until c) {
			val (ui, vi) = readln().split(' ').map { it.toInt() }
			u[i] = ui
			v[i] = vi
		}
		val turns = WonderlandChase(j, c, a, q, u, v).turns()
		if (turns == -1)
			println("Case #$case: SAFE")
		else
			println("Case #$case: $turns")
	}
}

class WonderlandChase(j: Int, c: Int, a: Int, q: Int, u: IntArray, v: IntArray) {
	inner class Node {
		val neighbors: MutableList<Node> = mutableListOf()
		var qDistance = -1
		var aDistance = -1
	}

	private val nodes = MutableList(j) { Node() }
	private val aliceNode = nodes[a - 1]
	private val queenNode = nodes[q - 1]
	private var farthestNode = aliceNode

	init {
		for (i in 0 until c) {
			val nu = nodes[u[i] - 1]
			val nv = nodes[v[i] - 1]
			nu.neighbors.add(nv)
			nv.neighbors.add(nu)
		}
	}

	fun turns(): Int {
		if (!bfsAlice()) return -1
		if (bfsQueen()) return -1
		return farthestNode.qDistance * 2
	}

	/** @return whether path exists */
	private fun bfsAlice(): Boolean {
		var pathExists = false
		val queue = ArrayDeque<Node>()
		aliceNode.aDistance = 0
		queue.addLast(aliceNode)
		while (queue.isNotEmpty()) {
			val curr = queue.removeFirst()
			if (curr == queenNode)
				pathExists = true
			for (next in curr.neighbors) {
				if (next.aDistance == -1) {
					next.aDistance = curr.aDistance + 1
					queue.addLast(next)
				}
			}
		}
		return pathExists
	}

	/** @return whether good cycle found */
	private fun bfsQueen(): Boolean {
		val queue = ArrayDeque<Node>()
		queenNode.qDistance = 0
		queue.addLast(queenNode)
		while (queue.isNotEmpty()) {
			val curr = queue.removeFirst()
			if (curr.qDistance > farthestNode.qDistance && curr.aDistance < curr.qDistance)
				farthestNode = curr
			for (next in curr.neighbors) {
				if (next.qDistance == -1) {
					next.qDistance = curr.qDistance + 1
					queue.addLast(next)
				} else if (curr.qDistance <= next.qDistance) {
					if (curr.aDistance < curr.qDistance) return true
					if (next.aDistance < next.qDistance) return true
				}
			}
		}
		return false
	}
}
