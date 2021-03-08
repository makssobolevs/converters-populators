package com.github.makssobolevs.converters

/**
 * @author makssobolevs
 */
trait Converter[S, T] {

  /**
   * @param source source
   * @return target target
   */
  def convert(source: S): T
}

object Converter {
  def apply[S, T](emptyTarget: T, populators: Seq[Populator[S, T]]): Converter[S, T] =
    new DefaultConverter(emptyTarget, populators)
}
