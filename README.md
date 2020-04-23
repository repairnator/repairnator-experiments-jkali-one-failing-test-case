# JKali Patches associated with Travis CI builds having only one failing test case

This is an open-science repository that collects the Kali patches generated by [Repairnator](https://github.com/eclipse/repairnator) during the analysis of the Travis CI builds characterised by only one failing test case and no test cases with errors.

This repository aims to contain the code of the failing Java projects and to analyze the Kali patches generated using [AstorJKali](https://github.com/SpoonLabs/astor#jkali).

## Content of the repository

The structure of the repository is as follows:

- Branch `master` contains the `jkali-patches` folder and the documentation of this repository;
- The `jkali-patches` folder contains the patches generated by Repairnator (using the `AstorJKali` repair mode) analyzing the Travis CI build failure related to Java programs that use Maven as building tool;
- For each of these failing builds, there is a specific branch with all the information related to the building of the project, bug reproduction, failing tests and repair attempts.

## Statistics

The builds collected in the dataset [repairnator-experiments](https://github.com/repairnator/repairnator-experiments) with only one failing test case are 2.381. In the following table, there are the different states of the builds detected by Repairnator during the analysis:

|                        | Failing | Patched | Not clonable | Not buildable | Not checked out | Not testable | Not failing|
|--------------------------|:--------:|:--------:|:------------:|:-------------:|---------------:|--------------:|-------:|
| **Number of the builds** | 1.687    | 73       |     -        | 275           | -              | 50            | 262    |

In this repository there are 1.763 branches (excluding `master` branch), each of them associated with a failure.

## Builds to be re-executed

- **EnMasseProject-enmasse-377294634-20180510-191135**: the reproduction of the failure generated `four erroring test cases` (three `java.net.BindException` and one `java.util.concurrent.TimeoutException`) instead of only one failing test case. I tried to run the program in local (without using Repairnator), but it passed all the test cases.

## Patch Analysis

### EnMasseProject-enmasse-353457987-20180314-185443

- **Branch associated with the failure**: [repairnator-repairnator-experiments-EnMasseProject-enmasse-353457987-20180314-185443-firstCommit](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/tree/repairnator-repairnator-experiments-EnMasseProject-enmasse-353457987-20180314-185443-firstCommit)

- **Information about the failure**:

| Failure type | Failing test case | Changed file by AstorJKali |
|--------------|-------------------|----------------------------|
| org.mockito.exceptions.verification.TooLittleActualInvocations | [FifoQueueTest.java](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/blob/733c76e58890cea2d4ce004760de719ae04ca826/k8s-api/src/test/java/io/enmasse/k8s/api/cache/FifoQueueTest.java#L64) | [FifoQueue.java](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/blob/733c76e58890cea2d4ce004760de719ae04ca826/k8s-api/src/main/java/io/enmasse/k8s/api/cache/FifoQueue.java#L54)|

- **Kali patch**:

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

- **Overview**: the problem is related to the test case that checks the correct behavior of the [method](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/blob/733c76e58890cea2d4ce004760de719ae04ca826/k8s-api/src/main/java/io/enmasse/k8s/api/cache/FifoQueue.java#L46) changed by AstorJKali. Indeed, looking at the commit history of the project, the developer [changed the test case](https://github.com/EnMasseProject/enmasse/pull/1058/commits/848ff42b0ed3fa5888778957ac8daca909c98072) to handle the failure associated with the use of method `draintTo`, that is the method removed by AstorJKali to create the patch.
- **Reason why the patch has been generated**: AstorJKali managed to generate a patch because the test case only checks if the FifoQueue object is empty or not, but it doesn't check the inner properties of that object. In particular, it doesn't check the size of the private member variable `queue`. Thus, removing the call to the method `drainTo` in the method `pop` (as AstorJKali did), the queue `queue` is not really emptied, and so the method `pop` is executed two times (even if the queue should have been emptied the first time) satisfying the test case that checks how many times that method is called.
- **Useful information for the developer**: the Kali patch suggested that there was a problem with the method `drainTo` or with the test case `testRemove` that checks the behavior of that method. In this case, since the method `drainTo` is a method of the JDK, there is more probability that the error is not related to that method, but in the way used to test it. Thus, the developer can start to analyze the problem focusing on the test case, checking if it is correct or not.

- **Fix of the test case**:

```diff
From 848ff42b0ed3fa5888778957ac8daca909c98072 Mon Sep 17 00:00:00 2001
From: Ulf Lilleengen <lulf@redhat.com>
Date: Wed, 14 Mar 2018 20:29:54 +0100
Subject: Fix test to handle drain

---
 .../src/test/java/io/enmasse/k8s/api/cache/FifoQueueTest.java   | 2 +-
 1 file changed, 1 insertion(+), 1 deletion(-)

diff --git a/k8s-api/src/test/java/io/enmasse/k8s/api/cache/FifoQueueTest.java b/k8s-api/src/test/java/io/enmasse/k8s/api/cache/FifoQueueTest.java
index 213c76fef1..ec7b7fda7f 100644
--- a/k8s-api/src/test/java/io/enmasse/k8s/api/cache/FifoQueueTest.java
+++ b/k8s-api/src/test/java/io/enmasse/k8s/api/cache/FifoQueueTest.java
@@ -61,7 +61,7 @@ public void testRemove() throws Exception {
         assertTrue(queue.list().isEmpty());
 
         queue.pop(mockProc, 0, TimeUnit.SECONDS);
-        verify(mockProc, times(2)).process(eq("k1"));
+        verify(mockProc).process(eq("k1"));
         assertTrue(queue.listKeys().isEmpty());
         assertTrue(queue.list().isEmpty());
     }
```

### Inmapg-Text-Traffic-Simulator-368867994-20180419-235104

- **Branch associated with the failure**: [repairnator-repairnator-experiments-Inmapg-Text-Traffic-Simulator-368867994-20180419-235104-firstCommit](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/tree/repairnator-repairnator-experiments-Inmapg-Text-Traffic-Simulator-368867994-20180419-235104-firstCommit)

- **Information about the failure**:

| Failure type | Failing test case | Changed file by AstorJKali |
|--------------|-------------------|----------------------------|
| java.lang.AssertionError | [VehicleTest.java](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/blob/af902a319926cfcbb772fbc6913f0a8112987129/src/test/java/pr5/tmodel/VehicleTest.java#L115) | [IniSection.java](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/blob/af902a319926cfcbb772fbc6913f0a8112987129/src/main/java/pr5/ini/IniSection.java#L176)|

- **Kali patch**:

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

- **Overview**: the problem is related to the test case that checks the correct behavior of the [method](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/blob/af902a319926cfcbb772fbc6913f0a8112987129/src/main/java/pr5/ini/IniSection.java#L159) changed by AstorJKali. Indeed, looking at the commit history of the project, the developer [changed the test case](https://github.com/Inmapg/Traffic-Simulator/commit/e59608185c3f326d46eb82371614d5ee354a9ba9) to handle the failure during the comparison of two `Vehicle` objects.
- **Reason why the patch has been generated**: AstorJKali managed to create a patch because the test case expects that two `IniSection` objects are equals, and since the patch removes the `return false` instruction, the objects are always equal, even if they are different. For this reason, there isn't the possibilty to recognise the error in the current test case. It is necessary to add some other checks in the test case that require `false` as expected value (e.g., a check of the individual properties of the objects, after a change to one of them, without the use of method `equals`).
- **Useful information for the developer**: the Kali patch suggested that there was a problem with the method `equals` or with the test case `VehicleFaultyTest` that checks the behavior of that method. Since it's correct that when two values are different the result of the comparison is false (this instruction has been removed by AstorJKali to create the fix), the developer can focus on the implementation of the test case to try to understand where the error might be.

- **Fix of the test case**:

```diff
From e59608185c3f326d46eb82371614d5ee354a9ba9 Mon Sep 17 00:00:00 2001
From: Inma <Inma@eduroam162180.eduroam.ucm.es>
Date: Thu, 19 Apr 2018 23:54:13 +0200
Subject: [PATCH] Fixing VehicleTest class

---
 src/test/java/pr5/tmodel/VehicleTest.java | 2 +-
 1 file changed, 1 insertion(+), 1 deletion(-)

diff --git a/src/test/java/pr5/tmodel/VehicleTest.java b/src/test/java/pr5/tmodel/VehicleTest.java
index 2248632..ca127a5 100755
--- a/src/test/java/pr5/tmodel/VehicleTest.java
+++ b/src/test/java/pr5/tmodel/VehicleTest.java
@@ -109,7 +109,7 @@ public void VehicleFaultyTest(){
         assertEquals(correct, result);
         vehicle.makeFaulty(2);
         correct.setValue("time", "2");
-        correct.setValue("speed", "10");
+        correct.setValue("speed", "0");
         correct.setValue("faulty", "2");
         result = vehicle.generateReport(2);
         assertEquals(correct, result);
```

### KGreg314-ivt-lab-380634197-20180518-125533

- **Branch associated with the failure**: [repairnator-repairnator-experiments-KGreg314-ivt-lab-380634197-20180518-125533-firstCommit](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/tree/repairnator-repairnator-experiments-KGreg314-ivt-lab-380634197-20180518-125533-firstCommit)

- **Information about the failure**:

| Failure type | Failing test case | Changed file by AstorJKali |
|--------------|-------------------|----------------------------|
| java.lang.AssertionError | [GT4500Test.java](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/blob/56c6b18912d8305128accfb32537b6c4f2d3c955/src/test/java/hu/bme/mit/spaceship/GT4500Test.java#L151) | [GT4500.java](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/blob/56c6b18912d8305128accfb32537b6c4f2d3c955/src/main/java/hu/bme/mit/spaceship/GT4500.java#L38)|

- **Kali patch**:

```diff
--- /src/main/java/hu/bme/mit/spaceship/GT4500.java
+++ /src/main/java/hu/bme/mit/spaceship/GT4500.java
@@ -20,25 +20,25 @@
 @java.lang.Override
 public boolean fireTorpedo(hu.bme.mit.spaceship.FiringMode firingMode) {
  boolean firingSuccess = false;
- if (firingMode == hu.bme.mit.spaceship.FiringMode.SINGLE) {
     if (wasPrimaryFiredLast) {
      if (!secondaryTorpedoStore.isEmpty()) {
       firingSuccess = secondaryTorpedoStore.fire(1);
       wasPrimaryFiredLast = false;
+ if (true) {
     if (wasPrimaryFiredLast) {
      if (!secondaryTorpedoStore.isEmpty()) {
       firingSuccess = this.secondaryTorpedoStore.fire(1);
       this.wasPrimaryFiredLast = false;
```

- **Overview**: the problem is related to the outermost `else branch` of [the method](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/blob/56c6b18912d8305128accfb32537b6c4f2d3c955/src/main/java/hu/bme/mit/spaceship/GT4500.java#L38) removed by AstorJKali. Indeed, looking at the commit history of the project, the developer implemented the outermost `else branch` in the [next commit](https://github.com/KGreg314/ivt-lab/compare/e45e9f994bdc...5669e29937d3).
- **Reason why the patch has been generated**: the test cases don't cover all of the possible paths of the method `fireTorpedo`, that is the bugged method changed by AstorJKali. In particular, applying the Kali patch, the method `fireTorpedo` executed by the failing test case returns the default value (`false`) that is the value expected by the test to pass. Actually, the test cases passes just because the default value is the same of the exptected value by the test case, and not because of the correctness of the patch.
- **Useful information for the developer**: the Kali patch suggested that the feature associated with the `else branch` (removed by the patch generated by AstorJKali) had an error. Indeed, looking at the source code, the `else branch`had yet to be implemented by the developer.
- **Fix of the source code**:

```diff
From 5669e29937d3a18c4ac17d5e33ba2dd03cd5ec75 Mon Sep 17 00:00:00 2001
From: meres <kgreg314@gmail.com>
Date: Fri, 18 May 2018 04:05:40 -0700
Subject: [PATCH] Add JoCoCo & Implement ALL firing mode perfectly

diff --git a/src/main/java/hu/bme/mit/spaceship/GT4500.java b/src/main/java/hu/bme/mit/spaceship/GT4500.java
index 48160c0..bbcd26d 100644
--- a/src/main/java/hu/bme/mit/spaceship/GT4500.java
+++ b/src/main/java/hu/bme/mit/spaceship/GT4500.java
@@ -78,7 +78,18 @@ public boolean fireTorpedo(FiringMode firingMode) {
      else{
         // try to fire both of the torpedo stores
         //TODO implement feature
-	firingSuccess = true;
+        if (! primaryTorpedoStore.isEmpty() && ! secondaryTorpedoStore.isEmpty()) {
+          boolean successPrimary = primaryTorpedoStore.fire(1);
+	        boolean successSecondary = secondaryTorpedoStore.fire(1);
+	        firingSuccess = successPrimary || successSecondary;
+          wasPrimaryFiredLast = false;
+        } else if (! primaryTorpedoStore.isEmpty()) {
+            firingSuccess = primaryTorpedoStore.fire(1);
+            wasPrimaryFiredLast = true;          
+        } else if (! secondaryTorpedoStore.isEmpty()) {
+            firingSuccess = secondaryTorpedoStore.fire(1);
+            wasPrimaryFiredLast = false;          
+        }
 
         
     }
```

### apache-commons-rng-354875355-20180318-011906

**Branch associated with the failure**: [repairnator-repairnator-experiments-apache-commons-rng-354875355-20180318-011906-firstCommit](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/tree/repairnator-repairnator-experiments-apache-commons-rng-354875355-20180318-011906-firstCommit)

- **Information about the failure**:

| Failure type | Failing test case | Changed file by AstorJKali |
|--------------|-------------------|----------------------------|
| java.lang.AssertionError | [ContinuousSamplerParametricTest.java]() | []()|

### apache-commons-rng-403087258-20180712-141947

**Branch associated with the failure**: [repairnator-repairnator-experiments-apache-commons-rng-403087258-20180712-141947-firstCommit](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/tree/repairnator-repairnator-experiments-apache-commons-rng-403087258-20180712-141947-firstCommit)

- **Information about the failure**:

| Failure type | Failing test case | Changed file by AstorJKali |
|--------------|-------------------|----------------------------|
| java.lang.AssertionError| [ContinuousSamplerParametricTest.java]() | []()|

### atomix-atomix-365170225-20180411-171152

**Branch associated with the failure**: [repairnator-repairnator-experiments-atomix-atomix-365170225-20180411-171152-firstCommit](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/tree/repairnator-repairnator-experiments-atomix-atomix-365170225-20180411-171152-firstCommit)

- **Information about the failure**:

| Failure type | Failing test case | Changed file by AstorJKali |
|--------------|-------------------|----------------------------|
| java.lang.AssertionError | [DefaultClusterServiceTest.java]() | []()|

### BradleyWood-Software-Quality-Test-Framework-351075282-20180309-001538

**Branch associated with the failure**: [repairnator-repairnator-experiments-BradleyWood-Software-Quality-Test-Framework-351075282-20180309-001538-firstCommit](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/tree/repairnator-repairnator-experiments-BradleyWood-Software-Quality-Test-Framework-351075282-20180309-001538-firstCommit)

- **Information about the failure**:

| Failure type | Failing test case | Changed file by AstorJKali |
|--------------|-------------------|----------------------------|
| java.lang.AssertionError | [TestClassTest.java]() | []()|

### cqse-test-analyzer-397786068-20180628-145935

**Branch associated with the failure**: [repairnator-repairnator-experiments-cqse-test-analyzer-397786068-20180628-145935-firstCommit](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/tree/repairnator-repairnator-experiments-cqse-test-analyzer-397786068-20180628-145935-firstCommit)

- **Information about the failure**:

| Failure type | Failing test case | Changed file by AstorJKali |
|--------------|-------------------|----------------------------|
| org.junit.ComparisonFailure | [SurefireTestListenerTest.java]() | []()|

### dotwebstack-dotwebstack-framework-363986485-20180409-090844

**Branch associated with the failure**: [repairnator-repairnator-experiments-dotwebstack-dotwebstack-framework-363986485-20180409-090844-firstCommit](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/tree/repairnator-repairnator-experiments-dotwebstack-dotwebstack-framework-363986485-20180409-090844-firstCommit)

- **Information about the failure**:

| Failure type | Failing test case | Changed file by AstorJKali |
|--------------|-------------------|----------------------------|
| java.lang.AssertionError | [DirectEndPointRequestMapperTest.java]() | []()|

### dropwizard-metrics-374587117-20180503-220610-firstCommit

**Branch associated with the failure**: [repairnator-repairnator-experiments-dropwizard-metrics-374587117-20180503-220610-firstCommit](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/tree/repairnator-repairnator-experiments-dropwizard-metrics-374587117-20180503-220610-firstCommit)

- **Information about the failure**:

| Failure type | Failing test case | Changed file by AstorJKali |
|--------------|-------------------|----------------------------|
| org.mockito.exceptions.verification.WantedButNotInvoked | [InstrumentedHttpClientsTimerTest.java]() | []()|

### eclipse-hono-338971473-20180208-141728-firstCommit

**Branch associated with the failure**: [repairnator-repairnator-experiments-eclipse-hono-338971473-20180208-141728-firstCommit](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/tree/repairnator-repairnator-experiments-eclipse-hono-338971473-20180208-141728-firstCommit)

- **Information about the failure**:

| Failure type | Failing test case | Changed file by AstorJKali |
|--------------|-------------------|----------------------------|
| org.mockito.exceptions.verification.WantedButNotInvoked | [EventConsumerImplTest.java]() | []()|

### EnMasseProject-enmasse-378592651-20180514-093752-firstCommit

**Branch associated with the failure**: [repairnator-repairnator-experiments-EnMasseProject-enmasse-378592651-20180514-093752-firstCommit-firstCommit](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/tree/repairnator-repairnator-experiments-EnMasseProject-enmasse-378592651-20180514-093752-firstCommit)

- **Information about the failure**:

| Failure type | Failing test case | Changed file by AstorJKali |
|--------------|-------------------|----------------------------|
| java.lang.AssertionError | [ArtemisTest.java]() | []()|

### EnMasseProject-enmasse-387846982-20180604-173506-firstCommit

**Branch associated with the failure**: [repairnator-repairnator-experiments-EnMasseProject-enmasse-387846982-20180604-173506-firstCommit](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/tree/repairnator-repairnator-experiments-EnMasseProject-enmasse-387846982-20180604-173506-firstCommit)

- **Information about the failure**:

| Failure type | Failing test case | Changed file by AstorJKali |
|--------------|-------------------|----------------------------|
| java.lang.AssertionError | [HTTPServerTest.java]() | []()|

### INL-BlackLab-214962527-20170325-134416-firstCommit

**Branch associated with the failure**: [repairnator-repairnator-experiments-INL-BlackLab-214962527-20170325-134416-firstCommit](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/tree/repairnator-repairnator-experiments-INL-BlackLab-214962527-20170325-134416-firstCommit)

- **Information about the failure**:

| Failure type | Failing test case | Changed file by AstorJKali |
|--------------|-------------------|----------------------------|
| java.lang.AssertionError | [TestSearchesNfa.java]() | []()|

### jguyet-HttpRequest-400611810-20180705-224946

**Branch associated with the failure**: [repairnator-repairnator-experiments-jguyet-HttpRequest-400611810-20180705-224946-firstCommit](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/tree/repairnator-repairnator-experiments-jguyet-HttpRequest-400611810-20180705-224946-firstCommit)

- **Information about the failure**:

| Failure type | Failing test case | Changed file by AstorJKali |
|--------------|-------------------|----------------------------|
| org.opentest4j.AssertionFailedError | [TestRequest.java]() | []()|

### jmrozanec-cron-utils-249918159-20170704-112646_bugonly

**Branch associated with the failure**: [repairnator-repairnator-experiments-jmrozanec-cron-utils-249918159-20170704-112646_bugonly-firstCommit](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/tree/repairnator-repairnator-experiments-jmrozanec-cron-utils-249918159-20170704-112646_bugonly-firstCommit)

- **Information about the failure**:

| Failure type | Failing test case | Changed file by AstorJKali |
|--------------|-------------------|----------------------------|
| java.lang.AssertionError | [Issue215Test.java]() | []()|

### Mistahmilla-SudokuSolver-372495757-20180428-211304

**Branch associated with the failure**: [repairnator-repairnator-experiments-Mistahmilla-SudokuSolver-372495757-20180428-211304-firstCommit](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/tree/repairnator-repairnator-experiments-Mistahmilla-SudokuSolver-372495757-20180428-211304-firstCommit)

- **Information about the failure**:

| Failure type | Failing test case | Changed file by AstorJKali |
|--------------|-------------------|----------------------------|
| java.lang.AssertionError | [NakedSubsetTest.java]() | []()|

### MPI-ExperimentGroup-ExperimentTemplate-384776966-20180528-170150

**Branch associated with the failure**: [repairnator-repairnator-experiments-MPI-ExperimentGroup-ExperimentTemplate-384776966-20180528-170150-firstCommit](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/tree/repairnator-repairnator-experiments-MPI-ExperimentGroup-ExperimentTemplate-384776966-20180528-170150-firstCommit)

- **Information about the failure**:

| Failure type | Failing test case | Changed file by AstorJKali |
|--------------|-------------------|----------------------------|
| java.lang.AssertionError | [AudioAsStimuliProviderTest.java]() | []()|

### nablarch-nablarch-example-http-messaging-361036711-20180402-045142

**Branch associated with the failure**: [repairnator-repairnator-experiments-nablarch-nablarch-example-http-messaging-361036711-20180402-045142-firstCommit](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/tree/repairnator-repairnator-experiments-nablarch-nablarch-example-http-messaging-361036711-20180402-045142-firstCommit)

- **Information about the failure**:

| Failure type | Failing test case | Changed file by AstorJKali |
|--------------|-------------------|----------------------------|
| org.junit.ComparisonFailure | [ProjectSaveActionRequestTest.java]() | []()|

### OpenFeign-feign-413754623-20180808-215547

**Branch associated with the failure**: [repairnator-repairnator-experiments-OpenFeign-feign-413754623-20180808-215547-firstCommit](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/tree/repairnator-repairnator-experiments-OpenFeign-feign-413754623-20180808-215547-firstCommit)

- **Information about the failure**:

| Failure type | Failing test case | Changed file by AstorJKali |
|--------------|-------------------|----------------------------|
| java.lang.AssertionError | [JAXBCodecTest.java]() | []()|

### opentracing-contrib-java-hazelcast-390335750-20180610-103253

**Branch associated with the failure**: [repairnator-repairnator-experiments-opentracing-contrib-java-hazelcast-390335750-20180610-103253-firstCommit](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/tree/repairnator-repairnator-experiments-opentracing-contrib-java-hazelcast-390335750-20180610-103253-firstCommit)

- **Information about the failure**:

| Failure type | Failing test case | Changed file by AstorJKali |
|--------------|-------------------|----------------------------|
| java.lang.AssertionError | [TracingTest.java]() | []()|

### pac4j-pac4j-322406277-20171228-030236_bugonly

**Branch associated with the failure**: [repairnator-repairnator-experiments-pac4j-pac4j-322406277-20171228-030236_bugonly-firstCommit](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/tree/repairnator-repairnator-experiments-pac4j-pac4j-322406277-20171228-030236_bugonly-firstCommit)

- **Information about the failure**:

| Failure type | Failing test case | Changed file by AstorJKali |
|--------------|-------------------|----------------------------|
| java.lang.AssertionError | [StringConverterTests.java]() | []()|

### ryhita-gilded-rose-349620528-20180306-041741

**Branch associated with the failure**: [repairnator-repairnator-experiments-ryhita-gilded-rose-349620528-20180306-041741-firstCommit](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/tree/repairnator-repairnator-experiments-ryhita-gilded-rose-349620528-20180306-041741-firstCommit)

- **Information about the failure**:

| Failure type | Failing test case | Changed file by AstorJKali |
|--------------|-------------------|----------------------------|
| java.lang.AssertionError | [ItemTest.java]() | []()|

### swissquote-carnotzet-351211949-20180309-095950

**Branch associated with the failure**: [repairnator-repairnator-experiments-swissquote-carnotzet-351211949-20180309-095950-firstCommit](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/tree/repairnator-repairnator-experiments-swissquote-carnotzet-351211949-20180309-095950-firstCommit)

- **Information about the failure**:

| Failure type | Failing test case | Changed file by AstorJKali |
|--------------|-------------------|----------------------------|
| java.lang.AssertionError | [DefaultCommandRunnerTest.java]() | []()|

### timmolter-XChange-301755888-20171114-043126_bugonly

**Branch associated with the failure**: [repairnator-repairnator-experiments-timmolter-XChange-301755888-20171114-043126_bugonly-firstCommit](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/tree/repairnator-repairnator-experiments-timmolter-XChange-301755888-20171114-043126_bugonly-firstCommit)

- **Information about the failure**:

| Failure type | Failing test case | Changed file by AstorJKali |
|--------------|-------------------|----------------------------|
| java.lang.AssertionError | [KrakenAdaptersTest.java]() | []()|

### usgs-volcano-core-408694507-20180726-231401

**Branch associated with the failure**: [repairnator-repairnator-experiments-usgs-volcano-core-408694507-20180726-231401-firstCommit](https://github.com/repairnator/repairnator-experiments-jkali-one-failing-test-case/tree/repairnator-repairnator-experiments-usgs-volcano-core-408694507-20180726-231401-firstCommit)

- **Information about the failure**:

| Failure type | Failing test case | Changed file by AstorJKali |
|--------------|-------------------|----------------------------|
| java.lang.AssertionError | [ScnlParserTest.java]() | []()|
