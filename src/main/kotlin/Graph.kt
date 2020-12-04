import java.util.PriorityQueue

data class PathNode<T>(val data: T) {

    var gScore: Double = Double.MAX_VALUE
    var fScore: Double = Double.MAX_VALUE
    var cameFrom: PathNode<T>? = null
}

data class Distance(val index: Int, val d: Double)

/**
 * A general A* implementation with classic Dijkstra algorithm as a special case for h(m) = 0.0
 * @see <a href="https://en.wikipedia.org/wiki/A*_search_algorithm">A*</a>
 *
 */
class Graph<T>(private val data: List<T>, private val distances: List<Iterable<Distance>>) {

    fun shortestPath(start: Int, goal: Int, h: (T) -> Double = { _ -> 0.0 }): List<PathNode<T>> {
        // nodes must be reinitialized for each run
        val nodes = data.map { PathNode(it) }

        val openSet = PriorityQueue<IndexedValue<PathNode<T>>>(compareBy { it.value.fScore })

        with(nodes[start]) {
            gScore = 0.0
            fScore = h(data)
            openSet += IndexedValue(start, this)
        }

        while (true) {
            val current = openSet.poll()
            if (current.index == goal) {
                break
            }

            distances[current.index].forEach {
                val tentativeGScore = current.value.gScore + it.d
                with(nodes[it.index]) {
                    if (tentativeGScore < gScore) {
                        gScore = tentativeGScore
                        fScore = gScore + h(this.data)
                        cameFrom = current.value

                        // PriorityQueue is not a set - we need to remove and add changed nodes for correct sorting and set semantics
                        openSet -= IndexedValue(it.index, this)
                        openSet += IndexedValue(it.index, this)
                    }
                }
            }
        }
        return traceback(nodes[goal])
    }

    private fun traceback(target: PathNode<T>): List<PathNode<T>> {
        var pathNode: PathNode<T>? = target
        val path = mutableListOf<PathNode<T>>()
        while (pathNode != null) {
            path += pathNode
            pathNode = pathNode.cameFrom
        }
        return path.reversed()
    }
}
