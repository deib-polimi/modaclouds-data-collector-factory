The MODAClouds Data Collector Factory
=============================

In the context of MODAClouds European project (www.modaclouds.eu), Politecnico was
one of the partners involved in the development of the Monitoring Platform.

The data collector factory is a java library for building new data collector.

Please refer to deliverable [D6.3.2](http://www.modaclouds.eu/publications/public-deliverables/) 
to better understand the role of data collectors.

Refer to the [Monitoring Platform Wiki](https://github.com/deib-polimi/modaclouds-monitoring-manager/wiki) for installation and usage of the whole platform.

## Usage

An example of data collector built using this library is available
[here](https://github.com/deib-polimi/modaclouds-app-level-dc).

Add the following repositories to your pom.xml:

```xml
<repositories>
	<repository>
		<id>deib-polimi-releases</id>
		<url>https://github.com/deib-polimi/deib-polimi-mvn-repo/raw/master/releases</url>
	</repository>
	<repository>
		<id>deib-polimi-snapshots</id>
		<url>https://github.com/deib-polimi/deib-polimi-mvn-repo/raw/master/snapshots</url>
	</repository>
</repositories>
```

and the following dependency:

```xml
<dependency>
	<groupId>it.polimi.modaclouds.monitoring</groupId>
	<artifactId>data-collector-factory</artifactId>
	<version>0.2.2</version>
</dependency>
```

Create your data collector factory by extending class DataCollectorFactory.

Have a look at the [javadoc](http://deib-polimi.github.io/modaclouds-data-collector-factory) 
for further details.

## Change List

v0.3.3:
* fixed a problem that caused the KB to be overloaded with requests
* updated to [knowledge-base-api 2.3.1](https://github.com/deib-polimi/modaclouds-knowledge-base-api/releases/tag/v2.3.1)
* updated to [qos-models 2.4.1](https://github.com/deib-polimi/modaclouds-qos-models/releases/tag/v2.4.1)

v0.3.1:
* bug fix

v0.3:
* updated to knowledge-base-api 2.2
* updated to qos-models 2.2
* refactoring (API changed)
* now using a buffer for sending monitoring data asynchronously: when method sendAsyncMonitoringDatum is called the datum is added to the buffer and a 1 second timer is started (unless already running); when timeout occurs all data in the buffer are sent and the buffer is emptied.
