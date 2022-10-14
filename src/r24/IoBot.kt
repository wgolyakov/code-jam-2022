package r24

import kotlin.math.min

fun main() {
	class Station(val x: Int, val s: Int) {
		override fun toString() = "($x, $s)"
	}

	fun calculate(stations: MutableList<Station>, c: Int): Long {
		if (stations.isEmpty())
			return 0L
		stations.sortBy { it.x }
		val n = stations.size
		val balance = mutableMapOf<Int, Int>()
		var count0 = 0
		var count1 = 0
		if (stations[0].s == 0) count0++ else count1++
		balance[count0 - count1] = 0
		val sums = arrayOf(LongArray(n + 1), LongArray(n + 1))
		sums[stations[0].s][1] = stations[0].x.toLong()
		val units = LongArray(n + 1)
		units[0] = 0L
		units[1] = 2L * stations[0].x
		for (i in 1 until n) {
			val st = stations[i]
			val x = st.x
			val s = st.s
			if (s == 0) count0++ else count1++
			val b = count0 - count1
			val k = balance.put(b, i) ?: -1
			for (r in 0..1)
				sums[r][i + 1] = sums[r][i] + if (r == s) x else 0
			if (stations[i - 1].s != s) {
				units[i + 1] = units[i - 1] + 2L * x
			} else {
				val units1 = units[i - 1] + c + 2L * x
				val units2 = if (k != -1 || b == 0)
					units[k + 1] + 2L * (sums[s][i + 1] - sums[s][k + 1])
				else
					Long.MAX_VALUE
				val units3 = units[i] + 2L * x
				units[i + 1] = min(units1, min(units2, units3))
			}
		}
		return units.last()
	}

	val t = readln().toInt()
	for (case in 1..t) {
		val (n, c) = readln().split(' ').map { it.toInt() }
		val positiveStations = mutableListOf<Station>()
		val negativeStations = mutableListOf<Station>()
		for (i in 0 until n) {
			val (xi, si) = readln().split(' ').map { it.toInt() }
			if (xi > 0)
				positiveStations.add(Station(xi, si))
			else
				negativeStations.add(Station(-xi, si))
		}
		var result = calculate(positiveStations, c)
		result += calculate(negativeStations, c)
		println("Case #$case: $result")
	}
}
