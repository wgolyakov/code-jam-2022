package r34

fun main() {
	val tree1 = listOf(
		listOf(1),
		listOf(0, 2),
		listOf(1, 3),
		listOf(2))
	WinAsSecond5().calculate(tree1)
	println("----------")

	val tree2 = listOf(
		listOf(1),
		listOf(0, 2, 5),
		listOf(1, 3),
		listOf(2, 4),
		listOf(3),
		listOf(1))
	WinAsSecond5().calculate(tree2)
	println("----------")

	val n = 30
	val tree = List(n) { mutableListOf<Int>() }
	for (i in 0 until n) {
		if (i > 0) tree[i].add(i - 1)
		if (i < n - 1) tree[i].add(i + 1)
	}
	WinAsSecond5().calculate(tree)
}

class WinAsSecond5 {
	data class Node(var num: Int, var red: Boolean = false, var neighbors: MutableList<Node> = mutableListOf()) {
		override fun toString() = num.toString()

		override fun equals(other: Any?): Boolean {
			if (this === other) return true
			if (other !is Node) return false
			if (num != other.num) return false
			if (red != other.red) return false
			if (neighbors.map { it.num }.toSet() != other.neighbors.map { it.num }.toSet()) return false
			return true
		}

		override fun hashCode(): Int {
			var result = num
			result = 31 * result + red.hashCode()
			result = 31 * result + neighbors.map { it.num }.toSet().hashCode()
			return result
		}
	}

	data class State(val nodes: MutableList<Node>) {
		fun size() = nodes.size
		fun isWin() = nodes.isEmpty() || nodes.all { it.red }

		override fun toString() = "" + nodes.map { it.neighbors } + ": " +
				nodes.map { it.red }.map { if (it) '#' else '-' }.joinToString("", "(", ")")

		fun clone(): State {
			val newNodes = nodes.map { Node(it.num) }
			for (i in newNodes.indices) {
				newNodes[i].red = nodes[i].red
				newNodes[i].neighbors = nodes[i].neighbors.map { newNodes[it.num] }.toMutableList()
			}
			return State(newNodes.toMutableList())
		}
	}

	private val lost = emptyList<Int>()
	private val dp = mutableMapOf<State, List<Int>>()

	fun calculate(adj: List<List<Int>>) {
		val nodes = MutableList(adj.size) { Node(it) }
		for (i in adj.indices)
			nodes[i].neighbors = adj[i].map { nodes[it] }.toMutableList()
		val state = State(nodes)
		dp(state)
		//println(dp.toList().joinToString("\n"))
		println("${state.size()}: " + dp[state])
	}

	private fun dp(state: State) {
		if (dp.contains(state)) return
		if (state.isWin()) {
			dp[state] = lost
			return
		}
		for (node in state.nodes) {
			if (node.red) continue
			if (checkMove(state, listOf(node.num))) return
			val neighbors = node.neighbors.filter { !it.red }
			val sz = neighbors.size
			if (sz == 0) continue
			val allMasks = 1L shl sz
			for (k in 1L until allMasks) {
				val move = mutableListOf(node.num)
				for (j in 0 until sz)
					if (k and (1L shl j) > 0)
						move.add(neighbors[j].num)
				if (checkMove(state, move)) return
			}
		}
		dp[state] = lost
	}

	private fun checkMove(state: State, move: List<Int>): Boolean {
		val newState = apply(state, move)
		dp(newState)
		if (dp[newState] == lost) {
			dp[state] = move
			return true
		}
		return false
	}

	private fun apply(state: State, move: List<Int>): State {
		val newState = state.clone()
		for (i in move)
			newState.nodes[i].red = true
		return simplify(newState)
	}

	private fun simplify(state: State): State {
		return cutLostParts(state)
	}

	private fun collapseRedNodes(state: State): State {
		for (node in state.nodes.reversed()) {
			if (!node.red) continue
			val neighbors = node.neighbors
			if (neighbors.isEmpty()) return State(mutableListOf())
			if (neighbors.size == 1) {
				neighbors.first().neighbors.remove(node)
				state.nodes.removeAt(node.num)
				for (i in node.num until state.nodes.size)
					state.nodes[i].num -= 1
				continue
			}
			val redNode = neighbors.find { it.red }
			if (redNode != null) {
				for (n in neighbors) {
					n.neighbors.remove(node)
					if (n.num != redNode.num) {
						n.neighbors.add(redNode)
						redNode.neighbors.add(n)
					}
				}
				state.nodes.removeAt(node.num)
				for (i in node.num until state.nodes.size)
					state.nodes[i].num -= 1
			}
		}
		return state
	}

	private fun cutLostParts(state: State): State {
		val groups = mutableListOf<MutableSet<Node>>()
		for (node in state.nodes) {
			if (node.red) continue
			val neighbors = node.neighbors
			val nGroups = mutableListOf<MutableSet<Node>>()
			for (n in neighbors) {
				if (n.red) continue
				val nGroup = groups.find { n in it }
				if (nGroup != null)
					nGroups.add(nGroup)
			}
			when (nGroups.size) {
				0 -> groups.add(mutableSetOf(node))
				1 -> nGroups.first().add(node)
				else -> {
					val first = nGroups.first()
					for (g in 1 until nGroups.size) {
						val gr = nGroups[g]
						first.addAll(gr)
						groups.remove(gr)
					}
					first.add(node)
				}
			}
		}
		val winGroups = mutableListOf<MutableSet<Node>>()
		for (group in groups) {
			val gr = group.sortedBy { it.num }
			val old2New = IntArray(state.size()) { -1 }
			for (new in gr.indices) {
				val old = gr[new].num
				old2New[old] = new
			}
			val nodes = gr.map { Node(old2New[it.num]) }
			for (i in nodes.indices) {
				nodes[i].neighbors = gr[i].neighbors.map { old2New[it.num] }.filter { it != -1 }.map { nodes[it] }.toMutableList()
			}
			val st = State(nodes.toMutableList())
			dp(st)
			if (dp[st] != lost)
				winGroups.add(group)
		}
		if (winGroups.isEmpty()) return State(mutableListOf())
		if (winGroups.size == groups.size)
			return collapseRedNodes(state)
		val nodes = mutableListOf<Node>()
		if (winGroups.size == 1) {
			nodes.addAll(winGroups.first())
		} else {
			val redNode = Node(state.size())
			redNode.red = true
			for (group in winGroups) {
				nodes.addAll(group)
				val node = group.first()
				node.neighbors.add(redNode)
				redNode.neighbors.add(node)
			}
			nodes.add(redNode)
		}
		val old2New = IntArray(state.size() + 1) { -1 }
		for (new in nodes.indices) {
			val old = nodes[new].num
			old2New[old] = new
		}
		for (node in nodes)
			node.neighbors = node.neighbors.filter { old2New[it.num] != -1 }.toMutableList()
		for (node in nodes)
			node.num = old2New[node.num]
		return State(nodes)
	}
}
