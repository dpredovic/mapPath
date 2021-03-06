/**
 * An adapter class between [Map2D] and [Graph]. Can be cached between runs.
 */
class Solver(private val map2D: Map2D) {

    private val graph: Graph<Point>
    private val indexMap = map2D.traversablePoints.mapIndexed { i, n -> n to i }.toMap()

    init {
        val distances = indexMap.map { e ->
            map2D.neighbors(e.key).map { Distance(indexMap.getValue(it), map2D.distance(e.key, it)) }
        }
        graph = Graph(map2D.traversablePoints, distances)
    }

    fun solve(start: Point, end: Point): List<PathNode<Point>> {
        val h: (Point) -> Double = { map2D.euclideanDistance(it, end) }
        return graph.shortestPath(indexMap.getValue(start), indexMap.getValue(end), h)
    }
}
