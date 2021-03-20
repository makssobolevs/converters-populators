package com.github.makssobolevs
package converters

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

/**
 * @author makssobolevs
 */
class DefaultConverterSpec extends AnyWordSpec with Matchers {
  case class Cat(name: String, black: Boolean)
  case class Dog(nickname: String, color: String)

  object NicknamePopulator extends Populator[Cat, Dog] {
    override def populate(source: Cat, target: Dog) = {
      target.copy(nickname = source.name)
    }
  }

  object ColorPopulator extends Populator[Cat, Dog] {
    override def populate(source: Cat, target: Dog) = {
      val color = if (source.black) "black" else ""
      target.copy(color = color)
    }
  }

  val testConverter = Converter(
    Dog("", ""),
    Seq(
      NicknamePopulator,
      ColorPopulator
    )
  )

  "DefaultConverter" should {
    "create target" in {
      val source = Cat("Kitty", black = true)
      val target = testConverter.convert(source)
      target shouldBe Dog("Kitty", "black")
    }
  }
}
