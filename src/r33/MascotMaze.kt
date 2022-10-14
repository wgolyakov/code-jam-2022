package r33

fun main() {
	class Room(val num: Int) {
		lateinit var left: Room
		lateinit var right: Room
		val back = mutableSetOf<Room>()
		var mascot: Int = -1
		override fun toString() = num.toString()
	}

	val mascots = "ACDEHIJKMORST"
	val mNum = mascots.length
	val t = readln().toInt()
	caseLoop@ for (case in 1..t) {
		val n = readln().toInt()
		val l = readln().split(' ').map { it.toInt() }.toIntArray()
		val r = readln().split(' ').map { it.toInt() }.toIntArray()
		for (i in 0 until n) {
			val left = l[i] - 1
			val right = r[i] - 1
			if (l[left] == i + 1 || r[left] == i + 1 || l[right] == i + 1 || r[right] == i + 1) {
				println("Case #$case: IMPOSSIBLE")
				continue@caseLoop
			}
		}
		val rooms = Array(n) { Room(it + 1) }
		for (i in 0 until n) {
			val room = rooms[i]
			val left = rooms[l[i] - 1]
			val right = rooms[r[i] - 1]
			room.left = left
			room.right = right
			left.back.add(room)
			right.back.add(room)
		}
		val randomRooms = rooms.toMutableList()
		do {
			var ok = true
			for (room in rooms)
				room.mascot = -1
			randomRooms.shuffle()
			for (room in randomRooms) {
				val neighbours = mutableListOf<Room>()
				neighbours.add(room.left)
				neighbours.add(room.left.left)
				neighbours.add(room.left.right)
				neighbours.add(room.right)
				neighbours.add(room.right.left)
				neighbours.add(room.right.right)
				neighbours.addAll(room.back)
				for (br in room.back)
					neighbours.addAll(br.back)
				val usedMascots = neighbours.map { it.mascot }.toMutableSet()
				usedMascots.remove(-1)
				if (usedMascots.size >= mNum) {
					ok = false
					break
				}
				for (mi in 0 until mNum) {
					if (mi !in usedMascots) {
						room.mascot = mi
						break
					}
				}
			}
		} while (!ok)
		val result = rooms.joinToString("") { mascots[it.mascot].toString() }
		println("Case #$case: $result")
	}
}
