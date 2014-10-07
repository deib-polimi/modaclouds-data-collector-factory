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

import static org.junit.Assert.*;

import org.junit.Test;

public class DCHashCodeTest {

	@Test
	public void emptyDC() {
		DCConfig dc1 = new DCConfig();
		DCConfig dc2 = new DCConfig();
		assertTrue(dc1.hashCode()==dc2.hashCode());
	}
	
	@Test
	public void dcWithDifferentID() {
		DCConfig dc1 = new DCConfig();
		DCConfig dc2 = new DCConfig();
		dc1.setMonitoredMetric("ReponseTime");
		dc1.setMonitoredMetric("CPUUtilization");
		dc1.addMonitoredResourceClass("myresource1");
		dc1.addMonitoredResourceClass("myresource2");
		dc1.addParameter("key", "value");
		dc2.addMonitoredResourceClass("myresource1");
		dc2.addMonitoredResourceClass("myresource2");
		dc2.addParameter("key", "value");
		assertTrue(dc1.hashCode()==dc2.hashCode());
	}

}
