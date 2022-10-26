package r34

import kotlin.system.exitProcess

fun main() {
	val was = WinAsSecond()
	val t = readln().toInt()
	for (case in 1..t) {
		// Ueli
		val n = readln().toInt()
		// Vreni
		val lostTree = was.createLostTree(n)
		for (edge in was.edges(lostTree))
			println("${edge.first + 1} ${edge.second + 1}")
		// Number of games
		val m = readln().toInt()
		for (game in 1..m) {
			// New game. Clear board.
			var tree = listOf(lostTree)
			val renTo = IntArray(n) { it }
			val renBack = IntArray(n) { it }
			do {
				// Ueli's turn
				val k = readln().toInt()
				if (k == -1)
					exitProcess(0)
				val a = readln().split(' ').map { it.toInt() }
				val move1 = a.map { renTo[it - 1] }
				tree = was.applyM(tree, move1, renTo, renBack)
				// Vreni's turn
				val move2 = was.dpm(tree)
				println(move2.size)
				println(move2.joinToString(" ") { "${renBack[it] + 1}" })
				tree = was.applyM(tree, move2, renTo, renBack)
			} while (!was.isWin(tree))
		}
	}
}

class WinAsSecond {
	private val dp = mutableMapOf<List<List<Int>>, List<Int>>()
	private val dpm = mutableMapOf<List<List<List<Int>>>, List<Int>>()
	private val lost = emptyList<Int>()

	private val lostTreeLineDiff = mapOf(
		30 to intArrayOf(27),
		31 to intArrayOf(4, 26),
		32 to intArrayOf(26, 28),
		33 to intArrayOf(19, 25, 28),
		34 to intArrayOf(17),
		35 to intArrayOf(31),
		36 to intArrayOf(33),
		37 to intArrayOf(32, 32),
		38 to intArrayOf(33, 36),
		39 to intArrayOf(31, 32, 37),
		40 to intArrayOf(37)
	)

	fun createLostTree(n: Int): List<List<Int>> {
		val diff = lostTreeLineDiff[n]!!
		val sz = n - diff.size
		val tree = MutableList(sz) { mutableListOf<Int>() }
		for (i in 0 until sz) {
			if (i > 0) tree[i].add(i - 1)
			if (i < sz - 1) tree[i].add(i + 1)
		}
		for (i in diff) {
			tree.add(mutableListOf(i))
			tree[i].add(tree.size - 1)
		}
		return tree
	}

	fun edges(tree: List<List<Int>>): List<Pair<Int, Int>> {
		val edges = mutableListOf<Pair<Int, Int>>()
		for (i in tree.indices)
			for (j in tree[i])
				if (i < j) edges.add(i to j)
		return edges
	}

	fun isWin(tree: List<List<List<Int>>>) = tree.isEmpty() || tree.all { it.isEmpty() }

	private fun dp(tree: List<List<Int>>): List<Int> {
		var move = dp[tree]
		if (move != null) return move
		if (tree.isEmpty()) {
			dp[tree] = lost
			return lost
		}
		for (i in tree.indices) {
			move = listOf(i)
			if (checkMove(tree, move)) return move
			val neighbors = tree[i]
			val sz = neighbors.size
			if (sz == 0) continue
			val allMasks = 1L shl sz
			for (k in 1L until allMasks) {
				move = mutableListOf(i)
				var jMask = 1L
				for (j in 0 until sz) {
					if (k and jMask > 0)
						move.add(neighbors[j])
					jMask = jMask shl 1
				}
				if (checkMove(tree, move)) return move
			}
		}
		dp[tree] = lost
		return lost
	}

	fun dpm(mTree: List<List<List<Int>>>): List<Int> {
		if (mTree.size == 1) return dp(mTree.first())
		var move = dpm[mTree]
		if (move != null) return move
		if (mTree.isEmpty()) {
			dpm[mTree] = lost
			return lost
		}
		var m = 0
		for (tree in mTree) {
			for (i in tree.indices) {
				move = listOf(m + i)
				if (checkMMove(mTree, move)) return move
				val neighbors = tree[i]
				val sz = neighbors.size
				if (sz == 0) continue
				val allMasks = 1L shl sz
				for (k in 1L until allMasks) {
					move = mutableListOf(m + i)
					var jMask = 1L
					for (j in 0 until sz) {
						if (k and jMask > 0)
							move.add(m + neighbors[j])
						jMask = jMask shl 1
					}
					if (checkMMove(mTree, move)) return move
				}
			}
			m += tree.size
		}
		dpm[mTree] = lost
		return lost
	}

