package com.github.makssobolevs
package converters

/**
 * @author makssobolevs
 */
trait Populator[S, T] {

  /**
   * @param source object
   * @param target intermediate result
   * @return copy of [[target]] with more data
   */
  def populate(source: S, target: T): T

}
