package r34

import kotlin.math.log
import kotlin.math.log2
import kotlin.math.pow

fun main() {
	WinAsSecondLine().calculate(20)
	// Second win: no
	println("----------")
	WinAsSecondBinTree().calculate(20)
	// Second win: 6
	println("----------")
	WinAsSecond3Tree().calculate(20)
	// Second win: 16
	println("----------")
	WinAsSecondNTreeL3(2).calculate(20)
	//println("----------")
	//for (w in 1..20) {
	//	print("$w: Second win: ")
	//	WinAsSecondNTreeL3(w).calculate(24)
	//	println("")
	//}
	// 1: Second win: no
	// 2: Second win: 6, 12, 18, 24, 30, 36, ...
	// 3: Second win: 8, 16, 24, 32, 40, ...
	// 4: Second win: 8, 10, 18, 20, 28, 30, 38, 40, ...
	// 5: Second win: 10, 12, 22, 24, 34, 36, ...
	// 6: Second win: 10, 12, 14, 24, 26, 28, 38, 40, ...
	println("----------")
	WinAsSecondTest1().calculate()
	println("----------")
	WinAsSecondTest2().calculate()
	println("----------")
	for (w in 3..20) {
		WinAsSecondTest3(w).calculate()
	}
	println("----------")
	WinAsSecondTest3(7).calculate()
}

// 1 +
// 1 1 -
// 1 2 +
// 1 3 +
// 1 4 +
// 1 5 -
// 1 6 +
// 1 7 +
// 1 8 +
// 1 9 -
// 1 10 +
// 1 11 +
// 1 12 +
// 1 13 +
// 1 14 +
// 1 15 -
// 1 16 +
// 1 17 +
// 1 18 +
// 1 19 +
// 1 20 +
// 1 21 -
// 1 22 +

// 1 2 3 -

// 1 1 1 1 -
// 1 1 1 2 +
// 1 1 1 3 +
// 1 1 1 4 +
// 1 1 1 5 -
// 1 1 1 6 +
// 1 1 1 7 +
// 1 1 1 8 +
// 1 1 1 9 -
// 1 1 1 10 +
// 1 1 1 11 +
// 1 1 1 12 +
// 1 1 1 13 +
// 1 1 1 14 +
// 1 1 1 15 -
// 1 1 1 16 +
// 1 1 1 17 +
// 1 1 1 18 +
// 1 1 1 10 +

// 2 1 +
// 2 2 -
// 2 3 +
// 2 4 +
// 2 5 +
// 2 6 +
// 2 7 +
// 2 8 -
// 2 9 +
// 2 10 +
// 2 11 +
// 2 12 +
// 2 13 +
// 2 14 +
// 2 15 +
// 2 16 +
// 2 17 +
// 2 18 +
// 2 19 +
// 2 20 +
// 2 21 +

// 3 1 +
// 3 2 +
// 3 3 -
// 3 4 +
// 3 5 +
// 3 6 +
// 3 7 -
// 3 8 +
// 3 9 +
// 3 10 +
// 3 11 +
// 3 12 +
// 3 13 +
// 3 14 +
// 3 15 +
// 3 16 +
// 3 17 +
// 3 18 +
// 3 19 +
// 3 20 +

// 4 1 +
// 4 2 +
// 4 3 +
// 4 4 -
// 4 5 +
// 4 6 +
// 4 7 +
// 4 8 +
// 4 9 +
// 4 10 +
// 4 11 +
// 4 12 -
// 4 13 +
// 4 14 +
// 4 15 +
// 4 16 +
// 4 17 +
// 4 18 -

// 1-<2>-3-...
class WinAsSecondTest3(private val w: Int): WinAsSecond2() {
	override fun neighbors(state: State, i: Int): List<Int> {
		val list = mutableListOf<Int>()
		if (i in 2..w && !state.red[i - 1 - 1]) list.add(i - 1)
		if (i < w && !state.red[i - 1 + 1]) list.add(i + 1)
		return list
	}

	fun calculate() {
		val red = MutableList(w) { false }
		red[2 - 1] = true
		dp(State(red))
		println("${red.size - 2}: " + dp[State(red)])
	}
}

// 1-2-3-4-5-<6>-7-8-9-10-11-<12>-13-14-15-16-17
//   |                 |                   |
//  18                19                  20
class WinAsSecondTest2: WinAsSecond2() {
	override fun neighbors(state: State, i: Int): List<Int> {
		val list = mutableListOf<Int>()
		if (i in 2..17 && !state.red[i - 1 - 1]) list.add(i - 1)
		if (i < 17 && !state.red[i - 1 + 1]) list.add(i + 1)
		if (i == 2 && !state.red[18 - 1]) list.add(18)
		if (i == 10 && !state.red[19 - 1]) list.add(19)
		if (i == 16 && !state.red[20 - 1]) list.add(20)
		if (i == 18 && !state.red[2 - 1]) list.add(2)
		if (i == 19 && !state.red[10 - 1]) list.add(10)
		if (i == 20 && !state.red[16 - 1]) list.add(16)
		return list
	}

	fun calculate() {
		val red = MutableList(20) { false }
		red[6 - 1] = true
		red[12 - 1] = true
		dp(State(red))
		println("20: " + dp[State(red)])
	}
}

// 1-2-3-4-5-<6>-7-8-9-10-11
//   |                 |
//  12                13
class WinAsSecondTest1: WinAsSecond2() {
	override fun neighbors(state: State, i: Int): List<Int> {
		val list = mutableListOf<Int>()
		if (i in 2..11 && !state.red[i - 1 - 1]) list.add(i - 1)
		if (i < 11 && !state.red[i - 1 + 1]) list.add(i + 1)
		if (i == 2 && !state.red[12 - 1]) list.add(12)
		if (i == 10 && !state.red[13 - 1]) list.add(13)
		if (i == 12 && !state.red[2 - 1]) list.add(2)
		if (i == 13 && !state.red[10 - 1]) list.add(10)
		return list
	}

