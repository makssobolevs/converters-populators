package com.github.makssobolevs
package converters

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

/**
 * @author makssobolevs
 */
class UnionConverterSpec extends AnyWordSpec with Matchers {
  case class Source(a: String, b: String)
  case class Target(x: Int, y: Int, z: String)

  case class Result1(x: Int, z: String)
  object Populator1 extends UnionPopulator[Source, Target] {
    override type Result = Result1

    def populate(source: Source): Result1 = {
      val x = source.a.toInt
      val z = (x + 1).toString
      Result1(x, z)
    }
  }

  case class Result2(y: Int)
  object Populator2 extends UnionPopulator[Source, Target] {
    override type Result = Result2

    def populate(source: Source): Result2 = {
      val y = source.b.toInt
      Result2(y)
    }

  }

  val SourceValue = Source("123", "4565")
  val TargetValue = Target(123, 4565, "124")

  "UnionConverter" should {
    "convert to target" in {
      val testConverter: Converter[Source, Target] = UnionConverter(Populator1, Populator2)

      val result = testConverter.convert(SourceValue)
      result shouldBe TargetValue
    }

    "not depend on populators order" in {
      val testConverter: Converter[Source, Target] = UnionConverter(Populator2, Populator1)

      val result = testConverter.convert(SourceValue)
      result shouldBe TargetValue
    }
  }
}
