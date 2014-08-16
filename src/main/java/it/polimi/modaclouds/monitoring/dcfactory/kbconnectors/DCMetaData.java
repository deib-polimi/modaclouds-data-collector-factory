package it.polimi.modaclouds.monitoring.dcfactory.kbconnectors;

import java.util.Map;
import java.util.Set;

public interface DCMetaData {

	Set<String> getMonitoredResourcesIds();

	String getMonitoredMetric();
	
	Map<String,String> getParameters();

}