	private fun apply(tree: List<List<Int>>, move: List<Int>, renTo: IntArray? = null): List<List<List<Int>>> {
		val groups = mutableListOf<MutableSet<Int>>()
		for (i in tree.indices) {
			if (i in move) continue
			val neighbors = tree[i]
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
		val mTree = mutableListOf<List<List<Int>>>()
		var grPrefix = 0
		for (group in groups) {
			val newTree = mutableListOf<List<Int>>()
			val gr = group.sorted()
			val old2New = IntArray(tree.size)
			for ((new, old) in gr.withIndex()) {
				old2New[old] = new
			}
			if (renTo != null) {
				for ((new, old) in gr.withIndex())
					renTo[old] = grPrefix + new
			}
			for (new in gr.indices) {
				val old = gr[new]
				val neighbors = tree[old]
				val newNeighbors = mutableListOf<Int>()
				for (n in neighbors) {
					if (n in group)
						newNeighbors.add(old2New[n])
				}
				newTree.add(newNeighbors)
			}
			mTree.add(newTree)
			grPrefix += newTree.size
		}
		return mTree
	}

	fun applyM(mTree: List<List<List<Int>>>, move: List<Int>,
			   renTo: IntArray? = null, renBack: IntArray? = null): List<List<List<Int>>> {
		val i = move.first()
		var j = 0
		var tNum = 0
		for ((treeNum, tree) in mTree.withIndex()) {
			if (i in j until (j + tree.size)) {
				tNum = treeNum
				break
			}
			j += tree.size
		}
		val tree = mTree[tNum]
		val tMove = move.map { it - j }
		val subRenTo = if (renTo == null || renBack == null) null else IntArray(tree.size) { -1 }
		val treeList = apply(tree, tMove, subRenTo)
		val res = mTree.toMutableList()
		res.removeAt(tNum)
		res.addAll(tNum, treeList)
		ren(renTo, renBack, subRenTo, mTree, j, tNum, move)
		return res
	}

	private fun ren(renTo: IntArray?, renBack: IntArray?, subRenTo: IntArray?,
					mTree: List<List<List<Int>>>, prefix: Int, tNum: Int, move: List<Int>) {
		if (renTo == null || renBack == null || subRenTo == null) return
		val oldGlobRenBack = renBack.copyOf()
		for ((old, new) in subRenTo.withIndex()) {
			if (new == -1) continue
			renTo[oldGlobRenBack[prefix + old]] = prefix + new
			renBack[prefix + new] = oldGlobRenBack[prefix + old]
		}
		var j = prefix + mTree[tNum].size
		for (i in (tNum + 1) until mTree.size) {
			val tree = mTree[i]
			for (k in tree.indices) {
				val old = j + k
				val new = old - move.size
				renTo[oldGlobRenBack[old]] = new
				renBack[new] = oldGlobRenBack[old]
			}
			j += tree.size
		}
	}

	private fun checkMove(tree: List<List<Int>>, move: List<Int>): Boolean {
		var mTree = apply(tree, move)
		mTree = mTree.filter { dp(it) != lost }
		if (mTree.isEmpty()) {
			dp[tree] = move
			return true
		}
		mTree = simplify(mTree)
		if (mTree.size == 1)
			return false
		if (dpm(mTree) == lost) {
			dp[tree] = move
			return true
		}
		return false
	}

	private fun checkMMove(mTree1: List<List<List<Int>>>, move: List<Int>): Boolean {
		var mTree = applyM(mTree1, move)
		mTree = mTree.filter { dp(it) != lost }
		if (mTree.isEmpty()) {
			dpm[mTree1] = move
			return true
		}
		mTree = simplify(mTree)
		if (mTree.size == 1)
			return false
		if (dpm(mTree) == lost) {
			dpm[mTree1] = move
			return true
		}
		return false
	}

	private fun simplify(mTree: List<List<List<Int>>>): List<List<List<Int>>> {
		if (mTree.size <= 2) return mTree
		val currMTree = mTree.toMutableList()
		do {
			val subMTree = findLostSubTree(currMTree)
			if (subMTree != null) {
				for (tree in subMTree)
					currMTree.remove(tree)
			}
		} while (subMTree != null)
		return currMTree
	}

	private fun findLostSubTree(mTree: List<List<List<Int>>>): List<List<List<Int>>>? {
		val sz = mTree.size
		if (sz <= 2) return null
		val allMasks = 1 shl sz
		for (k in 3..(allMasks - 2)) {
			if (k.countOneBits() < 2) continue
			val subList = mutableListOf<List<List<Int>>>()
			var jMask = 1
			for (j in 0 until sz) {
				if (k and jMask > 0)
					subList.add(mTree[j])
				jMask = jMask shl 1
			}
			if (dpm(subList) == lost)
				return subList
		}
		return null
	}
}
