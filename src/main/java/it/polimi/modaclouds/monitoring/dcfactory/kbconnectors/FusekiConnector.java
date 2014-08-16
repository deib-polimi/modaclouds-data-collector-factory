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

import it.polimi.modaclouds.monitoring.kb.api.FusekiKBAPI;

import java.util.HashSet;
import java.util.Set;

public class FusekiConnector implements KBConnector {

	private FusekiKBAPI fusekiKBAPI;

	public FusekiConnector(String knowledgeBaseURL) {
		fusekiKBAPI = new FusekiKBAPI(knowledgeBaseURL);
	}

	@Override
	public Set<DCMetaData> getDataCollectorsMetaData(
			Set<String> monitoredResourcesIds) {
		Set<DCMetaData> dataCollectorsMetaData = new HashSet<DCMetaData>();
		for (String monitoredResourceId : monitoredResourcesIds) {
			Set<? extends DCMetaData> dcs = fusekiKBAPI.getAll(
					FusekiDCMetaData.class, FusekiVocabulary.monitoredResourcesIds,
					monitoredResourceId);
			if (dcs == null)
				dcs = new HashSet<DCMetaData>();
			dataCollectorsMetaData.addAll(dcs);
		}
		return dataCollectorsMetaData;
	}

}
