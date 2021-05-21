# Process with Kafka

## POC that reproduces issue in Kogito 1.6.0.Final

This PoC was created on top of the Travelers example for kafka and Quarkus.

Workflow BPMN: [src/main/java/resources/ugc.bpmn](src/main/java/resources/ugc.bpmn)

This Poc sends events to a `ugc-request` topic in kafka and triggers an error in JBPM framework caused by a deprecated api.

# Steps to reproduce

1. Bring up a kafka instance with docker compose:
```shell
docker-compose up -d -f src/main/docker/docker-compose.yml
```

2. Compile the application and run in development mode:
```shell
mvn clean compile quarkus:dev
```

3. Send the following cloudevent payload to the `ugc-requests` topic:
```json
{
	"id": "2e7037a1-2c79-4ac8-9861-4991c4116b6c",
	"specversion" : "1.0",
	"source": "ugc-requests",
	"type": "ugc-event",
	"data": {
		"context": "myapp",
		"ownerId": "hecsalazar",
		"ownerType": "admin",
		"status": "pending",
		"ugcId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
		"ugcProcessName": "ugc-moderation",
		"ugcType": "image/jpeg",
		"sourceUri": "http://somewhere.com/image.jpeg"
	}
}
```

This will trigger the creation of a process instance for this event and should throw an exception calling a deprecated api in jbpm framework:
```
2021-05-21 11:30:19,478 ERROR [io.qua.mut.run.MutinyInfrastructure] (vert.x-eventloop-thread-0) Mutiny had to drop the following exception: java.lang.UnsupportedOperationException
        at org.jbpm.process.instance.impl.ProcessInstanceImpl.getId(ProcessInstanceImpl.java:60)
        at org.jbpm.process.instance.context.variable.VariableScopeInstance.getVariable(VariableScopeInstance.java:57)
        at org.jbpm.process.instance.context.variable.VariableScopeInstance.setVariable(VariableScopeInstance.java:87)
        at org.jbpm.process.instance.context.variable.VariableScopeInstance.setVariable(VariableScopeInstance.java:79)
        at org.jbpm.process.instance.LightProcessRuntimeContext.setupParameters(LightProcessRuntimeContext.java:101)
        at org.jbpm.process.instance.LightProcessRuntime.createProcessInstance(LightProcessRuntime.java:191)
        at org.jbpm.process.instance.LightProcessRuntime.createProcessInstance(LightProcessRuntime.java:182)
        at org.jbpm.process.instance.LightProcessRuntime.createProcessInstance(LightProcessRuntime.java:64)
        at org.kie.kogito.process.impl.AbstractProcessInstance.<init>(AbstractProcessInstance.java:102)
        at org.kie.kogito.process.impl.AbstractProcessInstance.<init>(AbstractProcessInstance.java:90)
        at org.acme.ugc.moderation.UgcmoderationProcessInstance.<init>(UgcmoderationProcessInstance.java:6)
        at org.acme.ugc.moderation.UgcmoderationProcess.createInstance(UgcmoderationProcess.java:30)
        at org.acme.ugc.moderation.UgcmoderationProcess.createInstance(UgcmoderationProcess.java:1)
        at org.acme.ugc.moderation.UgcmoderationProcess_ClientProxy.createInstance(UgcmoderationProcess_ClientProxy.zig:684)
        at org.kie.kogito.event.impl.CloudEventConsumer.lambda$consume$0(CloudEventConsumer.java:82)
        at org.kie.kogito.services.uow.UnitOfWorkExecutor.executeInUnitOfWork(UnitOfWorkExecutor.java:33)
        at org.kie.kogito.event.impl.CloudEventConsumer.consume(CloudEventConsumer.java:64)
        at org.kie.kogito.services.event.impl.AbstractMessageConsumer.consume(AbstractMessageConsumer.java:85)
        at io.smallrye.mutiny.subscription.Subscribers$CallbackBasedSubscriber.onItem(Subscribers.java:67)
        at io.smallrye.mutiny.subscription.MultiSubscriber.onNext(MultiSubscriber.java:61)
        at io.smallrye.mutiny.context.ContextPropagationMultiInterceptor$ContextPropagationSubscriber.lambda$onNext$1(ContextPropagationMultiInterceptor.java:89)
        at io.smallrye.context.SmallRyeThreadContext.lambda$withContext$1(SmallRyeThreadContext.java:530)
        at io.smallrye.mutiny.context.ContextPropagationMultiInterceptor$ContextPropagationSubscriber.onNext(ContextPropagationMultiInterceptor.java:89)
        at io.smallrye.mutiny.helpers.HalfSerializer.onNext(HalfSerializer.java:31)
        at io.smallrye.mutiny.helpers.StrictMultiSubscriber.onItem(StrictMultiSubscriber.java:83)
        at io.smallrye.mutiny.subscription.MultiSubscriber.onNext(MultiSubscriber.java:61)
        at io.smallrye.mutiny.subscription.SafeSubscriber.onNext(SafeSubscriber.java:98)
        at io.smallrye.mutiny.helpers.HalfSerializer.onNext(HalfSerializer.java:31)
        at io.smallrye.mutiny.helpers.StrictMultiSubscriber.onItem(StrictMultiSubscriber.java:83)
        at io.smallrye.mutiny.subscription.MultiSubscriber.onNext(MultiSubscriber.java:61)
        at io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor$BroadcastSubscription.onNext(BroadcastProcessor.java:209)
        at io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor.onNext(BroadcastProcessor.java:138)
        at org.kie.kogito.addon.cloudevents.quarkus.QuarkusCloudEventPublisher.produce(QuarkusCloudEventPublisher.java:90)
        at org.kie.kogito.addon.cloudevents.quarkus.QuarkusCloudEventPublisher.lambda$onEvent$1(QuarkusCloudEventPublisher.java:78)
        at java.base/java.util.concurrent.CompletableFuture$UniApply.tryFire(CompletableFuture.java:642)
        at java.base/java.util.concurrent.CompletableFuture.postComplete(CompletableFuture.java:506)
        at java.base/java.util.concurrent.CompletableFuture.complete(CompletableFuture.java:2137)
        at io.smallrye.reactive.messaging.kafka.commit.KafkaThrottledLatestProcessedCommit.lambda$handle$6(KafkaThrottledLatestProcessedCommit.java:274)
        at io.smallrye.reactive.messaging.kafka.commit.ContextHolder.lambda$runOnContext$0(ContextHolder.java:49)
        at io.vertx.core.impl.ContextImpl.executeTask(ContextImpl.java:366)
        at io.vertx.core.impl.EventLoopContext.lambda$executeAsync$0(EventLoopContext.java:38)
        at io.netty.util.concurrent.AbstractEventExecutor.safeExecute(AbstractEventExecutor.java:164)
        at io.netty.util.concurrent.SingleThreadEventExecutor.runAllTasks(SingleThreadEventExecutor.java:472)
        at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:500)
        at io.netty.util.concurrent.SingleThreadEventExecutor$4.run(SingleThreadEventExecutor.java:989)
        at io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74)
        at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
        at java.base/java.lang.Thread.run(Thread.java:832)

```
## Description

