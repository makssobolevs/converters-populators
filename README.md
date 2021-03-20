## Scala adaptation of Converters and Populators pattern

Original: https://blogs.sap.com/2019/08/29/converters-and-populators-deep-dive/

Contains two implementations of Converter:
1. [DefaultConverter](src/main/scala/converters/DefaultConverter.scala) is similar to Java, each populator returns copy of target.
2. [UnionConverter](src/main/scala/converters/UnionConverter.scala) where target is created by union of populator results using [shapeless](https://github.com/milessabin/shapeless). Each populator uses own result type.

For usage examples see tests.
