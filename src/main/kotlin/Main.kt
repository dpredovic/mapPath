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
        ProgressBar.wrap(
            scenarios.parallelStream(),
            ProgressBarBuilder().setTaskName(name).showSpeed().setConsumer(ConsoleProgressBarConsumer(System.out, 120))
        ).forEach { solve(it) }
        solverCache.clear()
    }

    private fun solve(scenario: Scenario) {
        val solver = solverCache[rootPath + scenario.filename]
        val path = solver.solve(scenario.start, scenario.end)

        assert(path.last().gScore <= scenario.score + 1e-6)
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
