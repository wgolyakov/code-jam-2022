package r34

import kotlin.random.Random

fun main() {
	val tree1 = listOf(
		listOf(1),
		listOf(0, 2),
		listOf(1))
	WinAsSecond4().calculate(tree1)
	println("----------")

	val tree2 = listOf(
		listOf(1),
		listOf(0, 2, 5),
		listOf(1, 3),
		listOf(2, 4),
		listOf(3),
		listOf(1))
	WinAsSecond4().calculate(tree2)
	println("----------")

	val w = WinAsSecond4()
	for (n in 1..40) {
		val tree = List(n) { mutableListOf<Int>() }
		for (i in 0 until n) {
			if (i > 0) tree[i].add(i - 1)
			if (i < n - 1) tree[i].add(i + 1)
		}
		w.calculate(tree)
	}
	println("----------")

	val time = System.currentTimeMillis()
	val was = WinAsSecond4()
	val tree = was.findLostTree4(30)
	println("${(System.currentTimeMillis() - time) / 1000} sec")
	was.calculate(tree)
	println(tree)
}

class WinAsSecond4 {
	private val lost = 0L
	private val dp = mutableMapOf<List<Long>, Long>()
	private val dpm = mutableMapOf<List<List<Long>>, Long>()

	private fun createRandomTree(n: Int): List<List<Int>> {
		val tree = mutableListOf<MutableList<Int>>(mutableListOf())
		for (i in 1 until n) {
			val j = tree.indices.random()
			tree.add(mutableListOf(j))
			tree[j].add(i)
		}
		return tree
	}

	fun findLostTree(n: Int): List<List<Int>> {
		var tree: List<List<Int>>
		do {
			tree = createRandomTree(n)
		} while (dp(tree) != lost)
		return tree
	}

	private fun addRandomNode(tree: List<List<Int>>): List<List<Int>> {
		if (tree.isEmpty())
			return listOf(listOf())
		val treeCopy = tree.map { it.toMutableList() }.toMutableList()
		val j = tree.indices.random()
		treeCopy.add(mutableListOf(j))
		treeCopy[j].add(tree.size)
		return treeCopy
	}

	fun findLostTree2(n: Int): List<List<Int>> {
		var lostTree = listOf<List<Int>>()
		var winTree: List<List<Int>>
		for (i in 0 until n step 2) {
			do {
				winTree = addRandomNode(lostTree)
			} while (dp(winTree) == lost)
			println(winTree.size)
			do {
				lostTree = addRandomNode(winTree)
			} while (dp(lostTree) != lost)
			println(lostTree.size)
		}
		return lostTree
	}

	private fun addRandomNodes(tree: List<List<Int>>): List<List<Int>> {
		var t = tree
		for (i in 0 until Random.nextInt(1, 7))
			t = addRandomNode(t)
		return t
	}

	private fun addRandomNodes2(tree: List<List<Int>>): List<List<Int>> {
		val treeCopy = tree.map { it.toMutableList() }.toMutableList()
		if (treeCopy.isEmpty())
			treeCopy.add(mutableListOf())
		for (i in 0 until Random.nextInt(1, 3)) {
			val j = treeCopy.indices.random()
			treeCopy.add(mutableListOf(j))
			treeCopy[j].add(treeCopy.size - 1)
			val p = treeCopy.size - 1
			for (k in 0 until Random.nextInt(0, 3)) {
				treeCopy.add(mutableListOf(p))
				treeCopy[p].add(treeCopy.size - 1)
			}
			val s = treeCopy.size - 1
			for (k in 0 until Random.nextInt(0, 3)) {
				treeCopy.add(mutableListOf(s))
				treeCopy[s].add(treeCopy.size - 1)
			}
		}
		return treeCopy
	}

	fun findLostTree3(n: Int): List<List<Int>> {
		var lostTree = listOf<List<Int>>()
		var tree: List<List<Int>>
		for (i in 0 until n) {
			do {
				tree = addRandomNodes2(lostTree)
			} while (dp(tree) != lost)
			lostTree = tree
			println(lostTree.size)
		}
		return lostTree
	}

	// 30 - 6 sec
	// 31 - no
	// 32 - 20 sec
	// 40 - 26 sec
	fun findLostTree4(n: Int): List<List<Int>> {
		val tree = List(n) { mutableListOf<Int>() }
		for (i in 1..(n - 2)) {
			tree[i].add(i - 1)
			tree[i].add(i + 1)
		}
		if (n > 1) {
			tree[0].add(1)
			tree[n - 1].add(n - 2)
		}
		while (dp(tree) != lost) {
			val i = tree.indices.random()
			if (tree[i].size == 1) {
				val j = tree.indices.random()
				if (i != j) {
					val k = tree[i].first()
					tree[k].remove(i)
					tree[i].remove(k)
					tree[j].add(i)
					tree[i].add(j)
				}
			}
		}
		return tree
	}

