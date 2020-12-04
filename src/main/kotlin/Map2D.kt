import java.io.File
import kotlin.math.sqrt

data class Point(val x: Int, val y: Int)

private val blockChars = arrayOf('@', 'O', 'T', 'W')
private val diagonalLength = sqrt(2.0)
private fun square(x: Int) = x * x.toDouble()

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
        blocks = points.filter { mapRows[it.y][it.x] in blockChars }.toSet()
    }

    val traversablePoints get() = points.filter { !isBlock(it) }

    val maxDistance get() = mapDistance(Point(0, 0), Point(xRange.last, yRange.last))

    fun distance(p1: Point, p2: Point) = if (p1.x == p2.x || p1.y == p2.y) 1.0 else diagonalLength

    fun mapDistance(p1: Point, p2: Point): Double = sqrt(square(p1.x - p2.x) + square(p1.y - p2.y))

    fun neighbors(p: Point): List<Point> = (-1..1).filter { dy -> p.y + dy in yRange }.flatMap { dy ->
        (-1..1).filter { dx -> p.x + dx in xRange }.map { dx ->
            Point(p.x + dx, p.y + dy)
        }
    }.filter { np -> !isBlock(np) && np != p }

    private fun isBlock(p: Point) = blocks.contains(p)

    private fun parseFile(fileName: String) = File(fileName).bufferedReader().use { reader ->
        reader.readLine()
        val heightLine = reader.readLine()
        val height = heightLine.split(" ").last().toInt()
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
