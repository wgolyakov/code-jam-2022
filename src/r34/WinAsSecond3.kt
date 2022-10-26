package r34

import kotlin.math.log2
import kotlin.math.max
import kotlin.math.pow

fun main() {
	Line().calculate(20)
	// Second win: no
	println("----------")
	Line1().calculate(20)
	// Second win: 6, 10, 16, 22
	println("----------")
	Line2().calculate(20)
	// Second win: 16, 22
	println("----------")
	Tree2().calculate(20)
	// Second win: 6
	println("----------")
	TreeN(3).calculate(20)
	// 1: Second win:
	// 2: Second win: 6,
	// 3: Second win:
	// 4: Second win: 18, 20,
	println("----------")
	TreeNL3(2).calculate(20)
	// 1: Second win: no
	// 2: Second win: 6, 12, 18, 24, 30, 36
	// 3: Second win: 8, 16, 24, 32, 40
	// 4: Second win: 8, 10, 18, 20, 28, 30, 38, 40
	// 5: Second win: 10, 12, 22, 24, 34, 36
	// 6: Second win: 10, 12, 14, 24, 26, 28, 38, 40
	println("----------")
	StarN(3).calculate(20)
	// 1: Second win:
	// 2: Second win:
	// 3: Second win: 10,
	// 4: Second win:
	// 5: Second win: 16, 18,
	//println("----------")
	//for (w in 1..20) {
	//	print("$w: Second win: ")
	//	StarN(w).calculate(21)
	//	println("")
	//}
}

//        1
//     /  |  \  ... N
//    2   3   4
//    |   |   |
//    5   6   7
//       ...
class StarN(private val w: Int): WinAsSecond3() {
	override fun neighbors(state: Int, i: Int): List<Int> {
		val list = mutableListOf<Int>()
		if (i == 1) {
			for (n1 in 2..(w + 1))
				if (n1 <= n && !isRed(state, n1))
					list.add(n1)
		} else {
			val nu = max(i - w, 1)
			if (!isRed(state, nu)) list.add(nu)
			val nd = i + w
			if (nd <= n && !isRed(state, nd)) list.add(nd)
		}
		return list
	}
}

//          1
//       /     \       \
//      2       6  ... 30
//    /|||\   /|||\
//   3 4 5... 7 8 9...
class TreeNL3(private val w: Int): WinAsSecond3() {
	override fun neighbors(state: Int, i: Int): List<Int> {
		val list = mutableListOf<Int>()
		if (i == 1) {
			for (n1 in 2..n step w + 1)
				if (!isRed(state, n1))
					list.add(n1)
		} else {
			val t = (i - 2) / (w + 1)
			val j = i - 2 - t * (w + 1)
			if (j == 0) {
				val n1 = 1
				if (n1 <= n && !isRed(state, n1)) list.add(n1)
				for (e in 1..w) {
					val n2 = i + e
					if (n2 <= n && !isRed(state, n2)) list.add(n)
				}
			} else {
				val n1 = i - j
				if (n1 <= n && !isRed(state, n1)) list.add(n)
			}
		}
		return list
	}
}

//           1
//     /     |     \  ... N
//    2      3      4
//  / | \  / | \  / | \
//  5 6 7 8 9 10 11 12 13
//         ...
class TreeN(private val w: Int): WinAsSecond3() {
	private fun powN(x: Int) = w.toDouble().pow(x).toInt()
	private fun level(i: Int): Pair<Int, Int> {
		var j = 1
		var level = 0
		var delta = powN(level)
		while (j + delta <= i) {
			j += delta
			level++
			delta = powN(level)
		}
		return Pair(level, j)
	}

	override fun neighbors(state: Int, i: Int): List<Int> {
		val list = mutableListOf<Int>()
		val (level, b) = level(i)
		val j = i - b
		if (i > 1) {
			val np = b - powN(level - 1) + (j / w)
			if (!isRed(state, np)) list.add(np)
		}
		val n0 = b + powN(level) + (j * w)
		for (t in 0 until w) {
			val nt = n0 + t
			if (nt > n) break
			if (!isRed(state, nt)) list.add(nt)
		}
		return list
	}
}

