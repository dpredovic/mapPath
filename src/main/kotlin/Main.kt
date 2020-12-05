import me.tongfei.progressbar.ConsoleProgressBarConsumer
import me.tongfei.progressbar.ProgressBar
import me.tongfei.progressbar.ProgressBarBuilder
import java.io.File

fun main() {
    TestScenarios.runAllScenarioFiles()
}

private object TestScenarios {

    private const val rootPath = "src/main/maps/"
    private val solverCache = Cache<String, Solver> { Solver(Map2D(it)) }

    fun runAllScenarioFiles() {
        File(rootPath).listFiles { f -> f.extension == "scen" }!!.forEach { solve(load(it), it.name) }
    }

    private fun solve(scenarios: Collection<Scenario>, name: String) {
        val pbb =
            ProgressBarBuilder().setTaskName(name).showSpeed().setConsumer(ConsoleProgressBarConsumer(System.out, 120))
                .setUpdateIntervalMillis(100)
        ProgressBar.wrap(scenarios.parallelStream(), pbb).forEach { solve(it) }
        solverCache.clear()
    }

    private fun solve(scenario: Scenario) {
        val solver = solverCache[rootPath + scenario.filename]
        val path = solver.solve(scenario.start, scenario.end)

        val score = path.last().gScore
        val diff = scenario.score - score

        when {
            // if our solution is worse, ignore rounding errors
            diff < -1e-6 -> throw Exception("Solution is worse! ${scenario.score} < $score")
            // if our solution is better, improvements should not be too big
            diff / scenario.score > 0.16 -> throw Exception("Solution seems too good to be true! ${scenario.score} > $score")
        }
    }

    private fun load(f: File): Collection<Scenario> = f.bufferedReader().use { reader ->
        reader.readLine()
        reader.lineSequence().map {
            val parts = it.split("\t")
            Scenario(
                parts[1],
                Point(parts[4].toInt(), parts[5].toInt()),
                Point(parts[6].toInt(), parts[7].toInt()),
                parts[8].toDouble()
            )
        }.toList()
    }

    private data class Scenario(val filename: String, val start: Point, val end: Point, val score: Double)

    private class Cache<K, V>(private val creator: (K) -> V) {

        private val map = mutableMapOf<K, V>()

        operator fun get(k: K) = map[k] ?: creator(k).also { map[k] = it }
        fun clear() = map.clear()
    }
}
