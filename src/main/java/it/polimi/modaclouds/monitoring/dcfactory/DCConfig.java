/**
 * Copyright 2014 deib-polimi
 * Contact: deib-polimi <marco.miglierina@polimi.it>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package it.polimi.modaclouds.monitoring.dcfactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DCConfig{

//	private String id;
	private String monitoredMetric;
	private Map<String, String> parameters = new HashMap<String, String>();
	private Set<String> monitoredResourcesClasses = new HashSet<String>();
	private Set<String> monitoredResourcesTypes = new HashSet<String>();
	private Set<String> monitoredResourcesIds = new HashSet<String>();
//	private String monitoringRuleId;
	
//	public String getMonitoringRuleId() {
//		return monitoringRuleId;
//	}
//
//	public void setMonitoringRuleId(String monitoringRuleId) {
//		this.monitoringRuleId = monitoringRuleId;
//	}

	public DCConfig() {
//		id = UUID.randomUUID().toString();
	}
	
	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public void addParameter(String key, String value) {
		parameters.put(key, value);
	}

	public String getMonitoredMetric() {
		return monitoredMetric;
	}

	public void setMonitoredMetric(String monitoredMetric) {
		this.monitoredMetric = monitoredMetric;
	}

	public Set<String> getMonitoredResourcesIds() {
		return monitoredResourcesIds;
	}

	public void setMonitoredResourcesIds(Set<String> monitoredResourcesIds) {
		this.monitoredResourcesIds = monitoredResourcesIds;
	}

	public void addMonitoredResourceId(String monitoredResourceId) {
		monitoredResourcesIds.add(monitoredResourceId);
	}
	
	public void addMonitoredResourceType(String monitoredResourceType) {
		monitoredResourcesTypes.add(monitoredResourceType);
	}
	
	public void addMonitoredResourceClass(String monitoredResourceClass) {
		monitoredResourcesClasses.add(monitoredResourceClass);
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((monitoredMetric == null) ? 0 : monitoredMetric.hashCode());
		result = prime
				* result
				+ ((monitoredResourcesClasses == null) ? 0
						: monitoredResourcesClasses.hashCode());
		result = prime
				* result
				+ ((monitoredResourcesIds == null) ? 0 : monitoredResourcesIds
						.hashCode());
		result = prime
				* result
				+ ((monitoredResourcesTypes == null) ? 0
						: monitoredResourcesTypes.hashCode());
		result = prime * result
				+ ((parameters == null) ? 0 : parameters.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DCConfig other = (DCConfig) obj;
		if (monitoredMetric == null) {
			if (other.monitoredMetric != null)
				return false;
		} else if (!monitoredMetric.equals(other.monitoredMetric))
			return false;
		if (monitoredResourcesClasses == null) {
			if (other.monitoredResourcesClasses != null)
				return false;
		} else if (!monitoredResourcesClasses
				.equals(other.monitoredResourcesClasses))
			return false;
		if (monitoredResourcesIds == null) {
			if (other.monitoredResourcesIds != null)
				return false;
		} else if (!monitoredResourcesIds.equals(other.monitoredResourcesIds))
			return false;
		if (monitoredResourcesTypes == null) {
			if (other.monitoredResourcesTypes != null)
				return false;
		} else if (!monitoredResourcesTypes
				.equals(other.monitoredResourcesTypes))
			return false;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (!parameters.equals(other.parameters))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DCMetaData [monitoredMetric=" + monitoredMetric
				+ ", parameters=" + parameters + ", monitoredResourcesClasses="
				+ monitoredResourcesClasses + ", monitoredResourcesTypes="
				+ monitoredResourcesTypes + ", monitoredResourcesIds="
				+ monitoredResourcesIds + "]";
	}

//	public String getId() {
//		return id;
//	}
//
//	public void setId(String id) {
//		this.id = id;
//	}

	public Set<String> getMonitoredResourcesClasses() {
		return monitoredResourcesClasses;
	}

	public void setMonitoredResourcesClasses(
			Set<String> monitoredResourcesClasses) {
		this.monitoredResourcesClasses = monitoredResourcesClasses;
	}

	public Set<String> getMonitoredResourcesTypes() {
		return monitoredResourcesTypes;
	}

	public void setMonitoredResourcesTypes(Set<String> monitoredResourcesTypes) {
		this.monitoredResourcesTypes = monitoredResourcesTypes;
	}

}
