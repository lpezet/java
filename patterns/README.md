Patterns
========

Collection of design and implementations of diverse software patterns.
Some are variations of well-known ones (e.g. Command Pattern), some are much-less-known ones (e.g. Retry Pattern, Circuit Breaker Pattern) and some are just way out there (Activity Pattern).


Documentation
-------------

### Command&lt;T&gt; Pattern
This is variation of the famous behavorial Command Pattern from GoF.
This pattern have commands return a result. This provide some extra benefits over the standard pattern.
The [AsyncCommand] for example allow a client to execute a command and choose how to get the results in an async fashion: either through a "pull" (*Future*) or through a "push" (*Callback*).

For more info, see [Command Patterns](http://mezzetin.blogspot.com/2014/03/execute-retry-trip.html).

### Worker Pattern
This is (yet) another variation of the standard Command Pattern. It's a Command with a parameter and a result.
This design allows for:
- more re-usability over the Command&lt;T&gt; Pattern.
- and chaining.

For more info, see [Worker Pattern](http://mezzetin.blogspot.com/2014/04/worker-pattern.html).

### Retry Pattern
This pattern provides a flexible approach to retrying execution of code.
A Retry is composed of a **condition** (i.e. when should we retry) and a **backoff strategy** (i.e. pause between retries).
This was inspired by the retry logic used in AWS Java SDK.
Both a Command and Activity implementation are provided.

### Circuit Breaker Pattern
This pattern can prevent execution of code under certain circumstances (like a circuit breaker prevent current to pass when tripped).
This pattern is composed of a **circuit breaker** and an **open-strategy** to handle the case when the circuit breaker is no longer closed.
Both a Command and Activity implementation are provided.

For more info, see [Command Patterns](http://mezzetin.blogspot.com/2014/03/execute-retry-trip.html) and [Circuit Breaker Pattern](http://msdn.microsoft.com/en-us/library/dn589784.aspx).

### Supervisor Pattern
This pattern provides a very simple way to monitor the time of the execution of certain code.
This can be used to fail fast for example, in cases where hanging or significant-and-unpredictable latency may happen. 
Both a Command and Activity implementation are provided.

For more info, see [Command Patterns](http://mezzetin.blogspot.com/2014/03/execute-retry-trip.html).


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
