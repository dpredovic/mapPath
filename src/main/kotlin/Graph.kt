import java.util.PriorityQueue

data class PathNode<T>(val data: T) : Iterable<PathNode<T>> {

    var gScore: Double = Double.MAX_VALUE
    var fScore: Double = Double.MAX_VALUE
    var cameFrom: PathNode<T>? = null

    /**
     * Iterate from current node to start
     */
    override fun iterator() = object : Iterator<PathNode<T>> {
        var next: PathNode<T>? = this@PathNode

        override fun hasNext() = next != null
        override fun next() = next!!.also { next = it.cameFrom }
    }
}

data class Distance(val index: Int, val d: Double)

/**
 * A general A* implementation with classic Dijkstra algorithm as a special case for h(n) = 0.0
 * @see <a href="https://en.wikipedia.org/wiki/A*_search_algorithm">A*</a>
 *
 */
class Graph<T>(private val data: List<T>, private val distances: List<Iterable<Distance>>) {

    fun shortestPath(start: Int, goal: Int, h: (T) -> Double = { _ -> 0.0 }): List<PathNode<T>> {
        // nodes must be reinitialized for each run
        val nodes = data.map { PathNode(it) }

        val openSet = PriorityQueue<IndexedValue<PathNode<T>>>(compareBy { it.value.fScore })

        nodes[start].apply {
            gScore = 0.0
            fScore = h(data)
            openSet += IndexedValue(start, this)
        }

        while (true) {
            val current = openSet.poll()
            if (current.index == goal) break

            distances[current.index].forEach {
                val tentativeGScore = current.value.gScore + it.d
                nodes[it.index].apply {
                    if (tentativeGScore < gScore) {
                        gScore = tentativeGScore
                        fScore = gScore + h(data)
                        cameFrom = current.value

                        // PriorityQueue is not a set - we need to remove and add changed nodes for correct sorting and set semantics
                        openSet -= IndexedValue(it.index, this)
                        openSet += IndexedValue(it.index, this)
                    }
                }
            }
        }
        return nodes[goal].reversed()
    }
}
