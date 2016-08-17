# StringAlignment
字符串比对，采用smithwaterman方法重复比对

对两个字符串进行模糊匹配（approximate string comparison），由于没有找到方法用blast来进行通用字符串的比对，为了达到类似的效果而诞生了这个小项目。

采用smithwaterman比对算法进行重复比对，每次把比对得到的局部最优结果抠出来，再进行下一轮比对。

比对得分参数：match3分;mismatch,ins,del,gapextend都是-1分；这些还有优化的余地。

通过这个小项目理解了一些biojava(1.7)中smithwaterman比对需要的一些类的配置，可以自定义Symbol，SymbolList，Alphabet，CharacterTokenization,SubstitutionMatrix

[superlukia@163.com](mailto:superlukia@163.com)
