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

import it.polimi.modaclouds.monitoring.dcfactory.wrappers.DDAConnector;
import it.polimi.modaclouds.monitoring.dcfactory.wrappers.KBConnector;
import it.polimi.modaclouds.qos_models.monitoring_ontology.MOVocabulary;
import it.polimi.modaclouds.qos_models.monitoring_ontology.Resource;

import java.util.HashMap;
import java.util.Map;
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
	private Map<String, DCConfig> dcConfigByMetric;
	private KBConnector kb;
//	private int kbSyncPeriod;
	private boolean isSyncingWithKB = false;
	// private Set<String> monitoredResourcesIds;
//	private Set<String> monitoredMetrics;

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
	 *            {@link it.polimi.modaclouds.monitoring.dcfactory.wrappers.DDAConnector}
	 *            provided in this version.
	 * @param kb
	 *            Any implementation of the KBHandler.
	 *            {@link it.polimi.modaclouds.monitoring.dcfactory.wrappers.KBConnector}
	 *            provided in this version.
	 */
	public DataCollectorFactory(DDAConnector dda, KBConnector kb) {
		this.dda = dda;
		this.kb = kb;
		dcConfigByMetric = new HashMap<String, DCConfig>();
//		monitoredMetrics = new HashSet<String>();
	}

//	/**
//	 * Add metrics monitored by the DC, required to have the Data Collector
//	 * Factory retrieve the necessary DC Config from the KB.
//	 * 
//	 * @param monitoredResourceId
//	 */
//	public void addMonitoredMetric(String metric) {
//		monitoredMetrics.add(metric.toLowerCase());
//		logger.info("Metric \"{}\" added to the list of monitored metrics", metric);
//	}

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
//		this.kbSyncPeriod = kbSyncPeriod;
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
		logger.info("Syncing with KB...");
		Map<String,DCConfig> newDCsConfigByMetric = kb.getDCsConfigByMetric();
		logger.info("{} data collectors configurations were downloaded from KB",
				newDCsConfigByMetric.size());
		dcConfigByMetric = newDCsConfigByMetric;
		logger.info("Data collectors synced with KB.");
		syncedWithKB();
	}

	/**
	 * Checks if metric {@code metric} should be collected for the resource 
	 * identified by {@code resourceId} according to the current configuration.
	 * 
	 * @param resourceId
	 * @param metric
	 * @return
	 */
	public boolean monitoringRequired(String resourceId, String metric) {
		DCConfig dc = dcConfigByMetric.get(metric);
		if (dc.getMonitoredResourcesIds().contains(resourceId))
			return true;
		if (!dc.getMonitoredResourcesIds().isEmpty())
			return false;
		Resource resource = kb.getResourceById(resourceId);
		if (resource == null) {
			logger.error("There is no resource with {} {} on the KB",
					MOVocabulary.resourceIdParameterName, resourceId);
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
	 * Getter for data collectors configuration, kept in sync with
	 * the KB automatically. If {@code null} is returned, the dedicated data
	 * collector should not send data.
	 * 
	 * @param monitoredMetric
	 *            The metric monitored by the data collector
	 * @return the data collector configuration for the monitoring metric {@code monitoredMetric} 
	 * if it exists, {@code null} otherwise
	 */
	public DCConfig getConfiguration(String monitoredMetric) {
		return dcConfigByMetric.get(monitoredMetric);
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
