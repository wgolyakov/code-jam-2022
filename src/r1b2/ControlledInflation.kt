package r1b2

import kotlin.math.abs
import kotlin.math.min

fun main() {
	class Customer(val min: Int, val max: Int) {
		val length = max - min
		var upPresses = 0L
		var downPresses = 0L
	}

	val t = readln().toInt()
	for (case in 1..t) {
		val (n, p) = readln().split(' ').map { it.toInt() }
		val x = Array(n) { intArrayOf() }
		for (i in 0 until n)
			x[i] = readln().split(' ').map { it.toInt() }.toIntArray()

		val customers = mutableListOf<Customer>()
		for (xi in x) {
			val xMin = xi.minOrNull() ?: 0
			val xMax = xi.maxOrNull() ?: 0
			customers.add(Customer(xMin, xMax))
		}
		for (i in n - 1 downTo 0) {
			val c = customers[i]
			if (i == n - 1) {
				c.upPresses = c.length.toLong()
				c.downPresses = c.length.toLong()
			} else {
				val cNext = customers[i + 1]
				val upToMinPresses = cNext.upPresses + abs(c.max - cNext.min) + c.length
				val upToMaxPresses = cNext.downPresses + abs(c.max - cNext.max) + c.length
				val downToMinPresses = cNext.upPresses + abs(c.min - cNext.min) + c.length
				val downToMaxPresses = cNext.downPresses + abs(c.min - cNext.max) + c.length
				c.upPresses = min(upToMinPresses, upToMaxPresses)
				c.downPresses = min(downToMinPresses, downToMaxPresses)
			}
		}
		val c = customers[0]
		val result = c.upPresses + c.min
		println("Case #$case: $result")
	}
}
