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
package it.polimi.modaclouds.monitoring.dcfactory.connectors;

import it.polimi.modaclouds.monitoring.dcfactory.Util;
import it.polimi.modaclouds.monitoring.kb.api.FusekiKBAPI;
import it.polimi.modaclouds.qos_models.monitoring_ontology.DataCollector;
import it.polimi.modaclouds.qos_models.monitoring_ontology.MO;
import it.polimi.modaclouds.qos_models.monitoring_ontology.Parameter;
import it.polimi.modaclouds.qos_models.monitoring_ontology.Vocabulary;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FusekiKBHandler implements KBHandler {

	private FusekiKBAPI fusekiKBAPI;

	public FusekiKBHandler(String knowledgeBaseURL) {
		fusekiKBAPI = new FusekiKBAPI(knowledgeBaseURL);
		fusekiKBAPI.setURIBase(MO.URI);
		fusekiKBAPI.setURIPrefix(MO.prefix);
	}

	@Override
	public Set<DataCollector> getDataCollectors(
			Set<String> monitoredResourcesIds) {
		Set<DataCollector> dataCollectors = new HashSet<DataCollector>();
		for (String monitoredResourceId : monitoredResourcesIds) {
			Set<DataCollector> dcs = fusekiKBAPI.getAll(DataCollector.class,
					Vocabulary.monitoredResourceId, monitoredResourceId);
			if (dcs == null)
				dcs = new HashSet<DataCollector>();
			dataCollectors.addAll(dcs);
		}
		return dataCollectors;
	}

	@Override
	public Map<String, String> getParameters(DataCollector dc) {
		Map<String, String> parameters = new HashMap<String, String>();
		for (URI pURI : Util.nullableIterable(dc.getParameters())) {
			Parameter p = (Parameter) fusekiKBAPI.getEntityByURI(pURI);
			parameters.put(p.getName(), p.getValue());
		}
		return parameters;
	}

}
