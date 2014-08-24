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
	public Set<DCMetaData> getDataCollectorsMetaData(
			Set<String> monitoredResourcesIds) {
		Set<DCMetaData> myDataCollectorsMetaData = new HashSet<DCMetaData>();
		Set<DCMetaData> allDCMetaData = new HashSet<DCMetaData>();
		try {
			allDCMetaData = fusekiKBAPI.getAll(DCMetaData.class);
		} catch (DeserializationException e) {
			logger.error(
					"Error while retriving data collectors meta data from KB",
					e);
		}
		if (allDCMetaData != null) {
			for (DCMetaData dcMetaData : allDCMetaData) {
				if (!Collections.disjoint(dcMetaData.getMonitoredResourcesIds(), monitoredResourcesIds))
					myDataCollectorsMetaData.add(dcMetaData);
			}
		}
		return myDataCollectorsMetaData;
	}
}
