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
package it.polimi.modaclouds.monitoring.dcfactory.wrappers;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;

public class DDAOntology {

	public static final String URI = "http://www.modaclouds.eu/rdfs/1.0/monitoringdata#";
	public static String prefix = "modamd";

	public static OntModel model = ModelFactory
			.createOntologyModel(OntModelSpec.RDFS_MEM);
	
	public static Property metric = makeProperty(DDAVocabulary.metric);
	public static final Property value = makeProperty(DDAVocabulary.value);
	public static final Property resourceId = makeProperty(DDAVocabulary.resourceId);
	public static final Property timestamp = makeProperty(DDAVocabulary.timestamp);

	public static OntClass MonitoringDatum = makeClass(DDAVocabulary.MonitoringDatum);

	static {
		model.setNsPrefix(prefix, URI);

	}

	private static OntClass makeClass(String className) {
		return model.createClass(URI + className);
	}
	
	private static Property makeProperty(String propertyName) {
		return model.createProperty(URI + propertyName);
	}
	
	public static String shortForm(Property property) {
		return prefix+":"+property.getLocalName();
	}
	
	public static String shortForm(OntClass ontClass) {
		return prefix+":"+ontClass.getLocalName();
	}

}
