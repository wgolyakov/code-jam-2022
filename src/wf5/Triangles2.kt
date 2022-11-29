package wf5

import kotlin.math.max
import kotlin.math.min

fun main() {
	val t = readln().toInt()
	for (case in 1..t) {
		val n = readln().toInt()
		val p = mutableListOf<Point>()
		for (i in 0 until n) {
			val (x, y) = readln().split(' ').map { it.toInt() }
			p.add(Point(i, x, y))
		}
		val result = triangles(n, p)
		println("Case #$case: ${result.size}")
		for (tr in result) println(tr.numbers())
	}
}

private class Point(val i: Int, val x: Int, val y: Int) {
	override fun toString() = "$i"
	override fun hashCode() = i
	override fun equals(other: Any?) = this === other || i == (other as? Point)?.i
}

private class Segment(val p1: Point, val p2: Point) {
	override fun toString() = "[$p1, $p2]"
}

private class Triangle(val p1: Point, val p2: Point, val p3: Point) {
	fun points() = listOf(p1, p2, p3)
	fun numbers() = "${p1.i + 1} ${p2.i + 1} ${p3.i + 1}"
	fun isCollinear() = (p3.y - p1.y).toLong() * (p2.x - p1.x) == (p3.x - p1.x).toLong() * (p2.y - p1.y)
	fun sides(): List<Segment> = listOf(Segment(p1, p2), Segment(p2, p3), Segment(p3, p1))
	override fun toString() = "<$p1, $p2, $p3>"
	override fun hashCode() = listOf(p1.i, p2.i, p3.i).hashCode()
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		val tr = (other as? Triangle) ?: return false
		return p1 == tr.p1 && p2 == tr.p2 && p3 == tr.p3
	}
}

private var maxCount = 0
private var maxTriangles = emptyList<Triangle>()

private fun triangles(n: Int, p: List<Point>): List<Triangle> {
	maxCount = 0
	maxTriangles = emptyList()
	iterTriangles(emptyList(), p)
	return maxTriangles
}

private fun iterTriangles(triangles: List<Triangle>, points: List<Point>) {
	if (points.size < 3) {
		val good = filterGoodTriangles(triangles)
		if (good.size > maxCount) {
			maxCount = good.size
			maxTriangles = good
		}
		return
	}
	for (i in points.indices) {
		val p1 = points[i]
		for (j in i + 1 until points.size) {
			val p2 = points[j]
			for (k in j + 1 until points.size) {
				val p3 = points[k]
				iterTriangles(triangles + listOf(Triangle(p1, p2, p3)), points - setOf(p1, p2, p3))
			}
		}
	}
}

private fun filterGoodTriangles(triangles: List<Triangle>): List<Triangle> {
	val goodTriangles = triangles.filter { !it.isCollinear() }
	val bad = mutableMapOf<Int, MutableSet<Int>>()
	for (i in goodTriangles.indices) {
		val tr1 = goodTriangles[i]
		for (j in i + 1 until goodTriangles.size) {
			val tr2 = goodTriangles[j]
			if (!check(tr1, tr2)) {
				bad.getOrPut(i) { mutableSetOf() }.add(j)
				bad.getOrPut(j) { mutableSetOf() }.add(i)
			}
		}
	}
	val removed = mutableSetOf<Int>()
	for (i in bad.entries.sortedBy { it.value.size }.map { it.key }.reversed()) {
		if (bad[i]!!.isNotEmpty()) {
			bad.remove(i)
			removed.add(i)
			for (set in bad.values)
				set.remove(i)
		}
	}
	return goodTriangles.withIndex().filter { (i, _) -> i !in removed }.map { (_, tr) -> tr }
}

private fun check(triangles: List<Triangle>): Boolean {
	for (tr in triangles)
		if (tr.isCollinear()) return false
	for (i in triangles.indices) {
		val tr1 = triangles[i]
		for (j in i + 1 until triangles.size) {
			val tr2 = triangles[j]
			if (!check(tr1, tr2))
				return false
		}
	}
	return true
}

private fun check(tr1: Triangle, tr2: Triangle): Boolean {
	for (s1 in tr1.sides()) {
		for (s2 in tr2.sides()) {
			if (lineSegmentsIntersectsEx(s1, s2))
				return false
		}
	}
	val inTr1 = trianglePointsContained(tr1, tr2)
	val inTr2 = trianglePointsContained(tr2, tr1)
	if ((inTr1 == 1 && inTr2 == 2) || (inTr1 == 2 && inTr2 == 1))
		return false
	return true
}

private fun lineSegmentsIntersectsEx(s1: Segment, s2: Segment): Boolean {
	val dx1 = (s1.p1.x - s1.p2.x).toLong()
	val dx2 = (s1.p1.x - s2.p1.x).toLong()
	val dx3 = (s2.p1.x - s2.p2.x).toLong()
	val dy1 = (s1.p1.y - s1.p2.y).toLong()
	val dy2 = (s1.p1.y - s2.p1.y).toLong()
	val dy3 = (s2.p1.y - s2.p2.y).toLong()
	val d = dx1 * dy3 - dy1 * dx3
	val t = dx2 * dy3 - dy2 * dx3
	val u = dx2 * dy1 - dy2 * dx1
	if (d == 0L) {
		if (t == 0L || u == 0L) {
			// Segments on one line
			val x1 = min(s1.p1.x, s1.p2.x)
			val x2 = max(s1.p1.x, s1.p2.x)
			val x3 = min(s2.p1.x, s2.p2.x)
			val x4 = max(s2.p1.x, s2.p2.x)
			val y1 = min(s1.p1.y, s1.p2.y)
			val y2 = max(s1.p1.y, s1.p2.y)
			val y3 = min(s2.p1.y, s2.p2.y)
			val y4 = max(s2.p1.y, s2.p2.y)
			return if (x1 == x2)
				max(y1, y3) < min(y2, y4)
			else
				max(x1, x3) < min(x2, x4)
		} else {
			// Segments on parallel lines
			return false
		}
	}
	return if (d > 0)
		t > 0.0 && t < d && u > 0.0 && u < d
	else
		t > d && t < 0.0 && u > d && u < 0.0
}

private fun trianglePointsContained(tr1: Triangle, tr2: Triangle): Int {
	return tr2.points().count { triangleContainsPoint(tr1, it) }
}

private fun triangleContainsPoint(t: Triangle, p: Point): Boolean {
	val a = (t.p1.x - p.x).toLong() * (t.p2.y - t.p1.y) - (t.p2.x - t.p1.x).toLong() * (t.p1.y - p.y)
	val b = (t.p2.x - p.x).toLong() * (t.p3.y - t.p2.y) - (t.p3.x - t.p2.x).toLong() * (t.p2.y - p.y)
	val c = (t.p3.x - p.x).toLong() * (t.p1.y - t.p3.y) - (t.p1.x - t.p3.x).toLong() * (t.p3.y - p.y)
	return ((a >= 0 && b >= 0 && c >= 0) || (a <= 0 && b <= 0 && c <= 0))
}
