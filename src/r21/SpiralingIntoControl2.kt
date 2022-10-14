package r21

import kotlin.math.abs

fun main() {
	fun distance(x1: Int, y1: Int, x2: Int, y2: Int) = abs(x2 - x1) + abs(y2 - y1)

	val t = readln().toInt()
	for (case in 1..t) {
		val (n, k) = readln().split(' ').map { it.toInt() }
		if (k < n - 1) {
			println("Case #$case: IMPOSSIBLE")
			continue
		}
		val rooms = Array(n) { IntArray(n) }
		var x = 0
		var y = 0
		var direction = 0
		for (i in 1 .. (n * n)) {
			rooms[y][x] = i
			when(direction) {
				0 -> if (x >= n - 1 || rooms[y][x + 1] != 0) direction = 1
				1 -> if (y >= n - 1 || rooms[y + 1][x] != 0) direction = 2
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

		//for (i in 0 until n) {
		//	for (j in 0 until n) {
		//		print(rooms[i][j].toString().padStart(2, ' '))
		//		print(" ")
		//	}
		//	println()
		//}
		//println()

		val xc = n / 2
		val yc = n / 2
		val shortcuts = mutableListOf<Pair<Int, Int>>()
		x = 0
		y = 0
		var r = k
		var d = distance(x, y, xc, yc)
		while (d > 0) {
			val moves = mutableListOf<Triple<Int, Int, Int>>()
			if (x < n - 1 && rooms[y][x] < rooms[y][x + 1])
				moves.add(Triple(x + 1, y, rooms[y][x + 1] - rooms[y][x]))
			if (y < n - 1 && rooms[y][x] < rooms[y + 1][x])
				moves.add(Triple(x, y + 1, rooms[y + 1][x] - rooms[y][x]))
			if (x > 0 && rooms[y][x] < rooms[y][x - 1])
				moves.add(Triple(x - 1, y, rooms[y][x - 1] - rooms[y][x]))
			if (y > 0 && rooms[y][x] < rooms[y - 1][x])
				moves.add(Triple(x, y - 1, rooms[y - 1][x] - rooms[y][x]))
			moves.sortBy { it.third }
			for ((x2, y2, s) in moves) {
				val d2 = distance(x2, y2, xc, yc)
				if (d2 <= r - 1) {
					if (s > 1)
						shortcuts.add(rooms[y][x] to rooms[y2][x2])
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
