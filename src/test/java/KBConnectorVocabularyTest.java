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
