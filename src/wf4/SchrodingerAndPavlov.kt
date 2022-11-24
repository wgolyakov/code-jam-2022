package wf4

fun main() {
	val t = readln().toInt()
	for (case in 1..t)
		println("Case #$case: ${schrodingerAndPavlov()}")
}

private const val MOD = 1000000007
private val PROB = mapOf('.' to 0, '?' to invMod(2), 'C' to 1)

private fun schrodingerAndPavlov(): Int {
	val n = readln().toInt()
	val s = readln()
	val b = readln().split(' ').map { it.toInt() - 1 }
	val cycleStart = findMinCycleStartInTheLastComponent(b)
	var result = 0
	for (probU in 0..1) {
		for (probV in 0..1) {
			val prob = s.map { PROB[it]!! }.toMutableList()
			var weight = 0
			for (i in 0 until n) {
				if (i == cycleStart) {
					val wu = if (probU == 1) prob[i] else subMod(1, prob[i])
					val wv = if (probV == 1) prob[b[i]] else subMod(1, prob[b[i]])
					weight = mulMod(wu, wv)
					prob[i] = probU
					prob[b[i]] = probV
				}
				val prob1 = mulMod(prob[i], prob[b[i]])
				val prob2 = subMod(addMod(prob[i], prob[b[i]]), mulMod(prob[i], prob[b[i]]))
				prob[i] = prob1
				prob[b[i]] = prob2
			}
			result = addMod(result, mulMod(weight, prob[n - 1]))
		}
	}
	return mulMod(result, powMod(2, s.count { it == '?' }))
}

private fun findMinCycleStartInTheLastComponent(B: List<Int>): Int {
	val lookup = IntArray(B.size) { -1 }
	var curr = B.size - 1
	var cnt = 0
	while (lookup[curr] == -1) {
		lookup[curr] = cnt++
		curr = B[curr]
	}
	return lookup.withIndex().filter { (_, x) -> x >= lookup[curr] }.minOf { it.index }
}

private fun addMod(x: Int, y: Int) = ((x.toLong() + y) % MOD).toInt()

private fun subMod(x: Int, y: Int) = if (x < y) x - y + MOD else x - y

private fun mulMod(x: Int, y: Int) = (x.toLong() * y % MOD).toInt()

private fun invMod(x: Int) = powMod(x, MOD - 2)

private fun powMod(x: Int, y: Int): Int {
	var a = (x % MOD).toLong()
	var n = y
	var r = 1L
	while (n != 0) {
		r = r * (if (n % 2 != 0) a else 1L) % MOD
		a = a * a % MOD
		n = n shr 1
	}
	return r.toInt()
}
