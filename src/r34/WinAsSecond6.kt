package r34

import kotlin.random.Random

fun main() {
	val t1 = listOf(
		listOf(1),
		listOf(0, 2),
		listOf(1))
	WinAsSecond6().calculate(t1)
	println("----------")

	val t2 = listOf(
		listOf(1),
		listOf(0, 2, 5),
		listOf(1, 3),
		listOf(2, 4),
		listOf(3),
		listOf(1))
	WinAsSecond6().calculate(t2)
	println("----------")

	val w = WinAsSecond6()
	for (n in 1..40) {
		val tree = List(n) { mutableListOf<Int>() }
		for (i in 0 until n) {
			if (i > 0) tree[i].add(i - 1)
			if (i < n - 1) tree[i].add(i + 1)
		}
		w.calculate(tree)
	}
	println("----------")

	val w1 = WinAsSecond6()
	val tree1 = w1.findLostTree(20)
	w1.calculate(tree1)
	println(tree1)
	println("----------")

	val w3 = WinAsSecond6()
	val tree3 = w3.findLostTree3(10)
	w3.calculate(tree3)
	println(tree3)
	println("----------")

	val w4 = WinAsSecond6()
	val tree4 = w4.findLostTree4(20)
	w4.calculate(tree4)
	println(tree4)
	println("----------")

	val time5 = System.currentTimeMillis()
	val w5 = WinAsSecond6()
	val tree5 = w5.findLostTree5(40)
	println("${(System.currentTimeMillis() - time5) / 1000} sec")
	w5.calculate(tree5)
	println(tree5)
	println("----------")

	val time6 = System.currentTimeMillis()
	val w6 = WinAsSecond6()
	val tree6 = w6.findLostTree6(39)
	println("${(System.currentTimeMillis() - time6) / 1000} sec")
	w6.calculate(tree6)
	println(tree6)
}

class WinAsSecond6 {
	private val lost = emptySet<Int>()
	private val dp = mutableMapOf<List<List<Int>>, Set<Int>>()
	private val dpm = mutableMapOf<List<List<List<Int>>>, Set<Int>>()

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

