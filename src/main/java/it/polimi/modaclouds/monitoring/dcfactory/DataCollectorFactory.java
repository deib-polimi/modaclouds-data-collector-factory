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

import it.polimi.modaclouds.monitoring.dcfactory.ddaconnectors.DDAConnector;
import it.polimi.modaclouds.monitoring.dcfactory.kbconnectors.KBConnector;
import it.polimi.modaclouds.qos_models.monitoring_ontology.Resource;
import it.polimi.modaclouds.qos_models.monitoring_ontology.Vocabulary;

import java.util.Collection;
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

	private DDAConnector dda;
	private ScheduledExecutorService executorService = Executors
			.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> kbSyncExecutorHandler;
	private Map<String, Map<String, DCMetaData>> dcByMetricByResourceId;
	private KBConnector kb;
	private int kbSyncPeriod;
	private boolean isSyncingWithKB = false;
	private Set<String> monitoredResourcesIds;

	// private Set<DataCollector> installedDataCollectors;

	/**
	 * This method will be called whenever synchronization with KB ends. The
	 * data collector factory is notified and it can check whether it has to
	 * update, delete or add new data collectors.
	 */
	protected abstract void syncedWithKB();

	/**
	 * 
	 * @param dda
	 *            Any implementation of the DDAHandler.
	 *            {@link it.polimi.modaclouds.monitoring.dcfactory.ddaconnectors.RCSConnector}
	 *            provided in this version.
	 * @param kb
	 *            Any implementation of the KBHandler.
	 *            {@link it.polimi.modaclouds.monitoring.dcfactory.kbconnectors.FusekiConnector}
	 *            provided in this version.
	 */
	public DataCollectorFactory(DDAConnector dda, KBConnector kb) {
		this.dda = dda;
		this.kb = kb;
		dcByMetricByResourceId = new HashMap<String, Map<String, DCMetaData>>();
		monitoredResourcesIds = new HashSet<String>();
	}

	/**
	 * Adds the id of a monitored resource, required to have the Data Collector
	 * Factory retrieve the necessary Data Collectors from the KB.
	 * 
	 * @param monitoredResourceId
	 */
	public void addMonitoredResourceId(String monitoredResourceId) {
		monitoredResourcesIds.add(monitoredResourceId);
		logger.info("Resource with id {} was added to the monitored resources",
				monitoredResourceId);
	}

	/**
	 * Starts periodical synchronization with KB to retrieve data collectors
	 * monitoring the specified resources.
	 * 
	 * @param kbSyncPeriod
	 *            Interval in seconds between two subsequent synchronizations
	 */
	public void startSyncingWithKB(int kbSyncPeriod) {
		logger.info("Starting synchronization with KB...");
		if (isSyncingWithKB) {
			logger.error("The Data Collector Factory is already syncing with KB");
			return;
		}
		this.kbSyncPeriod = kbSyncPeriod;
		kbSyncExecutorHandler = executorService.scheduleWithFixedDelay(
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
				}, 0, kbSyncPeriod, TimeUnit.SECONDS);
		setIsSyncingWithKB(true);
		logger.info("Syncing with KB started.");
	}

	public void stopSyncingWithKB() {
		kbSyncExecutorHandler.cancel(true);
		setIsSyncingWithKB(false);
		logger.info("Syncing with KB stopped.");
	}

	private void setIsSyncingWithKB(boolean isSyncingWithKB) {
		this.isSyncingWithKB = isSyncingWithKB;
	}

	/**
	 * 
	 * @return true if the synchronization was already started
	 */
	public boolean isSyncingWithKB() {
		return isSyncingWithKB;
	}

	private void syncWithKB() {
		Map<String, Map<String, DCMetaData>> newDCByMetricByResourceId = new HashMap<String, Map<String, DCMetaData>>();
		logger.info("Syncing with KB...");
		Set<DCMetaData> newDataCollectors = kb.getDataCollectorsMetaData();
		if (newDataCollectors == null)
			newDataCollectors = new HashSet<DCMetaData>();
		logger.info("{} data collectors were downloaded from KB",
				newDataCollectors.size());
		for (String resourceId : monitoredResourcesIds) {
			for (DCMetaData dc : newDataCollectors) {
				if (isMonitoredBy(resourceId, dc)) {
					logger.info("Resource {} required to be monitored according to {}", resourceId, dc.toString());
					Map<String, DCMetaData> dcByMetric = newDCByMetricByResourceId
							.get(resourceId);
					if (dcByMetric == null) {
						dcByMetric = new HashMap<String, DCMetaData>();
						newDCByMetricByResourceId.put(resourceId, dcByMetric);
					}
					dcByMetric.put(dc.getMonitoredMetric().toLowerCase(), dc);
				}
			}
		}
		dcByMetricByResourceId = newDCByMetricByResourceId;
		logger.info("Data collectors synced with KB.");
		syncedWithKB();
	}

	private boolean isMonitoredBy(String resourceId, DCMetaData dc) {
		if (dc.getMonitoredResourcesIds().contains(resourceId))
			return true;
		if (!dc.getMonitoredResourcesIds().isEmpty())
			return false;
		Resource resource = kb.getResourceById(resourceId);
		if (resource == null) {
			logger.error("There is no resource with {} {} on the KB",
					Vocabulary.resourceIdParameterName, resourceId);
			return false;
		}
		for (String type : dc.getMonitoredResourcesTypes()) {
			if (type.equalsIgnoreCase(resource.getType()))
				return true;
		}
		if (!dc.getMonitoredResourcesTypes().isEmpty())
			return false;
		for (String clazz : dc.getMonitoredResourcesClasses()) {
			if (clazz.equalsIgnoreCase(resource.getClass().getSimpleName()))
				return true;
		}
		return false;
	}

	/**
	 * Getter for the local representation of data collectors, kept in sync with
	 * the KB automatically. The requested data collector will be available only
	 * if monitoring data for the metric and resource specified are required by
	 * the monitoring platform. If {@code null} is returned, the dedicated data
	 * collector should not send data.
	 * 
	 * @param monitoredResourceId
	 *            The id of the resource monitored by the data collector
	 * @param monitoredMetric
	 *            The metric monitored by the data collector
	 * @return the data collector monitoring metric {@code monitoredMetric} on
	 *         resource with id {@code monitoredResourceId} if the data
	 *         collector exists on the KB, {@code null} otherwise
	 */
	public DCMetaData getDataCollector(String monitoredResourceId,
			String monitoredMetric) {
		if (dcByMetricByResourceId.get(monitoredResourceId) == null)
			return null;
		return dcByMetricByResourceId.get(monitoredResourceId).get(
				monitoredMetric.toLowerCase());
	}

	/**
	 * Getter for the local representation of data collectors, kept in sync with
	 * the KB automatically. Only data collectors for the resource specified
	 * that are requested by the mnitoring platform are returned if any. Only
	 * returned data collectors should send data for the resource specified.
	 * 
	 * @param monitoredResourceId
	 *            The id of the resource monitored by the data collector
	 * @return data collectors monitoring resource with id
	 *         {@code monitoredResourceId}
	 */
	public Collection<DCMetaData> getDataCollectors(String monitoredResourceId) {
		Set<DCMetaData> dcs = new HashSet<DCMetaData>();
		if (dcByMetricByResourceId.get(monitoredResourceId) == null)
			return dcs;
		return dcByMetricByResourceId.get(monitoredResourceId).values();
	}

	/**
	 * The monitoring datum is sent to the DDA synchronously.
	 * 
	 * @param value
	 * @param metric
	 * @param monitoredResourceId
	 */
	public void sendSyncMonitoringDatum(String value, String metric,
			String monitoredResourceId) {
		dda.sendSyncMonitoringDatum(value, metric.toLowerCase(),
				monitoredResourceId);
	}

	/**
	 * The monitoring datum is sent to the DDA asynchronously.
	 * 
	 * @param value
	 * @param metric
	 * @param monitoredResourceId
	 */
	public void sendAsyncMonitoringDatum(String value, String metric,
			String monitoredResourceId) {
		dda.sendAsyncMonitoringDatum(value, metric.toLowerCase(),
				monitoredResourceId);
	}
}
