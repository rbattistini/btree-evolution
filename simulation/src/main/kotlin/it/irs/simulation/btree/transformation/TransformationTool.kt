package it.irs.simulation.btree.transformation

interface TransformationTool {
  val name: String
    get() = this.javaClass.simpleName
}
