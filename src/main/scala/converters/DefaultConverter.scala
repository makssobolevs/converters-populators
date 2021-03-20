package com.github.makssobolevs
package converters

import scala.annotation.tailrec

/**
 * Uses populators to make conversion.
 * "Chain-of-responsibility"-like pattern applied.
 */
final class DefaultConverter[S, T](protected val emptyTarget: T, protected val populators: Seq[Populator[S, T]])
    extends Converter[S, T] {
  require(populators.nonEmpty, "No populators provided")

  /**
   * @inheritdoc
   */
  override def convert(source: S): T = {
    convertRecursive(source, emptyTarget, populators)
  }

  @tailrec
  private def convertRecursive(source: S, target: T, populatorsLeft: Seq[Populator[S, T]]): T = populatorsLeft match {
    case Nil => target
    case head :: tail => convertRecursive(source, head.populate(source, target), tail)
  }
}
