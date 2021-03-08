package com.github.makssobolevs.converters

/**
 * @author makssobolevs
 * @tparam S source type
 * @tparam T target type
 */
trait UnionPopulator[S, T] {

  /**
   * Result type.
   */
  type Result

  /**
   * @param source source object
   * @return result
   */
  def populate(source: S): Result
}

object UnionPopulator {
  type Aux[S, T, Result0] = UnionPopulator[S, T] { type Result = Result0 }
}
