package r1a1

fun main() {
	val t = readln().toInt()
	for (case in 1..t) {
		val s = readln()
		val res = StringBuilder()
		var i = 0
		while (i < s.length) {
			val c = s[i]
			var count = 1
			while (i + count < s.length && s[i + count] == c) count++
			if (i + count >= s.length) {
				res.append(c.toString().repeat(count))
				break
			}
			val n = s[i + count]
			if (c < n)
				res.append(c.toString().repeat(count * 2))
			else
				res.append(c.toString().repeat(count))
			i += count
		}
		println("Case #$case: $res")
	}
}
