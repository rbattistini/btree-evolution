package it.irs.lab

import it.irs.lab.BTreesToCompare.handcraftedBTree1
import it.irs.lab.BTreesToCompare.handcraftedBTree2
import it.irs.lab.BTreesToCompare.randomBTree
import it.irs.lab.ExperimentRunnerUtils.fitnessComputer
import it.irs.lab.ExperimentRunnerUtils.loadConfig
import it.irs.lab.env.GridWorld
import it.irs.lab.experiment.ExperimentLogger.Companion.roundOffDecimal
import it.irs.lab.experiment.config.ExperimentConfig
import it.irs.simulation.btree.BehaviorTree
import java.io.File
import java.io.PrintWriter
import kotlin.collections.forEach

fun Double.format(scale: Int) = "%.${scale}f".format(this)

fun Map<String, Double>.pprint() = this.entries.joinToString { "\n\t${it.key}: ${it.value}" }

fun experiment(
  cfg: ExperimentConfig,
  writer: PrintWriter,
  btrees: Map<String, BehaviorTree<GridWorld>>,
) {
  val ff = fitnessComputer(cfg)
  btrees.forEach { btree ->
    val res = ff.evalSimNTimes(btree.value)
    val fitness = res.values.sum()
    writer.println(
      "${btree.key}," +
        "${cfg.gridDimensions}," +
        "${cfg.gridObstacles}," +
        "${cfg.maxSimSteps}," +
        "${roundOffDecimal(res["treeComplexityPenalty"] ?: 0.0)}," +
        "${roundOffDecimal(res["backtrackingPenalty"] ?: 0.0)}," +
        "${roundOffDecimal(res["collisionPenalty"] ?: 0.0)}," +
        "${roundOffDecimal(res["phototaxisReward"] ?: 0.0)}," +
        "${roundOffDecimal(fitness)}",
    )
    println("Handcrafted 1 Evaluation Result: ${res.pprint()}\n")
    println("Random Avg. Fitness: \t\t${fitness.format(2)}\n")
  }
}

fun main(args: Array<String>) {
  val expCfg = loadConfig(args)
  val gridDimensions = listOf(5, 7, 9)
  val gridObstacles = listOf(5, 7, 9)
  val simSteps = listOf(50, 60, 80, 100)
  val btrees =
    mapOf(
      "Handcrafted1" to handcraftedBTree1,
      "Handcrafted2" to handcraftedBTree2,
      "Random" to randomBTree,
    )

  val resultsPath =
    if (args.size < 2) {
      "./notebook/simulation_results.csv"
    } else {
      args[1]
    }

  File(resultsPath).printWriter().use { writer ->
    writer.println(
      "btree,gridDimensions,gridObstacles,maxSimSteps,treeComplexityPenalty,backtrackingPenalty,collisionPenalty,phototaxisReward,fitness",
    )

    for (gridDimension in gridDimensions) {
      for (gridObstacle in gridObstacles) {
        for (simStep in simSteps) {
          val currentConfig =
            expCfg.copy(
              gridDimensions = gridDimension,
              gridObstacles = gridObstacle,
              maxSimSteps = simStep,
            )
          println("Running simulation with config: $currentConfig")
          println("-------------------------------------------------")
          experiment(currentConfig, writer, btrees)
          println("-------------------------------------------------")
        }
      }
    }
  }
}
