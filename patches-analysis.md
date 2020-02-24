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
| org.apache.rocketmq.client.exception.MQClientException | [DefaultMQProducerTest](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/blob/b6b2cdd0d3152a29813af70b447b4c8c80df7b4c/client/src/test/java/org/apache/rocketmq/client/producer/DefaultMQProducerTest.java#L202) | [ClientLogger.java](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/blob/b6b2cdd0d3152a29813af70b447b4c8c80df7b4c/client/src/main/java/org/apache/rocketmq/client/log/ClientLogger.java#L109)

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