//        1
//      /  \
//     2    3
//    / \  / \
//   4  5  6  7
//  /\ /\ /\ /\
//      ...
class Tree2: WinAsSecond3() {
	private fun pow2(x: Int) = 1 shl x

	override fun neighbors(state: Int, i: Int): List<Int> {
		val list = mutableListOf<Int>()
		val level = log2(i.toDouble()).toInt()
		val j = i - pow2(level)
		if (i > 1) {
			val n1 = pow2(level - 1) + (j / 2)
			if (!isRed(state, n1)) list.add(n1)
		}
		val n2 = pow2(level + 1) + (j * 2)
		if (n2 <= n) {
			if (!isRed(state, n2)) list.add(n2)
			val n3 = n2 + 1
			if (n3 <= n && !isRed(state, n3))
				list.add(n3)
		}
		return list
	}
}

// 1-2-3-4-5
class Line: WinAsSecond3() {
	override fun neighbors(state: Int, i: Int): List<Int> {
		val list = mutableListOf<Int>()
		if (i > 1 && !isRed(state, i - 1)) list.add(i - 1)
		if (i < n && !isRed(state, i + 1)) list.add(i + 1)
		return list
	}
}

//   3
//   |
// 1-2-4-5-6 ...
class Line1: WinAsSecond3() {
	override fun neighbors(state: Int, i: Int): List<Int> {
		val list = mutableListOf<Int>()
		if (i == 2 && !isRed(state, 4)) list.add(4)
		if (i == 4 && !isRed(state, 2)) list.add(2)
		if (i > 1 && i != 4 && !isRed(state, i - 1)) list.add(i - 1)
		if (i < n && i != 3 && !isRed(state, i + 1)) list.add(i + 1)
		return list
	}
}

//   4
//   |
//   3
//   |
// 1-2-5-6-7 ...
class Line2: WinAsSecond3() {
	override fun neighbors(state: Int, i: Int): List<Int> {
		val list = mutableListOf<Int>()
		if (i == 2 && !isRed(state, 5)) list.add(5)
		if (i == 5 && !isRed(state, 2)) list.add(2)
		if (i > 1 && i != 5 && !isRed(state, i - 1)) list.add(i - 1)
		if (i < n && i != 4 && !isRed(state, i + 1)) list.add(i + 1)
		return list
	}
}

abstract class WinAsSecond3 {
	private val lost = 0
	private val dp = mutableMapOf<Int, Int>()
	protected var n = 1
	private var winMask = 1

	protected abstract fun neighbors(state: Int, i: Int): List<Int>

	private fun init(n: Int) {
		dp.clear()
		this.n = n
		winMask = (1 shl n) - 1
	}

	fun calculate(nMax: Int) {
		for (i in 1..nMax) {
			init(i)
			dp(0)
			println("$i: " + dp[0])
			//if (dp[0] == lost) print("$n, ")
		}
		//println(dp)
	}

	private fun isWin(state: Int) = state and winMask == winMask

	// i = 1, 2, ...
	protected fun isRed(state: Int, i: Int) = (state and (1 shl (i - 1))) > 0

	private fun dp(state: Int) {
		if (dp.contains(state)) return
		if (isWin(state)) {
			dp[state] = lost
			return
		}
		for (i in 1..n) {
			if (isRed(state, i)) continue
			var move = 1 shl (i - 1)
			if (checkMove(state, move)) return
			val neighbors = neighbors(state, i)
			val sz = neighbors.size
			val allMasks = 1 shl sz
			for (k in 1 until allMasks) {
				move = 1 shl (i - 1)
				for (j in 0 until sz) if (k and (1 shl j) > 0)
					move = move or (1 shl (neighbors[j] - 1))
				if (checkMove(state, move)) return
			}
		}
		dp[state] = lost
	}

	private fun checkMove(state: Int, move: Int): Boolean {
		val newState = state or move
		dp(newState)
		if (dp[newState] == lost) {
			dp[state] = move
			return true
		}
		return false
	}
}
