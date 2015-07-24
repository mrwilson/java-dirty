## Hardware
8 cores
72GB of RAM
1TB SSD

## Reads - Simple Object - Multiple Threads

Threads: 40
Warmup Runs: 20
Measurement Runs: 50

```
Benchmark                  Mode   Cnt     Score    Error   Units
read2mObjectsFromStore    thrpt    50     0.017 ±  0.001  ops/ms
read2mObjectsFromStore     avgt    50  2180.266 ± 57.820   ms/op
read2mObjectsFromStore   sample  2000  1972.672 ± 19.912   ms/op
read2mObjectsFromStore       ss    50  1883.281 ± 33.791   ms/op
```

## Reads - Simple Object - Single Thread

Threads: 1
Warmup Runs: 20
Measurement Runs: 50

```
Benchmark                 Mode   Cnt     Score    Error   Units
read2mObjectsFromStore   thrpt    50     0.005 ±  0.001  ops/ms
read2mObjectsFromStore    avgt    50   184.354 ±  4.210   ms/op
read2mObjectsFromStore  sample   300   182.303 ±  1.102   ms/op
read2mObjectsFromStore      ss    50   219.567 ±  5.345   ms/op
```

## Reads - Complex Object - Single Thread

Threads: 1
Warmup Runs: 20
Measurement Runs: 50

```
Benchmark                 Mode  Cnt     Score    Error   Units
read2mObjectsFromStore   thrpt   50     0.001 ±  0.001  ops/ms
read2mObjectsFromStore    avgt   50  1527.901 ± 19.512   ms/op
read2mObjectsFromStore  sample   50  1555.542 ± 47.455   ms/op
read2mObjectsFromStore      ss   50  1539.636 ± 21.405   ms/op
```