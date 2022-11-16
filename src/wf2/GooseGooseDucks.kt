package wf2

import java.util.*
import kotlin.collections.ArrayDeque

fun main() {
	val t = readln().toInt()
	for (case in 1..t) {
		val (n, m, s) = readln().split(' ').map { it.toInt() }
		val ggd = GooseGooseDucks(n, m, s)
		for (i in 0 until m) {
			val (x, y, c) = readln().split(' ').map { it.toInt() }
			ggd.addMeeting(x, y, c)
		}
		for (j in 0 until s) {
			val (a, b, u, v, d) = readln().split(' ').map { it.toInt() }
			ggd.addStatement(a - 1, b - 1, u, v, d)
		}
		println("Case #$case: ${ggd.minDucks()}")
	}
}

class GooseGooseDucks(private val n: Int, private val m: Int, private val s: Int) {

	private class Point(var x: Int, var y: Int, var t: Int): Comparable<Point> {
		override fun toString() = "($x, $y, $t)"
		override fun compareTo(other: Point): Int {
			if (t != other.t) return t.compareTo(other.t)
			if (x != other.x) return x.compareTo(other.x)
			return y.compareTo(other.y)
		}
	}

	private class Statement(val a: Int, val b: Int, val point: Point): Comparable<Statement> {
		override fun toString() = "[$a, $b, $point]"
		override fun compareTo(other: Statement): Int {
			val pCmp = point.compareTo(other.point)
			if (pCmp != 0) return pCmp
			if (a != other.a) return a.compareTo(other.a)
			return b.compareTo(other.b)
		}
	}

	private enum class VisitType {
		VISIT,
		VISIT_EDGE,
		POST_VISIT
	}

	private val meetings = mutableListOf<Point>()
	private val statements = mutableListOf<Statement>()
	private val ducks = BooleanArray(n)

	fun addMeeting(x: Int, y: Int, c: Int) {
		meetings.add(Point(x, y, c))
	}

	fun addStatement(a: Int, b: Int, u: Int, v: Int, d: Int) {
		statements.add(Statement(a, b, Point(u, v, d)))
	}

	private fun consistent(p1: Point, p2: Point): Boolean {
		val dx = (p1.x - p2.x).toLong()
		val dy = (p1.y - p2.y).toLong()
		val dt = (p1.t - p2.t).toLong()
		return dx * dx + dy * dy <= dt * dt
	}

	private fun consistent(s1: Statement, s2: Statement) = consistent(s1.point, s2.point)

	private fun checkMeetings(): List<List<Int>> {
		val suspectStat = List<MutableList<Int>>(n) { mutableListOf() }
		val stByTime = statements.sortedBy { it.point.t }
		if (meetings.size < 2) {
			val meeting = meetings.first()
			for (st in stByTime) {
				val cons = consistent(st.point, meeting)
				if (!cons) {
					suspectStat[st.b].add(st.a)
				}
			}
		} else {
			val lastMeeting = meetings.last()
			val mIterator = meetings.windowed(2).iterator()
			var (m1, m2) = mIterator.next()
			for (st in stByTime) {
				val cons: Boolean
				if (st.point.t <= m1.t) {
					cons = consistent(st.point, m1)
				} else if (st.point.t >= lastMeeting.t) {
					cons = consistent(st.point, lastMeeting)
				} else {
					while (st.point.t !in m1.t until m2.t)
						mIterator.next().also { m1 = it[0]; m2 = it[1] }
					cons = consistent(st.point, m1) && consistent(st.point, m2)
				}
				if (!cons) {
					suspectStat[st.b].add(st.a)
				}
			}
		}
		return suspectStat
	}

	private fun checkStatements(): Int {
		val consStat = List<TreeSet<Statement>>(n) { sortedSetOf() }
		for (st in statements) {
			handleStatement(st, consStat[st.a])
			handleStatement(st, consStat[st.b])
		}
		return ducks.count { it }
	}

