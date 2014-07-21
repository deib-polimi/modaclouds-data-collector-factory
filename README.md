The MODAClouds Data Collector Factory
=============================

In the context of MODAClouds European project (www.modaclouds.eu), Politecnico was
one of the partners involved in the development of the Monitoring Platform.

The data collector factory is a java library for building new data collector.

Please refer to deliverable [D6.3.2](http://www.modaclouds.eu/publications/public-deliverables/) 
to better understand the role of data collectors.

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
	<version>0.1</version>
</dependency>
```

Create your data collector factory by extending class DataCollectorFactory.

Have a look at the [javadoc](http://deib-polimi.github.io/modaclouds-data-collector-factory) 
for further details.
