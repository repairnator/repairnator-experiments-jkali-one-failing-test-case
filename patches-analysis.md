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

Changing the if condition to `if (true)`, the source code runs every time the `then branch`. This means that:
  
- The test cases don't cover all the possible behaviours of the program;
- Is the `else branch` correct?






