###### Scala adaptation of Converters and Populators pattern

Original: https://blogs.sap.com/2019/08/29/converters-and-populators-deep-dive/

Contains two implementations of Converter:
1. [DefaultConverter](src/main/scala/converters/DefaultConverter.scala) - similar to Java, each populator returns copy of target.
2. [UnionConverter](src/main/scala/converters/UnionConverter.scala) - target is created by union of populators results using [shapeless](https://github.com/milessabin/shapeless). Each populator uses own result class.

For usage examples see tests.