	private var lostTreeSize = 30
	private lateinit var lostTree: MutableList<MutableList<Int>>

	// 30 - 2 sec
	// 31 - no
	// 40 - 18 sec
	fun findLostTree5(n: Int): List<List<Int>> {
		lostTreeSize = n
		lostTree = MutableList(1) { mutableListOf() }
		findLostTreeRecurs2()
		return lostTree
	}

	private fun findLostTreeRecurs(): Boolean {
		if (lostTree.size == lostTreeSize)
			return dp(lostTree) == lost
		for (i in lostTree.indices.reversed()) {
			lostTree.add(mutableListOf(i))
			lostTree[i].add(lostTree.size - 1)
			if (findLostTreeRecurs()) return true
			lostTree[i].remove(lostTree.size - 1)
			lostTree.removeLast()
		}
		return false
	}

	private fun findLostTreeRecurs2(): Boolean {
		if (lostTree.size == lostTreeSize)
			return dp(lostTree) == lost
		//for (i in lostTree.indices) {
		for (i in lostTree.indices.reversed()) {
			val treeCopy = MutableList(lostTree.size) { lostTree[it].toMutableList() }
			lostTree.add(mutableListOf(i))
			lostTree[i].add(lostTree.size - 1)
			if (findLostTreeRecurs()) return true
			lostTree = treeCopy
		}
		return false
	}

	fun calculate(tree: List<List<Int>>) {
		val move = dp(tree)
		//printDp()
		println("${tree.size}: " + unzipMove(move))
	}

	private fun printDp() {
		println(dp.toList().joinToString("\n") { "${unzipState(it.first)} = ${unzipMove(it.second)}" })
		println(dpm.toList().joinToString("\n") { "${unzipMState(it.first)} = ${unzipMove(it.second)}" })
	}

	private fun zipState(adj: List<List<Int>>): List<Long> {
		val state = mutableListOf<Long>()
		for (neighbors in adj) {
			var nb = 0L
			for (i in neighbors)
				nb = nb or (1L shl i)
			state.add(nb)
		}
		return state
	}

	private fun unzipState(state: List<Long>): List<List<Int>> {
		val adj = mutableListOf<List<Int>>()
		for (neighbors in state) {
			val nb = mutableListOf<Int>()
			for (i in state.indices)
				if (neighbors and (1L shl i) > 0)
					nb.add(i)
			adj.add(nb)
		}
		return adj
	}

	private fun zipMState(mAdj: List<List<List<Int>>>): List<List<Long>> {
		return mAdj.map { zipState(it) }
	}

	private fun unzipMState(mState: List<List<Long>>): List<List<List<Int>>> {
		return mState.map { unzipState(it) }
	}

	private fun zipMove(move: Set<Int>): Long {
		var mv = 0L
		for (i in move)
			mv = mv or (1L shl i)
		return mv
	}

	private fun unzipMove(move: Long, n: Int = 40): Set<Int> {
		val mv = mutableSetOf<Int>()
		for (i in 0 until n)
			if (move and (1L shl i) > 0)
				mv.add(i)
		return mv
	}

	private fun dp(adj: List<List<Int>>): Long {
		val state = zipState(adj)
		var zMove = dp[state]
		if (zMove != null) return zMove
		if (state.isEmpty()) {
			dp[state] = lost
			return lost
		}
		for (i in adj.indices) {
			zMove = checkMove(adj, setOf(i))
			if (zMove != null) return zMove
			val neighbors = adj[i]
			val sz = neighbors.size
			if (sz == 0) continue
			val allMasks = 1L shl sz
			for (k in 1L until allMasks) {
				val move = mutableSetOf(i)
				var jMask = 1L
				for (j in 0 until sz) {
					if (k and jMask > 0)
						move.add(neighbors[j])
					jMask = jMask shl 1
				}
				zMove = checkMove(adj, move)
				if (zMove != null) return zMove
			}
		}
		dp[state] = lost
		return lost
	}

	private fun dpm(mAdj: List<List<List<Int>>>): Long {
		val mState = zipMState(mAdj)
		var zMove = dpm[mState]
		if (zMove != null) return zMove
		if (mState.isEmpty()) {
			dpm[mState] = lost
			return lost
		}
		var m = 0
		for (adj in mAdj) {
			for (i in adj.indices) {
				zMove = checkMMove(mAdj, setOf(m + i))
				if (zMove != null) return zMove
				val neighbors = adj[i]
				val sz = neighbors.size
				if (sz == 0) continue
				val allMasks = 1L shl sz
				for (k in 1L until allMasks) {
					val move = mutableSetOf(m + i)
					var jMask = 1L
					for (j in 0 until sz) {
						if (k and jMask > 0)
							move.add(m + neighbors[j])
						jMask = jMask shl 1
					}
					zMove = checkMMove(mAdj, move)
					if (zMove != null) return zMove
				}
			}
			m += adj.size
		}
		dpm[mState] = lost
		return lost
	}