	fun calculate() {
		val red = MutableList(13) { false }
		red[6 - 1] = true
		dp(State(red))
		println("13: " + dp[State(red)])
	}
}

//          1
//       /     \       \
//      2       6  ... 30
//    /|||\   /|||\
//   3 4 5... 7 8 9...
class WinAsSecondNTreeL3(private val w: Int): WinAsSecond2() {
	override fun neighbors(state: State, i: Int): List<Int> {
		val list = mutableListOf<Int>()
		if (i == 1) {
			for (n in 2..state.size() step w + 1)
				if (!state.red[n - 1])
					list.add(n)
		} else {
			val t = (i - 2) / (w + 1)
			val j = i - 2 - t * (w + 1)
			if (j == 0) {
				val n1 = 1
				if (n1 <= state.size() && !state.red[n1 - 1]) list.add(n1)
				for (e in 1..w) {
					val n = i + e
					if (n <= state.size() && !state.red[n - 1]) list.add(n)
				}
			} else {
				val n = i - j
				if (n <= state.size() && !state.red[n - 1]) list.add(n)
			}
		}
		return list
	}
}

//          1
//     /    |     \
//    2     3      4
//  / | \ / | \  / | \
// 4  5 6 7 8 9 10 11 12
//         ...
class WinAsSecond3Tree: WinAsSecond2() {
	private fun pow3(x: Int) = 3.0.pow(x).toInt()
	private fun log3(x: Int) = log(x.toDouble(), 3.0).toInt()

	override fun neighbors(state: State, i: Int): List<Int> {
		val list = mutableListOf<Int>()
		val level = log3(i + 1)
		val j = i - pow3(level)
		if (i > 1) {
			val n1 = pow3(level - 1) + (j / 3)
			if (!state.red[n1 - 1]) list.add(n1)
		}
		val n2 = pow3(level + 1) + (j * 3)
		if (n2 <= state.size()) {
			if (!state.red[n2 - 1]) list.add(n2)
			val n3 = n2 + 1
			if (n3 <= state.size()) {
				if (!state.red[n3 - 1]) list.add(n3)
				val n4 = n3 + 1
				if (n4 <= state.size() && !state.red[n4 - 1])
					list.add(n4)
			}
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
class WinAsSecondBinTree: WinAsSecond2() {
	private fun pow2(x: Int) = 1 shl x

	override fun neighbors(state: State, i: Int): List<Int> {
		val list = mutableListOf<Int>()
		val level = log2(i.toDouble()).toInt()
		val j = i - pow2(level)
		if (i > 1) {
			val n1 = pow2(level - 1) + (j / 2)
			if (!state.red[n1 - 1]) list.add(n1)
		}
		val n2 = pow2(level + 1) + (j * 2)
		if (n2 <= state.size()) {
			if (!state.red[n2 - 1]) list.add(n2)
			val n3 = n2 + 1
			if (n3 <= state.size() && !state.red[n3 - 1])
				list.add(n3)
		}
		return list
	}
}

// 1-2-3-4-5
class WinAsSecondLine: WinAsSecond2() {
	override fun neighbors(state: State, i: Int): List<Int> {
		val list = mutableListOf<Int>()
		if (i > 1 && !state.red[i - 1 - 1])
			list.add(i - 1)
		if (i < state.size() && !state.red[i - 1 + 1])
			list.add(i + 1)
		return list
	}

	override fun simplify(state: State): State {
		val red = mutableListOf<Boolean>()
		var prevR = false
		for (r in state.red) {
			if (!(r && prevR))
				red.add(r)
			prevR = r
		}
		return State(red)
	}
}

abstract class WinAsSecond2 {
	data class State(val red: List<Boolean>) {
		constructor(n: Int): this(List(n) { false })
		fun size() = red.size
		fun isWin() = red.all { it }
		override fun toString() = red.map { if (it) '#' else '-' }.joinToString("", "(", ")")
	}

	private val lost = emptyList<Int>()
	protected val dp = mutableMapOf<State, List<Int>>()

	protected abstract fun neighbors(state: State, i: Int): List<Int>

	fun calculate(nMax: Int) {
		for (n in 1..nMax) {
			dp(State(n))
			println("$n: " + dp[State(n)])
			//if (dp[State(n)] == lost) print("$n, ")
		}
		//println(dp)
	}

	protected fun dp(state: State) {
		if (dp.contains(state)) return
		if (state.isWin()) {
			dp[state] = lost
			return
		}
		for (i in 1..state.size()) {
			if (state.red[i - 1]) continue
			if (checkMove(state, listOf(i))) return
			val neighbors = neighbors(state, i)
			val sz = neighbors.size
			val allMasks = 1L shl sz
			for (k in 1L until allMasks) {
				val move = mutableListOf(i)
				for (j in 0 until sz)
					if (k and (1L shl j) > 0)
						move.add(neighbors[j])
				if (checkMove(state, move)) return
			}
		}
		dp[state] = lost
	}

	private fun checkMove(state: State, move: List<Int>): Boolean {
		val r = state.red.toMutableList()
		for (m in move) r[m - 1] = true
		val newState = simplify(State(r))
		dp(newState)
		if (dp[newState] == lost) {
			dp[state] = move
			return true
		}
		return false
	}

	protected open fun simplify(state: State): State = state
}
