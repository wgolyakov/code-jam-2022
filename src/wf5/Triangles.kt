package wf5

import kotlin.math.max
import kotlin.math.sign

fun main() {
	val t = readln().toInt()
	for (case in 1..t)
		println("Case #$case: ${triangles()}")
}

private fun triangles(): String {
	val n = readln().toInt()
	val p = mutableListOf<List<Int>>()
	for (i in 0 until n)
		p.add(readln().split(' ').map { it.toInt() })
	val result = mutableListOf<List<Int>>()
	var removed = false
	val sortedRemain = List(n) { it }.sortedWith(compareBy({ p[it][0] }, { p[it][1] })).toMutableList()
	while (sortedRemain.size >= 3) {
		if (makeTriangleFromMaxPoints(p, sortedRemain, result))
			continue
		val (a, b) = sortedRemain.take(2)
		val v = vector(p[a], p[b])
		val c = sortedRemain.toMutableSet()
		if (!removed) {
			removed = true
			removeUnused(p, sortedRemain, c, a, v)
			if (sortedRemain.isEmpty())
				break
		}
		while (c.size / 2 > sortedRemain.size - c.size) {
			for (i in result.removeLast()) {
				insort(p, sortedRemain, i)
				if (outerProduct(v, vector(p[a], p[i])) == 0L)
				if (ccw(p[a], p[b], p[i]) == 0L)
					c.add(i)
			}
		}
		if (c.size == 3 && sortedRemain.size == 6) {
			makeTrianglesByBruteForce(p, sortedRemain, result)
			break
		}
		makeTrianglesFromMaxCollinear(p, sortedRemain, c, result)
	}
	val str = StringBuilder("${result.size}")
	for (tr in result)
		str.append("\n").append(tr.joinToString(" ") { "${it + 1}" })
	return str.toString()
}

private fun makeTriangleFromMaxPoints(p: List<List<Int>>, sortedRemain: MutableList<Int>,
									  result: MutableList<List<Int>>): Boolean {
	val (y, x) = sortedRemain.takeLast(2)
	val z = findNearestPoint(p, sortedRemain, x, y)
	if (z == -1)
		return false
	result.add(listOf(x, y, z))
	for (i in result.last())
		sortedRemain.remove(i)
	return true
}

private fun makeTrianglesFromMaxCollinear(p: List<List<Int>>, sortedRemain: MutableList<Int>,
										  c: MutableSet<Int>, result: MutableList<List<Int>>) {
	val other = mutableListOf<Int>()
	val collinear = mutableListOf<Int>()
	for (x in sortedRemain) {
		if (x in c)
			collinear.add(x)
		else
			other.add(x)
	}
	for (j in 0 until collinear.size / 2) {
		val x = collinear.removeLast()
		val y = collinear.removeLast()
		val z = findNearestPoint(p, other, x, y)
		other.remove(z)
		result.add(listOf(x, y, z))
		for (i in result.last())
			sortedRemain.remove(i)
	}
}

private fun makeTrianglesByBruteForce(p: List<List<Int>>, sortedRemain: MutableList<Int>,
									  result: MutableList<List<Int>>) {
	val i = 0
	for (j in i + 1 until sortedRemain.size) {
		for (k in j + 1 until sortedRemain.size) {
			val x = sortedRemain[i]
			val y = sortedRemain[j]
			val z = sortedRemain[k]
			if (ccw(p[x], p[y], p[z]) == 0L)
				continue
			val remain = mutableListOf<Int>()
			for (o in sortedRemain) {
				if (o != x && o != y && o != z)
					remain.add(o)
			}
			val (a, b, c) = remain
			if (ccw(p[a], p[b], p[c]) == 0L || !check(p[x], p[y], p[z], p[a], p[b], p[c]))
				continue
			for (t in listOf(listOf(x, y, z), listOf(a, b, c))) {
				result.add(t)
				for (r in t)
					sortedRemain.remove(r)
			}
			return
		}
	}
}

private fun findNearestPoint(p: List<List<Int>>, sortedRemain: MutableList<Int>, x: Int, y: Int): Int {
	var (d1, z1, v1) = Triple(Long.MAX_VALUE, -1, emptyList<Int>())
	var (d2, z2, v2) = Triple(Long.MAX_VALUE, -1, emptyList<Int>())
	val u = vector(p[y], p[x])
	for (c in sortedRemain) {
		val v = vector(p[y], p[c])
		val side = outerProduct(u, v)
		if (side == 0L)
			continue
		val d = innerProduct(v, v)
		if (side > 0) {
			if (z1 != -1 && outerProduct(v1, v) == 0L) {
				if (d < d1) {
					d1 = d
					z1 = c
					v1 = v
				}
			} else if (z1 == -1 || outerProduct(v1, v) < 0) {
				d1 = d
				z1 = c
				v1 = v
			}
		} else {
			if (z2 != -1 && outerProduct(v2, v) == 0L) {
				if (d < d2) {
					d2 = d
					z2 = c
					v2 = v
				}
			} else if (z2 == -1 || outerProduct(v2, v) > 0) {
				d2 = d
				z2 = c
				v2 = v
			}
		}
	}
	return if (z1 != -1) z1 else z2
}

