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
package it.polimi.modaclouds.monitoring.dcfactory.ddaconnectors;

import it.polimi.modaclouds.monitoring.dcfactory.DDAConnector;

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

public class RCSConnector implements DDAConnector {

	private final Logger logger = LoggerFactory
			.getLogger(RCSConnector.class);

	private RSP_services_csparql_API csparql_api;
	private ExecutorService execService = Executors.newSingleThreadExecutor();
	private String ddaURL;

	public RCSConnector(String ddaURL) {
		this.ddaURL = ddaURL;
		if (!ddaURL.endsWith("/"))
			ddaURL += "/";
		csparql_api = new RSP_services_csparql_API(ddaURL);
	}

	@Override
	public void sendSyncMonitoringDatum(String value, String metric,
			String monitoredResourceId) {
		send(value, metric, monitoredResourceId);
	}

	@Override
	public void sendAsyncMonitoringDatum(final String value,
			final String metric, final String monitoredResourceId) {
		execService.execute(new Runnable() {
			@Override
			public void run() {
				send(value, metric, monitoredResourceId);
			}
		});
	}

	private void send(String value, String metric, String monitoredResourceId) {
		metric = metric.toLowerCase();
		Model m = createModel(value, metric, monitoredResourceId);
		String streamURI = getStreamURI(metric);
		try {
			csparql_api.feedStream(streamURI, m);
			logger.debug("Monitoring datum sent synchronously to {}", ddaURL);
		} catch (ServerErrorException | StreamErrorException e) {
			logger.error("Error while sending monitoring datum to {}", ddaURL,e);
		}
	}

	private String getStreamURI(String metric) {
		String streamURI = ddaURL.toString() + "/streams/" + metric;
		return streamURI;
	}

	private Model createModel(String value, String metric,
			String monitoredResourceId) {
		String monDatumInstanceURI = RCSOntology.MonitoringDatum + "#"
				+ UUID.randomUUID().toString();
		Model m = ModelFactory.createDefaultModel();
		m.createResource(monDatumInstanceURI)
				.addProperty(RDF.type, RCSOntology.MonitoringDatum)
				.addProperty(RCSOntology.metric, m.createTypedLiteral(metric))
				.addProperty(RCSOntology.value,
						m.createTypedLiteral(value, XSDDatatype.XSDdouble))
				.addProperty(RCSOntology.resourceId,
						m.createTypedLiteral(monitoredResourceId));
		return m;
	}

}