A quickstart project that deals with traveller processing carried by rules. It illustrates
how easy it is to make the Kogito processes and rules to work with Apache Kafka

This example shows

* consuming events from a Kafka topic and for each event start new process instance
* each process instance is expecting a traveller information in JSON format
* traveller is then processed by rules and based on the outcome of the processing (processed or not) traveller is
	* if successfully processed traveller information is logged and then updated information is send to another Kafka topic
	* if not processed traveller info is logged and then process instance finishes without sending reply to Kafka topic


<p align="center"><img width=75% height=50% src="docs/images/process.png"></p>

* Diagram Properties (top)
<p align="center"><img src="docs/images/diagramProperties.png"></p>

* Diagram Properties (bottom)
<p align="center"><img src="docs/images/diagramProperties2.png"></p>

* Diagram Properties (process variables)
<p align="center"><img src="docs/images/diagramProperties3.png"></p>

* Start Message
<p align="center"><img src="docs/images/startMessage.png"></p>

* Start Message (Assignments)
<p align="center"><img src="docs/images/startMessageAssignments.png"></p>

* Process Traveler Business Rule (top)
<p align="center"><img src="docs/images/processTravelerBusinessRule.png"></p>

* Process Traveler Business Rule (bottom)
<p align="center"><img src="docs/images/processTravelerBusinessRule2.png"></p>

* Process Traveler Business Rule (Assignments)
<p align="center"><img src="docs/images/processTravelerBusinessRuleAssignments.png"></p>

* Process Traveler Gateway
<p align="center"><img src="docs/images/processedTravelerGateway.png"></p>

* Process Traveler Gateway Yes Connector
<p align="center"><img src="docs/images/processedTravelerYesConnector.png"></p>

* Process Traveler Gateway No Connector
<p align="center"><img src="docs/images/processedTravelerNoConnector.png"></p>

* Log Traveler Script Task
<p align="center"><img src="docs/images/logTravelerScriptTask.png"></p>

* Skip Traveler Script Task
<p align="center"><img src="docs/images/skipTravelerScriptTask.png"></p>

* Processed Traveler End Message
<p align="center"><img src="docs/images/processedTravelerEndMessage.png"></p>

* Processed Traveler End Message (Assignments)
<p align="center"><img src="docs/images/processedTravelerEndMessageAssignments.png"></p>

* Skip Traveler End
<p align="center"><img src="docs/images/skipTraveler.png"></p>


## Infrastructure requirements

This quickstart requires an Apache Kafka to be available and by default expects it to be on default port and localhost.

* Install and Startup Kafka Server / Zookeeper

https://kafka.apache.org/quickstart

## Build and run

### Prerequisites

You will need:
  - Java 11+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.6.2+ installed

When using native image compilation, you will also need:
  - GraalVM 19.3+ installed
  - Environment variable GRAALVM_HOME set accordingly
  - GraalVM native image needs as well native-image extension: https://www.graalvm.org/docs/reference-manual/native-image/
  - Note that GraalVM native image compilation typically requires other packages (glibc-devel, zlib-devel and gcc) to be installed too, please refer to GraalVM installation documentation for more details.

