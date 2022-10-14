package r21

import kotlin.math.abs

fun main() {
	fun distance(x1: Int, y1: Int, x2: Int, y2: Int) = abs(x2 - x1) + abs(y2 - y1)

	val nMax = 9999
	val rooms = Array(nMax) { IntArray(nMax) }
	var x = 0
	var y = 0
	var direction = 0
	for (i in 1 .. (nMax * nMax)) {
		rooms[y][x] = i
		when(direction) {
			0 -> if (x >= nMax - 1 || rooms[y][x + 1] != 0) direction = 1
			1 -> if (y >= nMax - 1 || rooms[y + 1][x] != 0) direction = 2
			2 -> if (x <= 0 || rooms[y][x - 1] != 0) direction = 3
			3 -> if (y <= 0 || rooms[y - 1][x] != 0) direction = 0
			else -> error("Wrong direction: $direction")
		}
		when(direction) {
			0 -> x++
			1 -> y++
			2 -> x--
			3 -> y--
			else -> error("Wrong direction: $direction")
		}
	}

	val t = readln().toInt()
	for (case in 1..t) {
		val (n, k) = readln().split(' ').map { it.toInt() }
		if (k < n - 1) {
			println("Case #$case: IMPOSSIBLE")
			continue
		}

		val r0 = nMax * nMax - n * n
		val c0 = nMax / 2 - n / 2

		val xc = n / 2
		val yc = n / 2
		x = 0
		y = 0
		var r = k

		for (i in 0..xc) {
			val j = xc - i
			val w = rooms[c0 + j][c0 + j]
			if (k - i * 2 >= w - r0 - 1) {
				x = j
				y = j
				r = k - (w - r0 - 1)
				break
			}
		}

		val shortcuts = mutableListOf<Pair<Int, Int>>()
		var d = distance(x, y, xc, yc)
		while (d > 0) {
			val w = rooms[c0 + y][c0 + x]
			val moves = mutableListOf<Triple<Int, Int, Int>>()
			if (x < n - 1 && w < rooms[c0 + y][c0 + x + 1])
				moves.add(Triple(x + 1, y, rooms[c0 + y][c0 + x + 1] - w))
			if (y < n - 1 && w < rooms[c0 + y + 1][c0 + x])
				moves.add(Triple(x, y + 1, rooms[c0 + y + 1][c0 + x] - w))
			if (x > 0 && w < rooms[c0 + y][c0 + x - 1])
				moves.add(Triple(x - 1, y, rooms[c0 + y][c0 + x - 1] - w))
			if (y > 0 && w < rooms[c0 + y - 1][c0 + x])
				moves.add(Triple(x, y - 1, rooms[c0 + y - 1][c0 + x] - w))
			moves.sortBy { it.third }
			for ((x2, y2, s) in moves) {
				val d2 = distance(x2, y2, xc, yc)
				if (d2 <= r - 1) {
					if (s > 1)
						shortcuts.add(w - r0 to rooms[c0 + y2][c0 + x2] - r0)
					x = x2
					y = y2
					d = d2
					r--
					break
				}
			}
		}

		if (r == 0) {
			println("Case #$case: ${shortcuts.size}")
			for (shortcut in shortcuts)
				println("${shortcut.first} ${shortcut.second}")
		} else {
			println("Case #$case: IMPOSSIBLE")
		}
	}
}
