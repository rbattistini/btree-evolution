package it.irs.simulation.blackboard

import io.github.oshai.kotlinlogging.KotlinLogging

/**
 * Define a generic blackboard that can handle data of any type.
 *
 * @property board The map storing entries, where each entry is a pair of key and value.
 */
data class Blackboard(
  val board: Map<String, Any?>,
) {
  private val logger = KotlinLogging.logger {}

  fun write(entry: Pair<String, Any?>): Blackboard {
    logger.debug { "Write to blackboard: [${entry.first}] -> ${entry.second}" }
    return copy(board = board.plus(entry))
  }

  fun plus(bl: Blackboard): Blackboard = copy(board = board + bl.board)

  fun read(key: String): Any? {
    val value = board[key]
    if (value != null) {
      logger.debug { "Read from blackboard: [$key] -> $value" }
    } else {
      logger.debug { "No entry found for key: $key" }
    }
    return value
  }

  fun erase(key: String): Blackboard = copy(board = board - key)

  fun containsKey(key: String): Boolean = board.containsKey(key)

  fun dumpFiltered(filterPredicate: (Map.Entry<String, Any?>) -> Boolean = { true }) {
    if (board.isEmpty()) {
      logger.debug { "The blackboard is empty." }
    } else {
      logger.debug { "Current Blackboard Entries:" }
      board.filter(filterPredicate).forEach { (key, value) ->
        println("[$key] -> $value")
      }
    }
  }

  fun clear(): Blackboard = copy(board = emptyMap())

  fun size(): Int = board.size

  companion object {
    fun create() = Blackboard(emptyMap())
  }
}
