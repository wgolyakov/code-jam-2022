package r22

import kotlin.math.sqrt

fun main() {
	for (i in 1..10) {
		println(i)
		val pc = PixelatedCircle2(i + 1)
		pc.drawCircleFilled(i)
		pc.drawCircleFilledWrong(i)
		pc.print()
		println(pc.count() * 4)
		println()
	}
}

class PixelatedCircle2(private val imageSize: Int) {
	private val image = Array(imageSize) { IntArray(imageSize) }

	private fun setPixelToBlack(x: Int, y: Int, c: Int) {
		if (x >= 0 && y >= 0)
			image[x][y] = c
	}

	// round to the nearest integer, breaking ties towards zero
	private fun round(a: Double): Int {
		val n = a.toInt()
		val d = a - n
		return n + if (d >= 0)
			if (d > 0.5) 1 else 0
		else
			if (d < -0.5) -1 else 0
	}

	private fun drawCirclePerimeter(R: Int) {
		for (x in -R..R) {
			val y = round(sqrt(R.toDouble() * R.toDouble() - x.toDouble() * x.toDouble()))
			setPixelToBlack(x, y, 2)
			setPixelToBlack(x, -y, 2)
			setPixelToBlack(y, x, 2)
			setPixelToBlack(-y, x, 2)
		}
	}

	fun drawCircleFilled(R: Int) {
		for (x in -R..R)
			for (y in -R..R)
				if (round(sqrt(x.toDouble() * x.toDouble() + y.toDouble() * y.toDouble())) <= R)
					setPixelToBlack(x, y, 1)
	}

	fun drawCircleFilledWrong(R: Int) {
		for (r in 0..R)
			drawCirclePerimeter(r)
	}

	fun print() {
		for (y in 0 until imageSize) {
			for (x in 0 until imageSize) {
				val c = when (image[x][y]) {
					1 -> "()"
					2 -> "██"
					else -> "  "
				}
				print(c)
			}
			println()
		}
	}

	fun count(): Int {
		var c = 0
		for (row in image)
			for (v in row)
				if (v == 1) c++
		return c
	}
}