### Compile and Run in Local Dev Mode

```sh
mvn clean compile quarkus:dev
```

NOTE: With dev mode of Quarkus you can take advantage of hot reload for business assets like processes, rules, decision tables and java code. No need to redeploy or restart your running application.

### Package and Run in JVM mode

```sh
mvn clean package
java -jar target/quarkus-app/quarkus-run.jar
```

or on windows

```sh
mvn clean package
java -jar target\quarkus-app\quarkus-run.jar
```

### Package and Run using Local Native Image
Note that this requires GRAALVM_HOME to point to a valid GraalVM installation

```
mvn clean package -Pnative
```
  
To run the generated native executable, generated in `target/`, execute

```
./target/process-kafka-quickstart-quarkus-runner
```

### OpenAPI (Swagger) documentation
[Specification at swagger.io](https://swagger.io/docs/specification/about/)

You can take a look at the [OpenAPI definition](http://localhost:8080/openapi?format=json) - automatically generated and included in this service - to determine all available operations exposed by this service. For easy readability you can visualize the OpenAPI definition file using a UI tool like for example available [Swagger UI](https://editor.swagger.io).

In addition, various clients to interact with this service can be easily generated using this OpenAPI definition.

When running in either Quarkus Development or Native mode, we also leverage the [Quarkus OpenAPI extension](https://quarkus.io/guides/openapi-swaggerui#use-swagger-ui-for-development) that exposes [Swagger UI](http://localhost:8080/swagger-ui/) that you can use to look at available REST endpoints and send test requests.

### Use the application

To make use of this application it is as simple as putting a message on `travellers` topic with following content  (cloud event format)

* To examine ProcessedTravellers topic and verify upcoming messages will be processed

Execute in a separate terminal session

```sh
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic processed-ugc-requests
```

* Send message that should be processed to Topic

```sh
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic travellers
```

Content (cloud event format)

```json
{
  "specversion": "0.3",
  "id": "21627e26-31eb-43e7-8343-92a696fd96b1",
  "source": "",
  "type": "TravellersMessageDataEvent_3",
  "time": "2019-10-01T12:02:23.812262+02:00[Europe/Warsaw]",
  "data": {
	"firstName" : "Jan",
	"lastName" : "Kowalski",
	"email" : "jan.kowalski@example.com",
	"nationality" : "Polish"
	}
}
```
One liner

```json
{"specversion": "0.3","id": "21627e26-31eb-43e7-8343-92a696fd96b1","source": "","type": "travellers", "time": "2019-10-01T12:02:23.812262+02:00[Europe/Warsaw]","data": { "firstName" : "Jan", "lastName" : "Kowalski", "email" : "jan.kowalski@example.com", "nationality" : "Polish"}}
```


this will then trigger the successful processing of the traveller and put another message on `processedtravellers` topic with following content (cloud event format)

```json
{
  "specversion": "0.3",
  "id": "86f69dd6-7145-4188-aeaa-e44622eeec86",
  "source": "",
  "type": "TravellersMessageDataEvent_3",
  "time": "2019-10-03T16:22:40.373523+02:00[Europe/Warsaw]",
  "data": {
    "firstName": "Jan",
    "lastName": "Kowalski",
    "email": "jan.kowalski@example.com",
    "nationality": "Polish",
    "processed": true
  },
  "kogitoProcessinstanceId": "4fb091c2-82f7-4655-8687-245a4ab07483",
  "kogitoParentProcessinstanceId": null,
  "kogitoRootProcessinstanceId": null,
  "kogitoProcessId": "Travellers",
  "kogitoRootProcessId": null,
  "kogitoProcessinstanceState": "1",
  "kogitoReferenceId": null
}
```

there are bunch of extension attributes that starts with `kogito` to provide some context of the execution and the event producer.

To take the other path of the process put following message on `travellers` topic

* Send Message that should be skipped to Topic

```sh
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic travellers
```

With the following content (Cloud Event Format)

```json
{
  "specversion": "0.3",
  "id": "31627e26-31eb-43e7-8343-92a696fd96b1",
  "source": "",
  "type": "travellers",
  "time": "2019-10-01T12:02:23.812262+02:00[Europe/Warsaw]",
  "data": {
	"firstName" : "John",
	"lastName" : "Doe",
    "email" : "john.doe@example.com",
    "nationality" : "American"
	}
}
```

One Liner

```json
{"specversion": "0.3","id": "31627e26-31eb-43e7-8343-92a696fd96b1","source": "","type": "travellers", "time": "2019-10-01T12:02:23.812262+02:00[Europe/Warsaw]","data": { "firstName" : "John", "lastName" : "Doe", "email" : "john.doe@example.com", "nationality" : "American"}}
```

this will not result in message being send to `processedtravelers` topic.

## Deploying with Kogito Operator

In the [`operator`](operator) directory you'll find the custom resources needed to deploy this example on OpenShift with the [Kogito Operator](https://docs.jboss.org/kogito/release/latest/html_single/#chap_kogito-deploying-on-openshift).
