package it.irs.simulation.btree.transformation.randomization

import it.irs.simulation.SimulationUtils.DEFAULT_SEED
import it.irs.simulation.btree.transformation.TransformationTool
import kotlin.random.Random

interface RandomTool : TransformationTool {
  val random: Random
    get() = Random(DEFAULT_SEED)
}
