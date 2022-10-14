package r1c3

// Rational number with module
class Rational(n: Int, d: Int, mod: Boolean = true) : Comparable<Rational> {
	constructor(n: Long, d: Long): this((n % m).toInt(), (d % m).toInt(), false)
	val n = if (mod) n % m else n
	val d = if (mod) d % m else d

	companion object {
		private const val m = 1000000007
		val ZERO = Rational(0, 1, false)
		val ONE = Rational(1, 1, false)
	}

	operator fun plus(r: Rational) = Rational(n.toLong() * r.d + r.n.toLong() * d, r.d.toLong() * d)

	operator fun minus(r: Rational) = Rational(n.toLong() * r.d - r.n.toLong() * d, r.d.toLong() * d)

	operator fun times(r: Rational) = Rational(n.toLong() * r.n, r.d.toLong() * d)

	operator fun div(r: Rational) = Rational(n.toLong() * r.d, d.toLong() * r.n)

	override fun compareTo(other: Rational) = (n.toLong() * other.d).compareTo(other.n.toLong() * d)

	override fun toString() = if (d == 1 || n % d == 0) (n / d).toString() else "$n/$d"
}