private fun check(x: List<Int>, y: List<Int>, z: List<Int>, a: List<Int>, b: List<Int>, c: List<Int>): Boolean {
	if ((listOf(x, y, z).count { isStrictlyInsideTriangle(it, a, b, c) } == 1 &&
				listOf(x, y, z).count { !isInsideTriangle(it, a, b, c) } == 2) ||
		(listOf(a, b, c).count { isStrictlyInsideTriangle(it, x, y, z) } == 1 &&
				listOf(a, b, c).count { !isInsideTriangle(it, x, y, z) } == 2))
		return false
	for ((d, e) in listOf(listOf(x, y), listOf(y, z), listOf(z, x))) {
		for ((f, g) in listOf(listOf(a, b), listOf(b, c), listOf(c, a))) {
			if (cross(d, e, f, g) || (ccw(d, f, g) == 0L && ccw(e, f, g) == 0L &&
						(isStrictlyInsideSegment(d, f, g) || isStrictlyInsideSegment(e, f, g) ||
								isStrictlyInsideSegment(f, d, e) || isStrictlyInsideSegment(g, d, e))))
				return false
		}
	}
	return true
}

private fun removeUnused(p: List<List<Int>>, sortedRemain: MutableList<Int>, c: MutableSet<Int>, a: Int, v: List<Int>) {
	val cnt = p.count { outerProduct(v, vector(p[a], it)) == 0L }
	for (i in 0 until max(cnt - 2 * (p.size - cnt), 0)) {
		val x = c.last()
		sortedRemain.remove(x)
		c.remove(x)
	}
}

private fun insort(p: List<List<Int>>, sortedRemain: MutableList<Int>, x: Int) {
	val comparator = compareBy<Int>({ p[it][0] }, { p[it][1] })
	val i = sortedRemain.withIndex().find { (_, y) -> comparator.compare(y, x) > 0 }?.index ?: sortedRemain.size
	sortedRemain.add(i, x)
}

private fun vector(a: List<Int>, b: List<Int>) = listOf(a[0] - b[0], a[1] - b[1])

private fun innerProduct(a: List<Int>, b: List<Int>) = a[0].toLong() * b[0] + a[1].toLong() * b[1]

private fun outerProduct(a: List<Int>, b: List<Int>) = a[0].toLong() * b[1] - a[1].toLong() * b[0]

private fun ccw(a: List<Int>, b: List<Int>, c: List<Int>) =
	(b[0] - a[0]).toLong() * (c[1] - a[1]) - (b[1] - a[1]).toLong() * (c[0] - a[0])

private fun cross(a: List<Int>, b: List<Int>, c: List<Int>, d: List<Int>) =
	ccw(a, c, d).sign * ccw(b, c, d).sign < 0 && ccw(a, b, c).sign * ccw(a, b, d).sign < 0

// Return true if t is strictly inside a, b line segment
private fun isStrictlyInsideSegment(t: List<Int>, a: List<Int>, b: List<Int>) =
	ccw(t, a, b) == 0L && innerProduct(vector(a, t), vector(t, b)) > 0

// Return true if t is strictly inside a, b, c triangle
private fun isStrictlyInsideTriangle(t: List<Int>, a: List<Int>, b: List<Int>, c: List<Int>): Boolean {
	val d1 = ccw(t, a, b)
	val d2 = ccw(t, b, c)
	val d3 = ccw(t, c, a)
	return (d1 > 0 && d2 > 0 && d3 > 0) || (d1 < 0 && d2 < 0 && d3 < 0)
}

// Return true if t is inside a, b, c triangle
private fun isInsideTriangle(t: List<Int>, a: List<Int>, b: List<Int>, c: List<Int>): Boolean {
	val d1 = ccw(t, a, b)
	val d2 = ccw(t, b, c)
	val d3 = ccw(t, c, a)
	return (d1 >= 0 && d2 >= 0 && d3 >= 0) || (d1 <= 0 && d2 <= 0 && d3 <= 0)
}
