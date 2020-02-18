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

Changing the if condition to `if (true)`, the program runs every time the `then branch`. This means that:
  
- The test cases don't cover all the possible behaviours of the program;
- Is the `else branch` correct?
- Is the `if condition` correct?


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
- The test cases don't cover all the possible behaviours of the program;
- Is the method used in the `if condition` correct?
- Is the `then branch` correct?