	private fun apply(adj: List<List<Int>>, move: Set<Int>): List<List<List<Int>>> {
		val groups = mutableListOf<MutableSet<Int>>()
		for (i in adj.indices) {
			if (i in move) continue
			val neighbors = adj[i]
			val nGroups = mutableListOf<MutableSet<Int>>()
			for (j in neighbors) {
				if (j in move) continue
				val jGroup = groups.find { j in it }
				if (jGroup != null)
					nGroups.add(jGroup)
			}
			when (nGroups.size) {
				0 -> groups.add(mutableSetOf(i))
				1 -> nGroups.first().add(i)
				else -> {
					val first = nGroups.first()
					for (g in 1 until nGroups.size) {
						val gr = nGroups[g]
						first.addAll(gr)
						groups.remove(gr)
					}
					first.add(i)
				}
			}
		}
		val newAdjList = mutableListOf<List<List<Int>>>()
		for (group in groups) {
			val newAdj = mutableListOf<List<Int>>()
			val gr = group.sorted()
			val old2New = IntArray(adj.size)
			for (new in gr.indices) {
				val old = gr[new]
				old2New[old] = new
			}
			for (new in gr.indices) {
				val old = gr[new]
				val neighbors = adj[old]
				val newNeighbors = mutableListOf<Int>()
				for (n in neighbors) {
					if (n in group)
						newNeighbors.add(old2New[n])
				}
				newAdj.add(newNeighbors)
			}
			newAdjList.add(newAdj)
		}
		return newAdjList
	}

	private fun applyM(mAdj: List<List<List<Int>>>, move: Set<Int>): List<List<List<Int>>> {
		val i = move.first()
		var j = 0
		var tAdj: List<List<Int>>? = null
		for (adj in mAdj) {
			if (i in j until (j + adj.size)) {
				tAdj = adj
				break
			}
			j += adj.size
		}
		val tMove = move.map { it - j }.toSet()
		val adjList = apply(tAdj!!, tMove)
		val res = mAdj.toMutableList()
		res.remove(tAdj)
		res.addAll(adjList)
		return res
	}

	private fun checkMove(adj: List<List<Int>>, move: Set<Int>): Long? {
		var mAdj = apply(adj, move)
		mAdj = mAdj.filter { dp(it) != lost }
		if (mAdj.isEmpty()) {
			val zMove = zipMove(move)
			dp[zipState(adj)] = zMove
			return zMove
		}
		mAdj = simplify(mAdj)
		if (mAdj.size == 1)
			return null
		if (dpm(mAdj) == lost) {
			val zMove = zipMove(move)
			dp[zipState(adj)] = zMove
			return zMove
		}
		return null
	}

	private fun checkMMove(mAdj0: List<List<List<Int>>>, move: Set<Int>): Long? {
		var mAdj = applyM(mAdj0, move)
		mAdj = mAdj.filter { dp(it) != lost }
		if (mAdj.isEmpty()) {
			val zMove = zipMove(move)
			dpm[zipMState(mAdj0)] = zMove
			return zMove
		}
		mAdj = simplify(mAdj)
		if (mAdj.size == 1)
			return null
		if (dpm(mAdj) == lost) {
			val zMove = zipMove(move)
			dpm[zipMState(mAdj0)] = zMove
			return zMove
		}
		return null
	}

	private fun simplify(mAdj: List<List<List<Int>>>): List<List<List<Int>>> {
		if (mAdj.size <= 2) return mAdj
		val currMAdj = mAdj.toMutableList()
		do {
			val subMAdj = findLostSubAdj(currMAdj)
			if (subMAdj != null) {
				for (adj in subMAdj)
					currMAdj.remove(adj)
			}
		} while (subMAdj != null)
		return currMAdj
	}

	private fun findLostSubAdj(mAdj: List<List<List<Int>>>): List<List<List<Int>>>? {
		val sz = mAdj.size
		if (sz <= 2) return null
		val allMasks = 1 shl sz
		for (k in 3..(allMasks - 2)) {
			if (k.countOneBits() < 2) continue
			val subList = mutableListOf<List<List<Int>>>()
			var jMask = 1
			for (j in 0 until sz) {
				if (k and jMask > 0)
					subList.add(mAdj[j])
				jMask = jMask shl 1
			}
			if (dpm(subList) == lost)
				return subList
		}
		return null
	}
}