	// Not work
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
			if (lostTree.size >= n) break
		}
		return lostTree
	}

	// 30 - 6 sec
	// 31 - no
	// 32 - 20 sec
	// 40 - 26 sec
	fun findLostTree4(n: Int): List<List<Int>> {
		val tree = List(n) { mutableListOf<Int>() }
		for (i in 0 until n) {
			if (i > 0) tree[i].add(i - 1)
			if (i < n - 1) tree[i].add(i + 1)
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

	// 30 - 0 sec
	// 31 - 40 sec
	// 32 - 12 sec
	// 33 - 429 sec
	// 40 - 6 sec
	//
	// Difference from line:
	// 30: 1 (27)
	// 31: 2 (26, 4); (0, 1, 23)
	// 32: 2 (28, 26)
	// 33: 3 (19, 25, 28); (1, 4, 10)
	// 34: 1 (17)
	// 35: 1 (31)
	// 36: 1 (33)
	// 37: 2 (32, 32); (2, 2)
	// 38: 2 (33, 36)
	// 39: 3 (31, 32, 37)
	// 40: 1 (37)
	fun findLostTree5(n: Int): List<List<Int>> {
		lostTreeSize = n
		lostTree = MutableList(1) { mutableListOf() }
		findLostTreeRecurs5()
		return lostTree
	}

	private fun findLostTreeRecurs5(): Boolean {
		if (lostTree.size == lostTreeSize)
			return dp(lostTree) == lost
		for (i in lostTree.indices.reversed()) {
			val treeCopy = lostTree
			lostTree = MutableList(lostTree.size) { lostTree[it].toMutableList() }
			lostTree.add(mutableListOf(i))
			lostTree[i].add(lostTree.size - 1)
			if (findLostTreeRecurs5()) return true
			lostTree = treeCopy
		}
		return false
	}

	fun findLostTree6(n: Int): List<List<Int>> {
		val sz = n - 3
		val tree = MutableList(sz) { mutableListOf<Int>() }
		for (i in 0 until sz) {
			if (i > 0) tree[i].add(i - 1)
			if (i < sz - 1) tree[i].add(i + 1)
		}
		lostTreeSize = n
		lostTree = tree
		findLostTreeRecurs6()
		return lostTree
	}

	private fun findLostTreeRecurs6(): Boolean {
		if (lostTree.size == lostTreeSize)
			return dp(lostTree) == lost
		val range = when (lostTree.size) {
			lostTreeSize - 3 -> 31 downTo 30
			lostTreeSize - 2 -> 34 downTo 32
			else -> 37 downTo 35
		}
		for (i in range) {
			println(i)
			val treeCopy = lostTree
			lostTree = MutableList(lostTree.size) { lostTree[it].toMutableList() }
			lostTree.add(mutableListOf(i))
			lostTree[i].add(lostTree.size - 1)
			if (findLostTreeRecurs6()) return true
			lostTree = treeCopy
		}
		return false
	}

	fun calculate(tree: List<List<Int>>) {
		val move = dp(tree)
		//printDp()
		println("${tree.size}: " + move)
	}

	private fun printDp() {
		println(dp.toList().joinToString("\n") { "${it.first} = ${it.second}" })
		println(dpm.toList().joinToString("\n") { "${it.first} = ${it.second}" })
	}

	private fun dp(adj: List<List<Int>>): Set<Int> {
		var move = dp[adj]
		if (move != null) return move
		if (adj.isEmpty()) {
			dp[adj] = lost
			return lost
		}
		for (i in adj.indices) {
			move = setOf(i)
			if (checkMove(adj, move)) return move
			val neighbors = adj[i]
			val sz = neighbors.size
			if (sz == 0) continue
			val allMasks = 1L shl sz
			for (k in 1L until allMasks) {
				move = mutableSetOf(i)
				var jMask = 1L
				for (j in 0 until sz) {
					if (k and jMask > 0)
						move.add(neighbors[j])
					jMask = jMask shl 1
				}
				if (checkMove(adj, move)) return move
			}
		}
		dp[adj] = lost
		return lost
	}

	private fun dpm(mAdj: List<List<List<Int>>>): Set<Int> {
		var move = dpm[mAdj]
		if (move != null) return move
		if (mAdj.isEmpty()) {
			dpm[mAdj] = lost
			return lost
		}
		var m = 0
		for (adj in mAdj) {
			for (i in adj.indices) {
				move = setOf(m + i)
				if (checkMMove(mAdj, move)) return move
				val neighbors = adj[i]
				val sz = neighbors.size
				if (sz == 0) continue
				val allMasks = 1L shl sz
				for (k in 1L until allMasks) {
					move = mutableSetOf(m + i)
					var jMask = 1L
					for (j in 0 until sz) {
						if (k and jMask > 0)
							move.add(m + neighbors[j])
						jMask = jMask shl 1
					}
					if (checkMMove(mAdj, move)) return move
				}
			}
			m += adj.size
		}
		dpm[mAdj] = lost
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

	private fun checkMove(adj: List<List<Int>>, move: Set<Int>): Boolean {
		var mAdj = apply(adj, move)
		mAdj = mAdj.filter { dp(it) != lost }
		if (mAdj.isEmpty()) {
			dp[adj] = move
			return true
		}
		mAdj = simplify(mAdj)
		if (mAdj.size == 1)
			return false
		if (dpm(mAdj) == lost) {
			dp[adj] = move
			return true
		}
		return false
	}

	private fun checkMMove(mAdj0: List<List<List<Int>>>, move: Set<Int>): Boolean {
		var mAdj = applyM(mAdj0, move)
		mAdj = mAdj.filter { dp(it) != lost }
		if (mAdj.isEmpty()) {
			dpm[mAdj0] = move
			return true
		}
		mAdj = simplify(mAdj)
		if (mAdj.size == 1)
			return false
		if (dpm(mAdj) == lost) {
			dpm[mAdj0] = move
			return true
		}
		return false
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
