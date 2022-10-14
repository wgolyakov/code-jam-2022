package qr4

import kotlin.math.max

fun main() {
	class Module(val f: Int, var fMax: Int = 0, var fTree: Long = 0,
				 var parent: Module? = null, val children: MutableList<Module> = mutableListOf()) {
		private var readyChildCount = 0
		fun isInitiator() = children.isEmpty()
		fun incReadyChild() { readyChildCount++ }
		fun isAllChildrenReady() = children.size == readyChildCount
		override fun toString() = "$f"
	}

	val t = readln().toInt()
	for (case in 1..t) {
		val n = readln().toInt()
		val f = readln().split(' ').map { it.toInt() }
		val p = readln().split(' ').map { it.toInt() }

		val modules = Array(n) { Module(f[it]) }
		val roots = mutableListOf<Module>()
		for ((i, pi) in p.withIndex()) {
			val module = modules[i]
			if (pi == 0) {
				roots.add(module)
			} else {
				val parent = modules[pi - 1]
				module.parent = parent
				parent.children.add(module)
			}
		}

		for (mod in modules) {
			if (!mod.isInitiator())
				continue
			var m: Module? = mod
			while (m != null) {
				if (m.children.isEmpty()) {
					m.fMax = m.f
				} else if (m.children.size == 1) {
					val child = m.children.first()
					m.fMax = max(child.fMax, m.f)
					m.fTree = child.fTree
				} else {
					if (m.isAllChildrenReady()) {
						val minChild = m.children.minByOrNull { it.fMax } ?: error("Empty children")
						m.fMax = max(minChild.fMax, m.f)
						for (c in m.children)
							m.fTree += if (c === minChild) c.fTree else c.fTree + c.fMax
					} else {
						break
					}
				}
				m = m.parent
				m?.incReadyChild()
			}
		}
		val result = roots.sumOf { it.fTree + it.fMax }
		println("Case #$case: $result")
	}
}
