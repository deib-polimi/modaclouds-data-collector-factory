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
package it.polimi.modaclouds.monitoring.dcfactory.examples;

import it.polimi.modaclouds.monitoring.dcfactory.DataCollectorFactory;
import it.polimi.modaclouds.monitoring.dcfactory.ddaconnectors.DDAConnector;
import it.polimi.modaclouds.monitoring.dcfactory.ddaconnectors.RCSConnector;
import it.polimi.modaclouds.monitoring.dcfactory.kbconnectors.FusekiConnector;
import it.polimi.modaclouds.monitoring.dcfactory.kbconnectors.KBConnector;

public class MyDCFactory extends DataCollectorFactory {

	public static void main(String[] args) {
		MyDCFactory dcfactory = new MyDCFactory(new RCSConnector(
				"http://54.76.205.254:8175"), new FusekiConnector(
				"http://54.76.205.254:3030/modaclouds/kb"));
		dcfactory.addMonitoredResourceId("frontend1");
		dcfactory.addMonitoredResourceId("mic1");
		dcfactory.startSyncingWithKB(10);
	}

	public MyDCFactory(DDAConnector dda, KBConnector kb) {
		super(dda, kb);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void syncedWithKB() {
		System.out.println(getDataCollector("frontend1","cpuutilization"));
	}

}
