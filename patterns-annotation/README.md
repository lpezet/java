Patterns Annotation
===================

Provides Java annotation to use patterns provided in [Java Patterns](https://github.com/lpezet/java/tree/master/patterns).


Documentation
-------------

### @Retry
This annotation wraps a method call using a specified Retry Strategy.
Under certain conditions, the method call will be retried.


Attribute | Description
--------- | -----------
**condition** | Specifies the IRetryCondition class to use
**maxExecutions** | Specifies the maximum number of times the method should be called (3 calls = 2 retries).
**exception** | Specifies which Exception will trigger a retry. Default is java.lang.Exception.
**scaleFactor** | Used in the default backoff strategy.
**backoff** | Specifies the IBackoffStrategy to use when waiting for the next retry. Default is ExponentialBackoffStrategy.
**maxBackoffInMillis** | Maximum time to wait in between retries. Default is 2s.

	

### @ShortCircuit
This annotation wraps a method call with a CircuitBreak.
Upon certain conditions, the method call will be short-circuited (i.e. not called), then called again upon other conditions.

Attribute | Description
--------- | -----------
**triper** | Exception that would trip the circuit breaker. Default is java.lang.Exception.
**exceptionsToTrip** | Number of exceptions to trip the circuit breaker. Default is 3.
**condition** | ICircuitBreakerCondition class to use to determine wehter or not the breaker is tripped. Default is BaseCircuitBreakerCondition.
**openHandler** | ICircuitBreakerHandler class to use to handle the breaker when open. Default is SingleTryCircuitBreakerStrategy.

### @Supervise
This annotation wraps a method call with a Supervisor.
This object will monitor the time of execution of the method, and throws a TimeoutException if not completed within a certain time frame.


Attribute | Description
--------- | -----------
**executeServiceFactory** | IExecutorServiceFactory class to use to create ExecutorService. Default is ExecutorService (creating fixed threads pool).
**threads** | If using the default IExecutorServiceFactory, set the number of threads in the fixed pool.
**timeout** | Time to wait for method call to complete.
**timeunit** | Time unit of *timeout*.

	
## Combing multiple annotations

It's possible to combine multiple annotations like so:

```java
@ShortCircuit(exceptionsToTrip=1)
@Retry(exception=TimeoutException.class)
@Supervise(timeout=100, timeunit=TimeUnit.MILLISECONDS)
public boolean doSomething() throws Exception {
	// do something...
	return oResult;
}
```

The order is the order in which they are defined here.
In the previous example, it means:
* @ShortCircuit wraps @Retry
* @Retry wraps @Supervise
* @Supervise wraps *doSomething()*

In other words:
* doSomething() will be monitored by a Supervisor, throwing a TimeoutException if it takes more than 100 millis to complete.
* Retry will catch TimeoutException and retry 3 times (default). If it still fails, exception is re-thrown.
* ShortCircuit will catch exceptions from Retry and trip at the first one raised. After 500ms (default), CircuitBreaker will switch to half-open. Upon another method call, CB will call the method (a single try among many thread if any) and upon success will reset the breaker.


Installation
------------

#### pom.xml

```xml
<repositories>
	<repository>
		<id>lpezet-snapshot</id>
		<url>https://repository-lpezet.forge.cloudbees.com/snapshot/</url>
		<name>LPezet Snapshot Repo</name>
		<snapshots>
			<enabled>true</enabled>
		</snapshots>
		<releases>
			<enabled>false</enabled>
		</releases>
	</repository>
	<repository>
		<id>lpezet-release</id>
		<url>https://repository-lpezet.forge.cloudbees.com/release/</url>
		<name>LPezet Snapshot Repo</name>
		<snapshots>
			<enabled>false</enabled>
		</snapshots>
		<releases>
			<enabled>true</enabled>
		</releases>
	</repository>
</repositories>
```

License
-------

See [LICENSE](src/main/resources/META-INF/LICENSE) file.
