package wf3

fun main() {
	val t = readln().toInt()
	for (case in 1..t) {
		val (b, s) = readln().split(' ').map { it.toInt() }
		val u = IntArray(s)
		val v = IntArray(s)
		for (i in 0 until s) {
			val (ui, vi) = readln().split(' ').map { it.toInt() }
			u[i] = ui
			v[i] = vi
		}
		val route = SlideParade2(b, s, u, v).route()
		if (route == null) {
			println("Case #$case: IMPOSSIBLE")
		} else {
			println("Case #$case: ${route.size}")
			println(route.joinToString(" "))
		}

	}
}

class SlideParade2(b: Int, s: Int, u: IntArray, v: IntArray) {
	private class Building(val num: Int) {
		val forward = mutableListOf<Building>()
		val back = mutableListOf<Building>()
		var distance = -1
		val unvisitedSlides = mutableListOf<Building>()
		var visit = 0
		override fun toString() = "$num: $visit"
	}

	companion object {
		private const val MAX_STEPS = 1000000
	}

	private val buildings = MutableList(b) { Building(it) }
	private val building1 = buildings[0]

	init {
		for (i in 0 until s) {
			val nu = buildings[u[i] - 1]
			val nv = buildings[v[i] - 1]
			nu.forward.add(nv)
			nv.back.add(nu)
			nu.unvisitedSlides.add(nv)
		}
	}

	/** @return whether path to all nodes exists */
	private fun bfs(): Boolean {
		val queue = ArrayDeque<Building>()
		building1.distance = 0
		queue.addLast(building1)
		var nodeCount = 1
		while (queue.isNotEmpty()) {
			val curr = queue.removeFirst()
			for (next in curr.forward) {
				if (next.distance == -1) {
					next.distance = curr.distance + 1
					queue.addLast(next)
					nodeCount++
				}
			}
		}
		return nodeCount == buildings.size
	}

	private fun found(): Boolean {
		val visit = building1.visit
		for (node in buildings) {
			if (node.unvisitedSlides.isNotEmpty()) return false
			if (node.visit != visit) return false
		}
		return true
	}

	private fun move(): List<Int>? {
		val path = mutableListOf<Int>()
		var building = building1
		path.add(building.num + 1)
		while (path.size < MAX_STEPS) {
			do {
				val next = if (building.unvisitedSlides.isNotEmpty()) {
					building.unvisitedSlides.removeLast()
				} else {
					val ways = building.forward.sortedBy { it.visit }
					val firstVisit = ways.first().visit
					ways.filter { it.visit <= firstVisit + 1 }.random()
				}
				building = next
				building.visit++
				path.add(building.num + 1)
				if (path.size > MAX_STEPS) return null
			} while (building.num != building1.num)
			if (found()) return path
		}
		return null
	}

	fun route(): List<Int>? {
		for (node in buildings)
			if (node.forward.isEmpty() || node.back.isEmpty())
				return null
		if (!bfs())
			return null
		return move()
	}
}
