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
package it.polimi.modaclouds.monitoring.dcfactory.kbconnectors;

import it.polimi.modaclouds.monitoring.dcfactory.DCMetaData;
import it.polimi.modaclouds.monitoring.kb.api.DeserializationException;
import it.polimi.modaclouds.monitoring.kb.api.FusekiKBAPI;
import it.polimi.modaclouds.qos_models.monitoring_ontology.Resource;
import it.polimi.modaclouds.qos_models.monitoring_ontology.Vocabulary;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FusekiConnector implements KBConnector {

	private Logger logger = LoggerFactory.getLogger(FusekiConnector.class);
	private FusekiKBAPI fusekiKBAPI;

	public FusekiConnector(String knowledgeBaseURL) {
		fusekiKBAPI = new FusekiKBAPI(knowledgeBaseURL);
	}

	@Override
	public Set<DCMetaData> getDataCollectorsMetaData() {
		// TODO should it retrieve only the required ones?
		Set<DCMetaData> dataCollectorsMetaData = new HashSet<DCMetaData>();
		try {
			dataCollectorsMetaData = fusekiKBAPI.getAll(DCMetaData.class);
		} catch (DeserializationException e) {
			logger.error(
					"Error while retriving data collectors meta data from KB",
					e);
		}
		return dataCollectorsMetaData;
	}

	@Override
	public Resource getResourceById(String resourceId) {
		try {
			return (Resource) fusekiKBAPI.getEntityById(resourceId,
					Vocabulary.resourceIdParameterName);
		} catch (Exception e) {
			logger.error("Could not retrieve resource with {} {}",
					Vocabulary.resourceIdParameterName, resourceId, e);
			return null;
		}
	}
}
