package r1a3

import java.util.*
import kotlin.math.min

fun main() {
	val t = readln().toInt()
	for (case in 1..t) {
		val (e, w) = readln().split(' ').map { it.toInt() }
		val x = Array(e) { intArrayOf() }
		for (i in 0 until e)
			x[i] = readln().split(' ').map { it.toInt() }.toIntArray()
		val result = Weightlifting2(e, w, x).run()
		println("Case #$case: $result")
	}
}

class Weightlifting2(private val e: Int, private val w: Int, private val x: Array<IntArray>) {

	inner class State(val stack: List<Int>) {
		var minOperations: Int? = null
		override fun toString() = stack.toString()
	}

	fun run(): Int {
		// Remove static bottom weights
		var staticOper = 0
		for (j in 0 until w) {
			val minX = x.minOf { it[j] }
			for (xi in x)
				xi[j] -= minX
			staticOper += minX * 2
		}

		//for (xi in x)
		//	println(xi.joinToString())
		//println()

		val exercises = mutableListOf<List<State>>()
		for (xi in x) {
			val states = mutableListOf<State>()
			val weights = mutableListOf<Int>()
			for (j in xi.indices)
				for (n in 0 until xi[j])
					weights.add(j + 1)
			states.add(State(weights.toList()))
			while (nextPermutation(weights))
				states.add(State(weights.toList()))
			exercises.add(states)
		}

		val root = State(emptyList())
		val minOper = fillTree(root, exercises, 0)
		return staticOper + minOper
	}

	private fun nextPermutation(a: MutableList<Int>): Boolean {
		val n = a.size
		if (n == 0) return false
		var j = n - 2
		while (j != -1 && a[j] >= a[j + 1]) j--
		if (j == -1) return false
		var k = n - 1
		while (a[j] >= a[k]) k--
		Collections.swap(a, j, k)
		var l = j + 1
		var r = n - 1
		while (l < r) Collections.swap(a, l++, r--)
		return true
	}

	private fun fillTree(parent: State, exercises: List<List<State>>, i: Int): Int {
		if (i == exercises.size)
			return parent.stack.size
		var minOper = Int.MAX_VALUE
		for (state in exercises[i]) {
			var oper = state.minOperations
			if (oper == null) {
				oper = fillTree(state, exercises, i + 1)
				state.minOperations = oper
			}
			oper += countOperations(parent, state)
			if (oper < minOper)
				minOper = oper
		}
		return minOper
	}

	private fun countOperations(parent: State, child: State): Int {
		val s1 = parent.stack
		val s2 = child.stack
		val minSize = min(s1.size, s2.size)
		var diffIndex = minSize
		for (i in 0 until minSize) {
			if (s1[i] != s2[i]) {
				diffIndex = i
				break
			}
		}
		return s1.size - diffIndex + s2.size - diffIndex
	}
}
