#!/bin/sh

DROP_CACHES=/proc/sys/vm/drop_caches

BENCHMARK_JAR=$(find target -name '*-jar-with-dependencies.jar' | head -n1)

# Sync disks
sudo sync

# Drop page cache
echo '1' | sudo tee --append ${DROP_CACHES} > /dev/null

# Run benchmark
java -jar ${BENCHMARK_JAR}