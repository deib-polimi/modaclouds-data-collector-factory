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

import static org.junit.Assert.fail;

import java.lang.reflect.Field;

import org.apache.commons.beanutils.BeanUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FieldsAlignmentTest {

	private Logger logger = LoggerFactory.getLogger(FieldsAlignmentTest.class);

	@Test
	public void test() {
		try {
			for (Field field : DCFields.class.getFields()) {
				BeanUtils.getProperty(new DCMetaData(), (String) field.get(null));
			}
		} catch (Exception e) {
			logger.error("{} not aligned with {}",
					DCFields.class.getSimpleName(),
					DCMetaData.class.getSimpleName(),e);
			fail();
		}
	}

}
