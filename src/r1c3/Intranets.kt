package r1c3

fun main() {
	Intranets.init()
	val t = readln().toInt()
	for (case in 1..t) {
		val (m, k) = readln().split(' ').map { it.toInt() }
		val result = Intranets(m , k).solve()
		println("Case #$case: $result")
	}
}

class Intranets(private val m: Int, private val k: Int) {
	companion object {
		private const val MOD = 1000000007
		private const val M_MAX = 500000
		private val fact = LongArray(M_MAX + 1)
		private val factR = LongArray(M_MAX + 1)

		fun init() {
			fact[0] = 1
			factR[0] = 1
			for (i in 1..M_MAX) {
				fact[i] = fact[i - 1] * i % MOD
				factR[i] = factR[i - 1] * invMod(i) % MOD
			}
		}

		private fun comb(n: Int, c: Int): Int {
			if (c < 0 || c > n) return 0
			return (factR[c] * fact[n] % MOD * factR[n - c] % MOD).toInt()
		}

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
	}

	fun solve(): Int {
		var f = 0L
		var d = 1L
		for (i in 2..m step 2) {
			d = d * invMod(comb(m, 2) - comb(m - i, 2)) % MOD
			if (i >= 2 * k) {
				val g = fact[m] * factR[m - i] % MOD * invMod(powMod(2, i / 2)) % MOD * d % MOD
				var a = g * comb(i / 2, k) % MOD
				if ((i / 2 - k) % 2 != 0) a = MOD - a
				f += a
			}
		}
		return (f % MOD).toInt()
	}
}
