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
import it.polimi.modaclouds.qos_models.monitoring_ontology.Resource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

	protected DDAConnector dda;
	private ScheduledExecutorService executorService = Executors
			.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> kbSyncExecutorHandler;
	private Map<String, Set<DCConfig>> dCsConfigByMetric;
	private Set<DCConfig> allDCsConfigs;
	protected KBConnector kb;
	private boolean isSyncingWithKB = false;


	/**
	 * This method will be called whenever synchronization with KB ends. The
	 * data collector factory is notified and it can check whether it has to
	 * update, delete or add new data collectors.
	 */
	protected abstract void syncedWithKB();

	public DataCollectorFactory(String ddaUrl, String kbUrl) {
		this.dda = new DDAConnector(ddaUrl);
		this.kb = new KBConnector(kbUrl);
		dCsConfigByMetric = new HashMap<String, Set<DCConfig>>();
		allDCsConfigs = new HashSet<DCConfig>();
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
		allDCsConfigs = kb.getAllDCsConfig();
		Map<String, Set<DCConfig>> newDCsConfigByMetric = new HashMap<String, Set<DCConfig>>();
		if (allDCsConfigs != null) {
			for (DCConfig dcConfig : allDCsConfigs) {
				Set<DCConfig> configsPerMetric = newDCsConfigByMetric
						.get(dcConfig.getMonitoredMetric());
				if (configsPerMetric == null) {
					configsPerMetric = new HashSet<DCConfig>();
					newDCsConfigByMetric.put(dcConfig.getMonitoredMetric().toLowerCase(),
							configsPerMetric);
				}
				configsPerMetric.add(dcConfig);
			}
		}
		dCsConfigByMetric = newDCsConfigByMetric;
		logger.info("Data collectors configuration synced with KB.");
		syncedWithKB();
	}

	public boolean monitoringRequired(Resource resource, DCConfig dcConfig) {
		if (dcConfig.getMonitoredResourcesIds().contains(resource.getId()))
			return true;
		if (!dcConfig.getMonitoredResourcesIds().isEmpty())
			return false;
		for (String type : dcConfig.getMonitoredResourcesTypes()) {
			if (type.equalsIgnoreCase(resource.getType()))
				return true;
		}
		if (!dcConfig.getMonitoredResourcesTypes().isEmpty())
			return false;
		for (String clazz : dcConfig.getMonitoredResourcesClasses()) {
			if (clazz.equalsIgnoreCase(resource.getClass().getSimpleName()))
				return true;
		}
		return false;
	}

	public Set<DCConfig> getConfiguration(Resource resource,
			String monitoredMetric) {
		Set<DCConfig> selectedConfig = new HashSet<DCConfig>();
		Set<DCConfig> allDCsConfigs;
		if (monitoredMetric != null)
			allDCsConfigs = dCsConfigByMetric.get(monitoredMetric.toLowerCase());
		else
			allDCsConfigs = this.allDCsConfigs;
		if (allDCsConfigs != null) {
			for (DCConfig dcConfig : allDCsConfigs) {
				if (resource == null
						|| monitoringRequired(resource, dcConfig)) {
					selectedConfig.add(dcConfig);
				}
			}
		}
		return selectedConfig;
	}

	/**
	 * The monitoring datum is sent to the DDA synchronously.
	 * 
	 * @param value
	 * @param metric
	 * @param monitoredResourceId
	 */
	public void sendSyncMonitoringDatum(String value, String metric,
			Resource resource) {
		dda.sendSyncMonitoringDatum(value, metric.toLowerCase(),
				resource);
	}
	
	/**
	 * The monitoring data is sent to the DDA synchronously.
	 * 
	 * @param values
	 * @param metric
	 * @param monitoredResourceId
	 */
	public void sendSyncMonitoringData(List<String> values, String metric,
			Resource resource) {
		dda.sendSyncMonitoringData(values, metric.toLowerCase(),
				resource);
	}

	/**
	 * The monitoring datum is sent to the DDA asynchronously.
	 * 
	 * @param value
	 * @param metric
	 * @param monitoredResourceId
	 */
	public void sendAsyncMonitoringDatum(String value, String metric,
			Resource resource) {
		dda.sendAsyncMonitoringDatum(value, metric.toLowerCase(),
				resource);
	}
}
