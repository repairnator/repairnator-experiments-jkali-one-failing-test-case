# Saga Pack Design
[![ZH doc](https://img.shields.io/badge/document-中文-blue.svg)](design_zh.md)
## Overview
Pack contains two components: *alpha* and *omega*. Alpha is the pack leader and backed by database to make sure transaction events stored permanently while omega is the pack worker and embedded inside services to intercept transaction invocation and report events to alpha.

![Pack Architecture](static_files/pack.png)

## Omega Internal
Omega plays as an embedded agent inside services. When a service request arrives, omega intercepts its header and retrieve the global transaction id as its global transaction id (Saga event id) and retrieve the local transaction id as its parent transaction id. In pre-process phase, a transaction started event will be recorded in alpha. In post-process phase, a transaction ended event will be recorded in alpha to mark the end of the sub-transaction.

![Omega Internal](static_files/omega_internal.png)

## Inter-Service Communication
The process of Inter-Service Communication is similar to [Zipkin](https://github.com/openzipkin/zipkin)'s. In the producer side, omega intercepts the transaction ids from request to retrieve the transaction context. In the consumer side, omega inject the global transaction ids into request to pass the transaction context. Sub-transactions can chain as a single global transaction by co-operating producers and consumers.

![Inter-Service Communication](static_files/inter-service_communication.png)

## Workflow
### Successful Scenario
In a successful scenario, all started events will have a corresponding ended event.

![Successful Scenario](static_files/successful_scenario.png)

### Exception Scenario
In an exception scenario, omega inside the abnormal service will report an aborted event to alpha. Apha will then send compensate commands to the completed events within the global transaction to make sure all sub-transactions are either completed or rollbacked.

![Exception Scenario](static_files/exception_scenario.png)

### Timeout Scenario
In timeout scenario, timeouted events will be detected by alpha's period scanner, the corresponding global transaction will be abort at the same time.

![Timeout Scenario](static_files/timeout_scenario.png)
