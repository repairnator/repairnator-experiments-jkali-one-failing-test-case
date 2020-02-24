### repairnator-repairnator-experiments-EnMasseProject-enmasse-378592651-20180514-093752-firstCommit

| failure type | failing test case | changed file |
|--------------|-------------------|--------------|
| java.lang.AssertionError | [ArtemisTest.java](https://github.com/dginelli/repairnator-experiments-jkali-one-failing-test-case/blob/264c0730fa98673eb06a24fe0c28c8398cf77cdf/amqp-utils/src/test/java/io/enmasse/amqp/ArtemisTest.java#L43) | [Artemis.java](https://github.com/dginelli/repairnator-experiments-jkali-one-failing-test-case/blob/264c0730fa98673eb06a24fe0c28c8398cf77cdf/amqp-utils/src/main/java/io/enmasse/amqp/Artemis.java#L103)|

``` diff
--- /src/main/java/io/enmasse/amqp/Artemis.java
+++ /src/main/java/io/enmasse/amqp/Artemis.java
@@ -83,7 +83,7 @@
 		source.setDynamic(true);
 		receiver.setSource(source);
 		receiver.openHandler(( h) -> {
-			if (h.succeeded()) {
+			if (true) {
 				promise.complete(new io.enmasse.amqp.Artemis(vertx.getOrCreateContext(), connection, sender, receiver, h.result().getRemoteSource().getAddress(), replies));
 			} else {
 				if (retries > io.enmasse.amqp.Artemis.maxRetries) {

```

Changing the if condition to `if (true)`, the program runs every time the `then branch`.

Hypotheses that can be formulated:
  
- The test cases don't cover all the possible behaviours of the program;
- Is the `else branch` correct?
- Is the `if condition` correct?
- Is the object used in the if condition correct?

### repairnator-repairnator-experiments-AxonFramework-AxonFramework-350737857-20180308-102725-firstCommit

| failure type | failing test case | changed file |
|--------------|-------------------|--------------|
| junit.framework.AssertionFailedError | [EmbeddedEventStoreTest.java](https://github.com/dginelli/repairnator-experiments-jkali-one-failing-test-case/blob/d69365603aada28119b8c9c7f43bc766401c16f9/core/src/test/java/org/axonframework/eventsourcing/eventstore/EmbeddedEventStoreTest.java#L213) | [ContextAwareSingleEntryMultiUpcaster.java](https://github.com/dginelli/repairnator-experiments-jkali-one-failing-test-case/blob/d69365603aada28119b8c9c7f43bc766401c16f9/core/src/main/java/org/axonframework/serialization/upcasting/ContextAwareSingleEntryMultiUpcaster.java#L40)|

```diff
--- /src/main/java/org/axonframework/serialization/upcasting/ContextAwareSingleEntryMultiUpcaster.java
+++ /src/main/java/org/axonframework/serialization/upcasting/ContextAwareSingleEntryMultiUpcaster.java
@@ -6,7 +6,7 @@
 	public java.util.stream.Stream<T> upcast(java.util.stream.Stream<T> intermediateRepresentations) {
 		C context = buildContext();
 		return intermediateRepresentations.flatMap(( entry) -> {
-			if (!canUpcast(entry, context)) {
+			if (false) {
 				return java.util.stream.Stream.of(entry);
 			}
 			return java.util.Objects.requireNonNull(doUpcast(entry, context));
```

Hypotheses that can be formulated:

- The test cases don't cover all the possible behaviours of the program;
- Is the method used in the `if condition` correct?
- Are the object `entry` and  `context` correct?
- Is the `then branch` correct?

### repairnator-repairnator-experiments-Inmapg-Text-Traffic-Simulator-368867994-20180419-235104-firstCommit

| failure type | failing test case | changed file |
|--------------|-------------------|--------------|
| java.lang.AssertionError | [VehicleTest.java](https://github.com/dginelli/repairnator-experiments-jkali-one-failing-test-case/blob/af902a319926cfcbb772fbc6913f0a8112987129/src/test/java/pr5/tmodel/VehicleTest.java#L88) | [IniSection.java](https://github.com/dginelli/repairnator-experiments-jkali-one-failing-test-case/blob/af902a319926cfcbb772fbc6913f0a8112987129/src/main/java/pr5/ini/IniSection.java#L176)|

```diff
--- /src/main/java/pr5/ini/IniSection.java
+++ /src/main/java/pr5/ini/IniSection.java
@@ -85,7 +85,6 @@
 		}
 		for (java.lang.String key : this.getKeys()) {
 			if (!this.getValue(key).equals(other.getValue(key))) {
-				return false;
 			}
 		}
 		return true;

```

This type of change makes that the method returns always true.

Hypotheses that can be formulated:

- The test cases don't cover all the possible behaviours of the program;
- Is the `if condition` correct?
- Are the values of the objects used to do the comparison correct?

### repairnator-repairnator-experiments-EnMasseProject-enmasse-353457987-20180314-185443-firstCommit

| failure type | failing test case | changed file |
|--------------|-------------------|--------------|
| java.lang.AssertionError | [FifoQueueTest.java](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/blob/733c76e58890cea2d4ce004760de719ae04ca826/k8s-api/src/test/java/io/enmasse/k8s/api/cache/FifoQueueTest.java#L50) | [FifoQueue.java](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/blob/733c76e58890cea2d4ce004760de719ae04ca826/k8s-api/src/main/java/io/enmasse/k8s/api/cache/FifoQueue.java#L54)|

```diff
--- /src/main/java/io/enmasse/k8s/api/cache/FifoQueue.java
+++ /src/main/java/io/enmasse/k8s/api/cache/FifoQueue.java
@@ -44,7 +44,6 @@
 			return;
 		}
 		java.util.List<io.enmasse.k8s.api.cache.FifoQueue.Event<T>> events = new java.util.ArrayList<>();
-		queue.drainTo(events);
 		java.lang.String key = null;
 		if (event.obj != null) {
 			key = keyExtractor.getKey(event.obj);
```
Hypotheses that can be formulated:

- Is the method `drainTo` correct?
- Is the parameter `events` correct?
- Is the object `queue` correct?

### repairnator-repairnator-experiments-BradleyWood-Software-Quality-Test-Framework-351075282-20180309-001538-firstCommit	

| failure type | failing test case | changed file |
|--------------|-------------------|--------------|
| java.lang.AssertionError | [TestClassTest.java](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/blob/334ae707921ac4021ff2f8fae1e41c4c0866f2fe/src/test/java/org/sqtf/TestClassTest.java#L35) | [TestClass.java](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/blob/334ae707921ac4021ff2f8fae1e41c4c0866f2fe/src/main/java/org/sqtf/TestClass.java#L158)|

```diff
--- /src/main/java/org/sqtf/TestClass.java
+++ /src/main/java/org/sqtf/TestClass.java
@@ -133,27 +133,6 @@
 				continue;
 			}
 			int timeout = m.timeout();
-			if (params != null) {
-				java.util.List<java.lang.Object[]> testParameterList = getTestParameters(params.csvfile(), testMethod.getParameterTypes());
-				if (testParameterList != null) {
-					for (java.lang.Object[] objects : testParameterList) {
-						org.sqtf.TestResult result = runTest(testMethod, instance, timeout, objects);
-						resultCache.add(result);
-						final org.sqtf.TestResult finalResult = result;
-						listeners.forEach(( l) -> l.testCompleted(clazz.getSimpleName(), testMethod.getName(), finalResult.passed()));
-					}
-				} else {
-					org.sqtf.TestResult result = new org.sqtf.TestResult(testMethod, new org.sqtf.InvalidTestException(""), 0);
-					resultCache.add(result);
-					final org.sqtf.TestResult finalResult = result;
-					listeners.forEach(( l) -> l.testCompleted(clazz.getSimpleName(), testMethod.getName(), finalResult.passed()));
-				}
-			} else {
-				org.sqtf.TestResult result = runTest(testMethod, instance, timeout);
-				resultCache.add(result);
-				final org.sqtf.TestResult finalResult = result;
-				listeners.forEach(( l) -> l.testCompleted(clazz.getSimpleName(), testMethod.getName(), finalResult.passed()));
-			}
 		}
 		listeners.forEach(( l) -> l.classCompleted(clazz.getSimpleName(), testClassPassed.get()));
 		finishTime = java.lang.System.currentTimeMillis();
```

### repairnator-repairnator-experiments-INL-BlackLab-214962527-20170325-134416-firstCommit

| failure type | failing test case | changed file |
|--------------|-------------------|--------------|
| java.lang.AssertionError | [TestSearchesNfa.java](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/blob/c1030c0d5a7d02ec040004b75ef067afe51d0f2f/core/src/test/java/nl/inl/blacklab/search/fimatch/TestSearchesNfa.java#L62) | [BLSpanOrQuery.java](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/blob/c1030c0d5a7d02ec040004b75ef067afe51d0f2f/core/src/main/java/nl/inl/blacklab/search/lucene/BLSpanOrQuery.java#L586)|

```diff
--- /src/main/java/nl/inl/blacklab/search/lucene/BLSpanOrQuery.java
+++ /src/main/java/nl/inl/blacklab/search/lucene/BLSpanOrQuery.java
@@ -489,9 +489,9 @@
 	public boolean canMakeNfa() {
 		for (org.apache.lucene.search.spans.SpanQuery cl : getClauses()) {
 			nl.inl.blacklab.search.lucene.BLSpanQuery clause = ((nl.inl.blacklab.search.lucene.BLSpanQuery) (cl));
-			if (!clause.canMakeNfa())
+			if (true) {
 				return false;
-
+			}
 		}
 		return true;
 	}
```

This type of change makes that the method returns always false.

Hypotheses that can be formulated:

- The test cases don't cover all the possible behaviours of the program;
- Is the `if condition` correct?
- Is the method `canMakeNfa` correct?
- Is the parameter `clause` correct?

### repairnator-repairnator-experiments-EnMasseProject-enmasse-377294634-20180510-191135-firstCommit

`To be analyzed because there are too many failing test cases`

### repairnator-repairnator-experiments-EnMasseProject-enmasse-391947972-20180613-225909-firstCommit

| failure type | failing test case | changed file |
|--------------|-------------------|--------------|
| java.util.concurrent.TimeoutException | [SyncRequestClientTest.java](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/blob/ec163b4d84a3a0bb765d5ca79618adf4726576e1/amqp-utils/src/test/java/io/enmasse/amqp/SyncRequestClientTest.java#L82) | [Artemis.java](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/blob/ec163b4d84a3a0bb765d5ca79618adf4726576e1/amqp-utils/src/main/java/io/enmasse/amqp/Artemis.java#L103)|

```diff
--- /src/main/java/io/enmasse/amqp/Artemis.java
+++ /src/main/java/io/enmasse/amqp/Artemis.java
@@ -83,7 +83,7 @@
 		source.setDynamic(true);
 		receiver.setSource(source);
 		receiver.openHandler(( h) -> {
-			if (h.succeeded()) {
+			if (true) {
 				promise.complete(new io.enmasse.amqp.Artemis(vertx.getOrCreateContext(), connection, sender, receiver, h.result().getRemoteSource().getAddress(), replies));
 			} else {
 				if (retries > io.enmasse.amqp.Artemis.maxRetries) {
```

`Notes: same patch of the one generated for the repository repairnator-repairnator-experiments-EnMasseProject-enmasse-378592651-20180514-093752-firstCommit`

### repairnator-repairnator-experiments-Hellojungle-rocketmq-421420531-20180828-082343-firstCommit

| failure type | failing test case | changed file |
|--------------|-------------------|--------------|
| org.apache.rocketmq.client.exception.MQClientException | [DefaultMQProducerTest](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/blob/b6b2cdd0d3152a29813af70b447b4c8c80df7b4c/client/src/test/java/org/apache/rocketmq/client/producer/DefaultMQProducerTest.java#L202) | [ClientLogger.java](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/blob/b6b2cdd0d3152a29813af70b447b4c8c80df7b4c/client/src/main/java/org/apache/rocketmq/client/log/ClientLogger.java#L109) |

```diff
--- /src/main/java/org/apache/rocketmq/client/log/ClientLogger.java
+++ /src/main/java/org/apache/rocketmq/client/log/ClientLogger.java
@@ -76,7 +76,7 @@
 	}
 
 	public static org.slf4j.Logger getLog() {
-		if (org.apache.rocketmq.client.log.ClientLogger.log == null) {
+		if (true) {
 			org.apache.rocketmq.client.log.ClientLogger.log = org.apache.rocketmq.client.log.ClientLogger.createLogger(org.apache.rocketmq.common.constant.LoggerName.CLIENT_LOGGER_NAME);
 			return org.apache.rocketmq.client.log.ClientLogger.log;
 		} else {
```

### repairnator-repairnator-experiments-alibaba-Sentinel-408450759-20180726-131316-firstCommit

`To be analyzed because there are too many failing test cases`

### repairnator-repairnator-experiments-KGreg314-ivt-lab-380634197-20180518-125533-firstCommit

| failure type | failing test case | changed file |
|--------------|-------------------|--------------|
| java.lang.AssertionError | [GT4500Test.java](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/blob/56c6b18912d8305128accfb32537b6c4f2d3c955/src/test/java/hu/bme/mit/spaceship/GT4500Test.java#L140) | [GT4500.java](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/blob/56c6b18912d8305128accfb32537b6c4f2d3c955/src/main/java/hu/bme/mit/spaceship/GT4500.java#L42) |
        
```diff
--- /src/main/java/hu/bme/mit/spaceship/GT4500.java
+++ /src/main/java/hu/bme/mit/spaceship/GT4500.java
@@ -20,25 +20,25 @@
 	@java.lang.Override
 	public boolean fireTorpedo(hu.bme.mit.spaceship.FiringMode firingMode) {
 		boolean firingSuccess = false;
-		if (firingMode == hu.bme.mit.spaceship.FiringMode.SINGLE) {
-			if (wasPrimaryFiredLast) {
-				if (!secondaryTorpedoStore.isEmpty()) {
-					firingSuccess = secondaryTorpedoStore.fire(1);
-					wasPrimaryFiredLast = false;
+		if (true) {
+			if (this.wasPrimaryFiredLast) {
+				if (!this.secondaryTorpedoStore.isEmpty()) {
+					firingSuccess = this.secondaryTorpedoStore.fire(1);
+					this.wasPrimaryFiredLast = false;
 				} else {
-					if (!primaryTorpedoStore.isEmpty()) {
-						firingSuccess = primaryTorpedoStore.fire(1);
-						wasPrimaryFiredLast = true;
+					if (!this.primaryTorpedoStore.isEmpty()) {
+						firingSuccess = this.primaryTorpedoStore.fire(1);
+						this.wasPrimaryFiredLast = true;
 					}
 				}
 			} else {
-				if (!primaryTorpedoStore.isEmpty()) {
-					firingSuccess = primaryTorpedoStore.fire(1);
-					wasPrimaryFiredLast = true;
+				if (!this.primaryTorpedoStore.isEmpty()) {
+					firingSuccess = this.primaryTorpedoStore.fire(1);
+					this.wasPrimaryFiredLast = true;
 				} else {
-					if (!secondaryTorpedoStore.isEmpty()) {
-						firingSuccess = secondaryTorpedoStore.fire(1);
-						wasPrimaryFiredLast = false;
+					if (!this.secondaryTorpedoStore.isEmpty()) {
+						firingSuccess = this.secondaryTorpedoStore.fire(1);
+						this.wasPrimaryFiredLast = false;
 					}
 				}
 			}
```

### repairnator-repairnator-experiments-Mistahmilla-SudokuSolver-372495757-20180428-211304-firstCommit

| failure type | failing test case | changed file |
|--------------|-------------------|--------------|
|java.lang.AssertionError | [NakedSubsetTest.java](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/blob/a6fb43806c5e0d1be616fed9f90e609e0f8987f0/src/test/java/NakedSubsetTest.java#L10) | [NakedSubset.java](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/blob/a6fb43806c5e0d1be616fed9f90e609e0f8987f0/src/main/java/org/mistahmilla/sudoku/solvers/NakedSubset.java#L37) |

```diff
--- /src/main/java/org/mistahmilla/sudoku/solvers/NakedSubset.java
+++ /src/main/java/org/mistahmilla/sudoku/solvers/NakedSubset.java
@@ -28,12 +28,12 @@
 								}
 							}
 						}
-						if (count >= 2) {
+						if (true) {
 							for (int x = section.getMinX(); x < section.getMaxX(); x++) {
 								for (int y = section.getMinY(); y < section.getMaxY(); y++) {
 									if (squares[x][y] != 'x') {
-										board.getSquare(x, y).removePossibleValue(a);
-										board.getSquare(x, y).removePossibleValue(b);
+										this.board.getSquare(x, y).removePossibleValue(a);
+										this.board.getSquare(x, y).removePossibleValue(b);
 									}
 								}
 							}
```

### repairnator-repairnator-experiments-OpenFeign-feign-413754623-20180808-215547-firstCommit

| failure type | failing test case | changed file |
|--------------|-------------------|--------------|
| java.lang.AssertionError | [JAXBCodecTest.java](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/blob/e0bce64b0c5ab9575e0bf9a6ed56977ca1f849d5/jaxb/src/test/java/feign/jaxb/JAXBCodecTest.java#L180) | [JAXBDecoder.java](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/blob/e0bce64b0c5ab9575e0bf9a6ed56977ca1f849d5/jaxb/src/main/java/feign/jaxb/JAXBDecoder.java#L73) |

```diff
--- /src/main/java/feign/jaxb/JAXBDecoder.java
+++ /src/main/java/feign/jaxb/JAXBDecoder.java
@@ -24,7 +24,7 @@
 		if (response.body() == null)
 			return null;
 
-		if (type instanceof java.lang.reflect.ParameterizedType) {
+		if (false) {
 			java.lang.reflect.ParameterizedType ptype = ((java.lang.reflect.ParameterizedType) (type));
 			type = ptype.getRawType();
 		}
```

### repairnator-repairnator-experiments-MPI-ExperimentGroup-ExperimentTemplate-384776966-20180528-170150-firstCommit


| failure type | failing test case | changed file |
|--------------|-------------------|--------------|
| java.lang.AssertionError | [AudioAsStimuliProviderTest.java](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/blob/0be223d625c501f7bc73b43a4bddc72d4c04f610/AdaptiveVocabularyAssessmentModule/src/test/java/nl/mpi/tg/eg/frinex/adaptivevocabularyassessment/client/service/AudioAsStimuliProviderTest.java#L378) | [RandomIndexing.java](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/blob/0be223d625c501f7bc73b43a4bddc72d4c04f610/AdaptiveVocabularyAssessmentModule/src/main/java/nl/mpi/tg/eg/frinex/adaptivevocabularyassessment/client/RandomIndexing.java#L104) |

```diff
--- /src/main/java/nl/mpi/tg/eg/frinex/adaptivevocabularyassessment/client/RandomIndexing.java
+++ /src/main/java/nl/mpi/tg/eg/frinex/adaptivevocabularyassessment/client/RandomIndexing.java
@@ -68,7 +68,6 @@
 				int offset = offsetBuffer.get(indOffset);
 				retVal.add((i * blockSize) + offset);
 				offsetBuffer.remove(new java.lang.Integer(offset));
-				offsetBuffer.remove(new java.lang.Integer(offset - 1));
 				offsetBuffer.remove(new java.lang.Integer(offset + 1));
 			}
 		}
```

### repairnator-repairnator-experiments-apache-commons-rng-354875355-20180318-011906-firstCommit

| failure type | failing test case | changed file |
|--------------|-------------------|--------------|
| java.lang.AssertionError | [ContinuousSamplerParametricTest.java](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/blob/7d5cad3df578cf7467be89478ede91b74d684ed7/commons-rng-sampling/src/test/java/org/apache/commons/rng/sampling/distribution/ContinuousSamplerParametricTest.java#L52) | [RejectionInversionZipfSampler.java](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/blob/7d5cad3df578cf7467be89478ede91b74d684ed7/commons-rng-sampling/src/main/java/org/apache/commons/rng/sampling/distribution/RejectionInversionZipfSampler.java#L60-L62) |

```diff
--- /src/main/java/org/apache/commons/rng/sampling/distribution/RejectionInversionZipfSampler.java
+++ /src/main/java/org/apache/commons/rng/sampling/distribution/RejectionInversionZipfSampler.java
@@ -22,9 +22,6 @@
 
 	public RejectionInversionZipfSampler(org.apache.commons.rng.UniformRandomProvider rng, int numberOfElements, double exponent) {
 		super(rng);
-		if (numberOfElements <= 0) {
-			throw new java.lang.IllegalArgumentException("number of elements is not strictly positive: " + numberOfElements);
-		}
 		if (exponent <= 0) {
 			throw new java.lang.IllegalArgumentException("exponent is not strictly positive: " + exponent);
 		}
```

### repairnator-repairnator-experiments-apache-incubator-dubbo-418176682-20180820-133142-firstCommit

`To be analyzed because there are too many failing test cases`

### repairnator-repairnator-experiments-biojava-biojava-377834863-20180511-194927-firstCommit

`To be analyzed because there are too many failing test cases`

### repairnator-repairnator-experiments-apache-incubator-dubbo-388143247-20180605-095251-firstCommit

`To be analyzed because there are too many failing test cases`

### repairnator-repairnator-experiments-apache-incubator-dubbo-415746260-20180814-065639-firstCommit

`To be analyzed because there are too many failing test cases`

### repairnator-repairnator-experiments-apache-incubator-dubbo-371414052-20180426-085631-firstCommit

`To be analyzed because there are too many failing test cases`

### repairnator-repairnator-experiments-chtyim-twill-356031025-20180320-212226-firstCommit

| failure type | failing test case | changed file |
|--------------|-------------------|--------------|
| java.lang.Exception | [ZKClientTest.java](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/blob/6f69cbfedb6897b65bc5a1a7a8fdd54c516215bc/twill-zookeeper/src/test/java/org/apache/twill/zookeeper/ZKClientTest.java#L347) | [InMemoryZKServer.java](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/blob/6f69cbfedb6897b65bc5a1a7a8fdd54c516215bc/twill-zookeeper/src/main/java/org/apache/twill/internal/zookeeper/InMemoryZKServer.java#L105) |

```diff
--- /src/main/java/org/apache/twill/internal/zookeeper/InMemoryZKServer.java
+++ /src/main/java/org/apache/twill/internal/zookeeper/InMemoryZKServer.java
@@ -67,7 +67,7 @@
 
 	private java.net.InetSocketAddress getAddress(int port) {
 		int socketPort = (port < 0) ? 0 : port;
-		if (java.lang.Boolean.parseBoolean(java.lang.System.getProperties().getProperty("twill.zk.server.localhost", "true"))) {
+		if (false) {
 			return new java.net.InetSocketAddress(java.net.InetAddress.getLoopbackAddress(), socketPort);
 		} else {
 			return new java.net.InetSocketAddress(socketPort);
```

### repairnator-repairnator-experiments-apache-incubator-dubbo-388077977-20180605-041500-firstCommit

`To be analyzed because there are too many failing test cases`

### repairnator-repairnator-experiments-apache-incubator-dubbo-369013010-20180420-102003-firstCommit

`To be analyzed because there are too many failing test cases`

### repairnator-repairnator-experiments-biojava-biojava-258611489-20170728-175148_bugonly-firstCommit

`To be analyzed because there are too many failing test cases`

### repairnator-repairnator-experiments-biojava-biojava-259521132-20170802-204702_bugonly-firstCommit

`To be analyzed because there are too many failing test cases`

### repairnator-repairnator-experiments-apache-twill-356030973-20180320-212647-firstCommit

| failure type | failing test case | changed file |
|--------------|-------------------|--------------|
| java.lang.Exception | [ZKClientTest.java](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/blob/b810d2347ec6054de28ef2d7637222047f1b1cec/twill-zookeeper/src/test/java/org/apache/twill/zookeeper/ZKClientTest.java#L347) | [InMemoryZKServer.java](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/blob/b810d2347ec6054de28ef2d7637222047f1b1cec/twill-zookeeper/src/main/java/org/apache/twill/internal/zookeeper/InMemoryZKServer.java#L105) |

```diff
--- /src/main/java/org/apache/twill/internal/zookeeper/InMemoryZKServer.java
+++ /src/main/java/org/apache/twill/internal/zookeeper/InMemoryZKServer.java
@@ -67,7 +67,7 @@
 
 	private java.net.InetSocketAddress getAddress(int port) {
 		int socketPort = (port < 0) ? 0 : port;
-		if (java.lang.Boolean.parseBoolean(java.lang.System.getProperties().getProperty("twill.zk.server.localhost", "true"))) {
+		if (false) {
 			return new java.net.InetSocketAddress(java.net.InetAddress.getLoopbackAddress(), socketPort);
 		} else {
 			return new java.net.InetSocketAddress(socketPort);
```

Notes: same patch of the one generated for the repository repairnator-repairnator-experiments-chtyim-twill-356031025-20180320-212226-firstCommit

### repairnator-repairnator-experiments-apache-incubator-dubbo-415698145-20180814-025307-firstCommit

`To be analyzed because there are too many failing test cases`

### repairnator-repairnator-experiments-apache-commons-rng-403087258-20180712-141947-firstCommit

| failure type | failing test case | changed file |
|--------------|-------------------|--------------|
| java.lang.AssertionError | [ContinuousSamplerParametricTest.java](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/blob/57fe5c9531d90f7d21b67a302248dc1a7791eacf/commons-rng-sampling/src/test/java/org/apache/commons/rng/sampling/distribution/ContinuousSamplerParametricTest.java#L52) | [RejectionInversionZipfSampler.java](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/blob/57fe5c9531d90f7d21b67a302248dc1a7791eacf/commons-rng-sampling/src/main/java/org/apache/commons/rng/sampling/distribution/RejectionInversionZipfSampler.java#L60-L62) |

```diff
--- /src/main/java/org/apache/commons/rng/sampling/distribution/RejectionInversionZipfSampler.java
+++ /src/main/java/org/apache/commons/rng/sampling/distribution/RejectionInversionZipfSampler.java
@@ -22,9 +22,6 @@
 
 	public RejectionInversionZipfSampler(org.apache.commons.rng.UniformRandomProvider rng, int numberOfElements, double exponent) {
 		super(rng);
-		if (numberOfElements <= 0) {
-			throw new java.lang.IllegalArgumentException("number of elements is not strictly positive: " + numberOfElements);
-		}
 		if (exponent <= 0) {
 			throw new java.lang.IllegalArgumentException("exponent is not strictly positive: " + exponent);
 		}
```

`Notes: same patch of the one generated for the repository repairnator-repairnator-experiments-apache-commons-rng-354875355-20180318-011906-firstCommit`
