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

import it.polimi.modaclouds.monitoring.dcfactory.connectors.DDAHandler;
import it.polimi.modaclouds.monitoring.dcfactory.connectors.KBHandler;
import it.polimi.modaclouds.qos_models.monitoring_ontology.DataCollector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.jena.atlas.web.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DataCollectorFactory {

	private final Logger logger = LoggerFactory
			.getLogger(DataCollectorFactory.class);

	private DDAHandler dda;
	private ScheduledExecutorService executorService = Executors
			.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> kbPollingExecutorHandler;
	private Map<String, Map<String, DataCollector>> dcByMetricByResourceId;
	private KBHandler kb;
	private int kbPollingPeriod;
	private boolean isSyncingWithKB = false;
	private Set<String> monitoredResourcesIds;

	// private Set<DataCollector> installedDataCollectors;

	protected abstract void syncedWithKB();

	public DataCollectorFactory(DDAHandler dda, KBHandler kb) {
		this.dda = dda;
		this.kb = kb;
		dcByMetricByResourceId = new HashMap<String, Map<String, DataCollector>>();
		monitoredResourcesIds = new HashSet<String>();
	}

	public void addMonitoredResourceId(String monitoredResourceId) {
		monitoredResourcesIds.add(monitoredResourceId);
		logger.info("Resource with id {} was added to the monitored resources",
				monitoredResourceId);
	}

	public void startPollingFromKB(int kbPollingPeriod) {
		logger.info("Starting synchronization with KB...");
		if (isSyncingWithKB) {
			logger.error("The Data Collector Factory is already polling from KB");
			return;
		}
		this.kbPollingPeriod = kbPollingPeriod;
		kbPollingExecutorHandler = executorService.scheduleWithFixedDelay(
				new Runnable() {
					@Override
					public void run() {
						try {
							syncWithKB();
						} catch (HttpException e) {
							logger.error(
									"Error while retrieving data collectors from KB, KB may be unreachable",
									e);
						} catch (Exception e) {
							logger.error(
									"Error while retrieving data collectors from KB",
									e);
						}
					}
				}, 0, kbPollingPeriod, TimeUnit.SECONDS);
		setIsSyncingWithKB(true);
		logger.info("Syncing with KB started.");
	}

	private void setIsSyncingWithKB(boolean isSyncingWithKB) {
		this.isSyncingWithKB = isSyncingWithKB;
	}

	public boolean isSyncingWithKB() {
		return isSyncingWithKB;
	}

	public void syncWithKB() {
		Map<String, Map<String, DataCollector>> newDCByMetricByResourceId = new HashMap<String, Map<String, DataCollector>>();
		logger.info("Syncing with KB...");
		Set<DataCollector> newDataCollectors = kb
				.getDataCollectors(monitoredResourcesIds);
		if (newDataCollectors == null)
			newDataCollectors = new HashSet<DataCollector>();
		for (DataCollector dc : newDataCollectors) {
			for (String resourceId : dc.getMonitoredResourcesIds()) {
				Map<String, DataCollector> dcByMetric = newDCByMetricByResourceId
						.get(resourceId);
				if (dcByMetric == null) {
					dcByMetric = new HashMap<String, DataCollector>();
					newDCByMetricByResourceId.put(resourceId, dcByMetric);
				}
				dcByMetric.put(dc.getMonitoredMetric(), dc);
			}
			logger.info("Data collector retrieved from KB: {}", dc.toString());
		}
		logger.info("{} data collectors were downloaded from the KB",
				newDataCollectors.size());
		dcByMetricByResourceId = newDCByMetricByResourceId;
		logger.info("Data collectors synced with KB.");
		syncedWithKB();
	}

	protected DataCollector getDataCollector(String monitoredResourceId,
			String metric) {
		if (dcByMetricByResourceId.get(monitoredResourceId) == null)
			return null;
		return dcByMetricByResourceId.get(monitoredResourceId).get(metric);
	}
	
	protected Map<String,String> getParameters(DataCollector dc){
		return kb.getParameters(dc);
	}

	protected void sendSyncMonitoringDatum(String value, String metric,
			String monitoredResourceId) {
		dda.sendSyncMonitoringDatum(value, metric, monitoredResourceId);
	}

	protected void sendAsyncMonitoringDatum(String value, String metric,
			String monitoredResourceId) {
		dda.sendAsyncMonitoringDatum(value, metric, monitoredResourceId);
	}
}
