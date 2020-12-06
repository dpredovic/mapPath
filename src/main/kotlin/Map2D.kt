import java.io.File
import kotlin.math.absoluteValue
import kotlin.math.sqrt

data class Point(val x: Int, val y: Int)

private val BLOCK_CHARS = arrayOf('@', 'O', 'T', 'W')
private val DIAGONAL_DISTANCE = sqrt(2.0)

/**
 * A 2D map representation.
 */
class Map2D(fileName: String) {

    private val yRange: IntRange
    private val xRange: IntRange
    private val blocks: Set<Point>
    private val mapRows: List<CharArray>
    private val points: List<Point>

    init {
        mapRows = parseFile(fileName)
        yRange = mapRows.indices
        xRange = mapRows[0].indices
        points = yRange.flatMap { y -> xRange.map { x -> Point(x, y) } }
        blocks = points.filter { mapRows[it.y][it.x] in BLOCK_CHARS }.toSet()
    }

    val traversablePoints get() = points.filter { !isBlock(it) }

    fun distance(p1: Point, p2: Point) = if (p1.x == p2.x || p1.y == p2.y) 1.0 else DIAGONAL_DISTANCE

    fun euclideanDistance(p1: Point, p2: Point) = sqrt(square(p1.x - p2.x) + square(p1.y - p2.y))
    private fun square(x: Int) = x * x.toDouble()

    @Suppress("unused")
    fun manhattanDistance(p1: Point, p2: Point) = ((p1.x - p2.x).absoluteValue + (p1.y - p2.y).absoluteValue).toDouble()

    fun neighbors(p: Point): List<Point> = (-1..1).flatMap { dy -> (-1..1).map { dx -> Point(p.x + dx, p.y + dy) } }
        .filter { it != p && isInRange(it) && !isBlock(it) }

    private fun isBlock(p: Point) = p in blocks
    private fun isInRange(p: Point) = p.x in xRange && p.y in yRange

    private fun parseFile(fileName: String) = File(fileName).bufferedReader().use { reader ->
        reader.readLine()
        val height = reader.readLine().split(" ").last().toInt()
        reader.readLine()
        reader.readLine()

        (1..height).map { reader.readLine().toCharArray() }
    }

    @Suppress("unused")
    fun plot(start: Point, end: Point, path: List<Point>, pc: Char = 'X', sc: Char = 'S', ec: Char = 'E'): String {
        val clone = mapRows.map { it.clone() }
        path.forEach { clone[it.y][it.x] = pc }
        clone[start.y][start.x] = sc
        clone[end.y][end.x] = ec
        return clone.joinToString(separator = "\n") { it.joinToString(separator = "") }
    }
}
