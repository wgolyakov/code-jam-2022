package qr5

fun main() {
	val t = readln().toInt()
	for (case in 1..t) {
		val (n, k) = readln().split(' ').map { it.toInt() }
		val cave = Array(n) { mutableSetOf<Int>() }
		val exitCount = IntArray(n)
		var walkFromRoom: Int? = null
		for (exchange in 0..k) {
			val (r, p) = readln().split(' ').map { it.toInt() }
			exitCount[r - 1] = p
			if (walkFromRoom != null && walkFromRoom != r && walkFromRoom !in cave[r - 1]) {
				cave[r - 1].add(walkFromRoom)
				cave[walkFromRoom - 1].add(r)
			}
			if (exchange == k)
				break

			var walk = false
			var teleportRoom = -1
			val exploredExits = cave[r - 1].size
			val exits = exitCount[r - 1]
			if (exploredExits < exits / 2 + 1) {
				walk = true
			} else {
				// Find unexplored room
				teleportRoom = exitCount.indexOf(0)
				if (teleportRoom == -1) {
					// If there are unexplored exits in the current room
					if (exploredExits < exits) {
						walk = true
					} else {
						// Find room with unexplored exits
						teleportRoom = cave.withIndex().indexOfFirst { it.value.size < exitCount[it.index] }
					}
				}
			}
			if (walk) {
				walkFromRoom = r
				// W - walk through a random passage
				println("W")
			} else {
				if (teleportRoom == -1) {
					// All rooms already explored
					break
				}
				walkFromRoom = null
				// T num - teleport to room num
				println("T ${teleportRoom + 1}")
			}
		}
		// Unexplored rooms have a median number of exits in explored rooms
		val unexploredRooms = exitCount.count { it == 0 }
		val sumExits = exitCount.sumOf { it.toLong() }
		val sortedExits = exitCount.filter { it != 0 }.sorted()
		val medianExits = sortedExits[sortedExits.size / 2]
		val exits = sumExits + medianExits.toLong() * unexploredRooms
		val passages = exits / 2
		// E num - finish exploring and estimate that the cave contains num passages
		println("E $passages")
	}
}
