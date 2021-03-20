package com.github.makssobolevs
package converters

import shapeless._
import shapeless.ops.hlist.{Align, FlatMapper}

import scala.annotation.implicitNotFound

/**
 * Provides composable type-safe way of creating case classes from source object.
 * Unions non overlapping results provided by populators into target instance.
 *
 * Converter variable definition must have type annotation to help infer types, e.g.
 * {{{
 * val converter: Converter[Dog, Cat] = TypedConverter(populator1, populator2, ...)
 * }}}
 *
 * Field names and types of the [[UnionPopulator.Result]] must match [[T]], otherwise compilation will fail with
 * 'could not find implicit value for parameter align: ...'
 * Order of the populators and [[UnionPopulator.Result]] fields is not important.
 *
 * For examples see test.
 *
 * @param populators populators hlist
 * @param constraint populators type constraint
 * @param genTarget generic target
 * @param evidence custom type class evidence
 * @param flatMapper flat mapper
 * @tparam S source type
 * @tparam T target type
 * @tparam HP populators hlist type
 * @tparam HI1 intermediate hlist type
 * @tparam HI2 intermediate hlist type
 * @tparam HT target hlist type
 * @author makssobolevs
 */
class UnionConverter[S, T, HP <: HList, HI1 <: HList, HI2 <: HList, HT <: HList](populators: HP)(
  implicit constraint: LUBConstraint[HP, UnionPopulator[S, T]],
  genTarget: LabelledGeneric.Aux[T, HT],
  evidence: ToPopulatorResult.Aux[S, HP, HI1],
  flatMapper: FlatMapper.Aux[labelledExpand.type, HI1, HI2],
  align: Align[HI2, HT]
) extends Converter[S, T] {

  /**
   * @param l populators HList
   * @tparam L populators HList type
   */
  implicit class ToPopulatorResultMap[L <: HList](l: L) {
    def mapToPopulatorResult[O <: HList](source: S)(implicit ev: ToPopulatorResult.Aux[S, L, O]): O = ev.map(source, l)
  }
  def convert(source: S): T = {
    // execute populate:
    val hPopulatorsResult: HI1 =
      populators.mapToPopulatorResult(source) // Populator1Result :: Populators2Result :: HNil

    // flatten populators result:
    val hTarget: HI2 = hPopulatorsResult.flatMap(labelledExpand)

    // align to target Hlist:
    val aligned: HT = hTarget.align

    // create target instance:
    genTarget.from(aligned)
  }
}

object UnionConverter {

  /**
   * Provides abstraction over arity for [[UnionConverter]].
   *
   * @return converter instance
   */
  def apply[S, T, P <: Product, HP <: HList, HI1 <: HList, HI2 <: HList, HT <: HList](product: P)(
    implicit genProduct: LabelledGeneric.Aux[P, HP],
    @implicitNotFound(
      "could not find implicit value for parameter constraint: shapeless.LUBConstraint[${HP},UnionPopulator[${S},${T}]], " +
        "check if your converter definition contains explicit type annotation (val converter: Converter[${S}, ${T}] = ...) " +
        "and all populators are subtypes of UnionPopulator[${S}, ${T}]"
    )
    constraint: LUBConstraint[HP, UnionPopulator[S, T]],
    genTarget: LabelledGeneric.Aux[T, HT],
    evidence: ToPopulatorResult.Aux[S, HP, HI1],
    flatMapper: FlatMapper.Aux[labelledExpand.type, HI1, HI2],
    @implicitNotFound(
      "could not find implicit value for parameter align: shapeless.ops.hlist.Align[${HI2},${HT}] " +
        "(No implicit view available from ${HI2} => ${HT}.), " +
        "check UnionPopulator result types and field names"
    )
    align: Align[HI2, HT]
  ): Converter[S, T] = {
    val hPopulators: HP = genProduct.to(product) // Populator1 :: Populator2 :: HNil
    new UnionConverter(hPopulators)
  }
}

/**
 * Type class computing [[UnionPopulator]] result.
 *
 * @tparam S source type
 * @tparam HP populators Hlist type
 * @author makssobolevs
 */
trait ToPopulatorResult[S, HP <: HList] {
  type Out <: HList

  def map(source: S, hPopulators: HP): Out
}

object ToPopulatorResult {

  type Aux[S, HP <: HList, O <: HList] = ToPopulatorResult[S, HP] { type Out = O }

  /**
   * @tparam S source type
   * @return type class instance
   */
  implicit def hnilToPopulatorResult[S]: Aux[S, HNil, HNil] = new ToPopulatorResult[S, HNil] {
    type Out = HNil
    override def map(source: S, hPopulators: HNil): Out = hPopulators
  }

  /**
   * @param ev [[ToPopulatorResult]] evidence
   * @param populatorEvidence populator evidence
   * @tparam S source type
   * @tparam T target type
   * @tparam P populator type
   * @tparam PR populator result type
   * @tparam HP populator result HList type
   * @tparam O output HList type
   * @return type class instance
   */
  implicit def hconsToPopulatorResult[S, T, P, PR, HP <: HList, O <: HList](
    implicit ev: Aux[S, HP, O],
    populatorEvidence: P <:< UnionPopulator.Aux[S, T, PR]
  ) =
    new ToPopulatorResult[S, P :: HP] {
      type Out = PR :: O

      override def map(source: S, hPopulators: P :: HP) = {
        val head :: tail = hPopulators
        head.populate(source) :: ev.map(source, tail)
      }
    }
}

/**
 * Higher ranked function which expands any case class into [[HList]].
 */
object labelledExpand extends Poly1 {
  implicit def default[T, HT <: HList](implicit gen: LabelledGeneric.Aux[T, HT]): Case.Aux[T, HT] =
    at[T](value => gen.to(value))
}
