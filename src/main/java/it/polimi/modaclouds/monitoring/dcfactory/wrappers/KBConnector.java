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
package it.polimi.modaclouds.monitoring.dcfactory.wrappers;

import it.polimi.modaclouds.monitoring.dcfactory.DCConfig;
import it.polimi.modaclouds.monitoring.dcfactory.DCVocabulary;
import it.polimi.modaclouds.monitoring.kb.api.DeserializationException;
import it.polimi.modaclouds.monitoring.kb.api.FusekiKBAPI;
import it.polimi.modaclouds.qos_models.monitoring_ontology.Resource;
import it.polimi.modaclouds.qos_models.monitoring_ontology.MOVocabulary;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KBConnector {

	private Logger logger = LoggerFactory.getLogger(KBConnector.class);
	private FusekiKBAPI fusekiKBAPI;

	public KBConnector(String knowledgeBaseURL) {
		fusekiKBAPI = new FusekiKBAPI(knowledgeBaseURL);
	}

	
	public Map<String,DCConfig> getDCsConfigByMetric() {
		Set<DCConfig> dCsConfig = new HashSet<DCConfig>();
		Map<String,DCConfig> dcConfigByMetric = new HashMap<String, DCConfig>();
		try {
			dCsConfig = fusekiKBAPI.getAll(DCConfig.class,
					DCVocabulary.DATA_COLLECTORS_GRAPH_NAME);
			for (DCConfig dcConfig : dCsConfig) {
				dcConfigByMetric.put(dcConfig.getMonitoredMetric(), dcConfig);
			}
		} catch (DeserializationException e) {
			logger.error(
					"Error while retriving data collectors meta data from KB",
					e);
		}
		return dcConfigByMetric;
	}

	public Resource getResourceById(String resourceId) {
		try {
			return (Resource) fusekiKBAPI.getEntityById(resourceId,
					MOVocabulary.resourceIdParameterName,
					MOVocabulary.MODEL_GRAPH_NAME);
		} catch (Exception e) {
			logger.error("Could not retrieve resource with {} {}",
					MOVocabulary.resourceIdParameterName, resourceId, e);
			return null;
		}
	}
}
