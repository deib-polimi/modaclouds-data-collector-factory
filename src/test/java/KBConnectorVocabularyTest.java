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
import static org.junit.Assert.fail;
import it.polimi.modaclouds.monitoring.dcfactory.kbconnectors.FusekiDCMetaData;
import it.polimi.modaclouds.monitoring.dcfactory.kbconnectors.FusekiVocabulary;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KBConnectorVocabularyTest {

	private Logger logger = LoggerFactory
			.getLogger(KBConnectorVocabularyTest.class);

	@Test
	public void test() {
		try {
			FusekiDCMetaData.class
					.getDeclaredField(FusekiVocabulary.monitoredResourcesIds);
		} catch (NoSuchFieldException | SecurityException e) {
			logger.error("Vocabulary is not aligned with the implementation", e);
			fail();
		}
	}

}
