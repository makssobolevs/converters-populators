package com.github.makssobolevs
package converters

/**
 * Uses populators to make conversion ("chain-of-responsibility"-like pattern applied).
 */
class DefaultConverter[S, T](protected val emptyTarget: T, protected val populators: Seq[Populator[S, T]])
    extends Converter[S, T] {
  require(populators.nonEmpty, "No populators provided")

  /**
   * @inheritdoc
   */
  override def convert(source: S): T = {
    var target = emptyTarget

    for (populator <- populators) {
      target = populator.populate(source, target)
    }

    target
  }
}