	private fun handleStatement(st: Statement, birdStatements: TreeSet<Statement>) {
		birdStatements.add(st)
		val iterBack = birdStatements.descendingSet().tailSet(st, false).iterator()
		while (iterBack.hasNext()) {
			val prev = iterBack.next()
			if (consistent(prev, st)) {
				break
			} else {
				ducks[prev.a] = true
				iterBack.remove()
			}
		}
		val iterForward = birdStatements.tailSet(st, false).iterator()
		while (iterForward.hasNext()) {
			val next = iterForward.next()
			if (consistent(next, st)) {
				break
			} else {
				ducks[next.a] = true
				iterForward.remove()
			}
		}
	}

	private fun bfs(suspectStatements: List<List<Int>>): Int {
		val queue = ArrayDeque<Int>()
		for (i in ducks.indices)
			if (ducks[i]) queue.add(i)
		while (queue.isNotEmpty()) {
			val b = queue.removeFirst()
			for (a in suspectStatements[b]) {
				if (ducks[a]) continue
				ducks[a] = true
				queue.add(a)
			}
		}
		return ducks.count { it }
	}

	// Path-based strong component algorithm
	private fun minSCC(suspectStatements: List<List<Int>>): Int {
		val solution = mutableListOf<Set<Int>>()
		val stackS = ArrayDeque<Int>()
		val stackP = ArrayDeque<Int>()
		val preorderNumbers = IntArray(n) { -1 }
		val assignedNodeSet = mutableSetOf<Int>()
		var c = 0
		val stack = ArrayDeque<Triple<Int, Int, VisitType>>()
		val mayBeLeaf = BooleanArray(n)
		val notLeaf = BooleanArray(n)
		val absNotLeaf = BooleanArray(n)
		for (i in 0 until n) {
			if (preorderNumbers[i] != -1) continue
			stack.addLast(Triple(i, -1, VisitType.VISIT))
			while (stack.isNotEmpty()) {
				val (v, u, visitType) = stack.removeLast()
				when (visitType) {
					VisitType.VISIT -> {
						preorderNumbers[v] = c++
						stackS.addLast(v)
						stackP.addLast(v)
						stack.addLast(Triple(v, -1, VisitType.POST_VISIT))
						val vList = suspectStatements[v]
						if (vList.isEmpty()) mayBeLeaf[v] = true
						for (w in vList) {
							stack.addLast(Triple(v, w, VisitType.VISIT_EDGE))
						}
					}
					VisitType.VISIT_EDGE -> {
						val w = u
						if (preorderNumbers[w] == -1) {
							notLeaf[v] = true
							stack.addLast(Triple(w, -1, VisitType.VISIT))
						} else {
							if (!assignedNodeSet.contains(w)) {
								mayBeLeaf[v] = true
								while (preorderNumbers[stackP.last()] > preorderNumbers[w])
									stackP.removeLast()
							} else {
								notLeaf[v] = true
								absNotLeaf[v] = true
							}
						}
					}
					VisitType.POST_VISIT -> {
						if (v == stackP.last()) {
							stackP.removeLast()
							var mayBeLeafComp = false
							var notLeafComp = false
							val component = mutableSetOf<Int>()
							do {
								val topOfStackS = stackS.removeLast()
								component.add(topOfStackS)
								assignedNodeSet.add(topOfStackS)
								if (mayBeLeaf[topOfStackS] && !notLeaf[topOfStackS]) mayBeLeafComp = true
								if (absNotLeaf[topOfStackS]) notLeafComp = true
							} while (topOfStackS != v)
							if (mayBeLeafComp && !notLeafComp) {
								solution.add(component)
							}
						}
					}
				}
			}
		}
		return solution.minOf { it.size }
	}

	fun minDucks(): Int {
		val suspectStat = checkMeetings()
		val ducksCount = checkStatements()
		return if (ducksCount > 0)
			bfs(suspectStat)
		else
			minSCC(suspectStat)
	}
}
