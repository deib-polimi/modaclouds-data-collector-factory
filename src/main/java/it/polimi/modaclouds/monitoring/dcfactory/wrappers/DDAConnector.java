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

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import polimi.deib.csparql_rest_api.RSP_services_csparql_API;
import polimi.deib.csparql_rest_api.exception.ServerErrorException;
import polimi.deib.csparql_rest_api.exception.StreamErrorException;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.RDF;

public class DDAConnector {

	private final Logger logger = LoggerFactory.getLogger(DDAConnector.class);

	private RSP_services_csparql_API csparql_api;
	private ExecutorService execService = Executors.newCachedThreadPool();
	private String ddaURL;

	public DDAConnector(String ddaURL) {
		this.ddaURL = ddaURL;
		if (!ddaURL.endsWith("/"))
			ddaURL += "/";
		csparql_api = new RSP_services_csparql_API(ddaURL);
	}

	public void sendSyncMonitoringData(List<String> values, String metric,
			String monitoredResourceId) {
		send(values, metric, monitoredResourceId);
	}

	public void sendSyncMonitoringDatum(String value, String metric,
			String monitoredResourceId) {
		send(Arrays.asList(new String[] { value }), metric.toLowerCase(),
				monitoredResourceId);
	}

	public void sendAsyncMonitoringDatum(final String value,
			final String metric, final String monitoredResourceId) {
		execService.execute(new Runnable() {
			@Override
			public void run() {
				send(Arrays.asList(new String[] { value }),
						metric.toLowerCase(), monitoredResourceId);
			}
		});
	}

	private void send(List<String> values, String metric,
			String monitoredResourceId) {
		Model m = createModel(values, metric, monitoredResourceId);
		String streamURI = getStreamURI(metric);
		try {
			csparql_api.feedStream(streamURI, m);
			logger.info("Monitoring data sent to {}: {} {} {}", ddaURL,
					monitoredResourceId, metric, values);
		} catch (ServerErrorException | StreamErrorException e) {
			logger.error("Error while sending monitoring datum to {}", ddaURL,
					e);
		}
	}

	private String getStreamURI(String metric) {
		String streamURI = "http://www.modaclouds.eu/streams/" + metric;
		return streamURI;
	}

	private Model createModel(List<String> values, String metric,
			String monitoredResourceId) {
		Model m = ModelFactory.createDefaultModel();
		for (String value : values) {
			String monDatumInstanceURI = DDAOntology.MonitoringDatum + "#"
					+ UUID.randomUUID().toString();
			m.createResource(monDatumInstanceURI)
					.addProperty(RDF.type, DDAOntology.MonitoringDatum)
					.addProperty(DDAOntology.metric,
							m.createTypedLiteral(metric, XSDDatatype.XSDstring))
					.addProperty(DDAOntology.value,
							m.createTypedLiteral(value, XSDDatatype.XSDdouble))
					.addProperty(
							DDAOntology.resourceId,
							m.createTypedLiteral(monitoredResourceId,
									XSDDatatype.XSDstring));
		}
		return m;
	}

}
