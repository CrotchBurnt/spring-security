/*
 * Copyright 2002-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.security.saml2.provider.service.authentication;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.xml.ElementSupport;
import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.AbstractXMLObjectBuilder;
import org.opensaml.core.xml.ElementExtensibleXMLObject;
import org.opensaml.core.xml.Namespace;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.AbstractXMLObjectMarshaller;
import org.opensaml.core.xml.io.AbstractXMLObjectUnmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AttributeValue;
import org.w3c.dom.Element;

public class TestCustomOpenSamlObject {

	public interface CustomSamlObject extends ElementExtensibleXMLObject {

		String TYPE_LOCAL_NAME = "CustomType";

		String TYPE_CUSTOM_PREFIX = "custom";

		String CUSTOM_NS = "https://custom.com/schema/custom";

		/** QName of the CustomType type. */
		QName TYPE_NAME = new QName(CUSTOM_NS, TYPE_LOCAL_NAME, TYPE_CUSTOM_PREFIX);

		String getStreet();

		String getStreetNumber();

		String getZIP();

		String getCity();

	}

	public static class CustomSamlObjectImpl extends AbstractXMLObject
			implements TestCustomOpenSamlObject.CustomSamlObject {

		@Nonnull
		private IndexedXMLObjectChildrenList<XMLObject> unknownXMLObjects;

		/**
		 * Constructor.
		 * @param namespaceURI the namespace the element is in
		 * @param elementLocalName the local name of the XML element this Object
		 * represents
		 * @param namespacePrefix the prefix for the given namespace
		 */
		protected CustomSamlObjectImpl(@Nullable String namespaceURI, @Nonnull String elementLocalName,
				@Nullable String namespacePrefix) {
			super(namespaceURI, elementLocalName, namespacePrefix);
			super.getNamespaceManager().registerNamespaceDeclaration(new Namespace(CUSTOM_NS, TYPE_CUSTOM_PREFIX));
			this.unknownXMLObjects = new IndexedXMLObjectChildrenList<>(this);
		}

		@Nonnull
		@Override
		public List<XMLObject> getUnknownXMLObjects() {
			return this.unknownXMLObjects;
		}

		@Nonnull
		@Override
		public List<XMLObject> getUnknownXMLObjects(@Nonnull QName typeOrName) {
			return (List<XMLObject>) this.unknownXMLObjects.subList(typeOrName);
		}

		@Nullable
		@Override
		public List<XMLObject> getOrderedChildren() {
			return Collections.unmodifiableList(this.unknownXMLObjects);
		}

		@Override
		public String getStreet() {
			return ((XSAny) getOrderedChildren().get(0)).getTextContent();
		}

		@Override
		public String getStreetNumber() {
			return ((XSAny) getOrderedChildren().get(1)).getTextContent();
		}

		@Override
		public String getZIP() {
			return ((XSAny) getOrderedChildren().get(2)).getTextContent();
		}

		@Override
		public String getCity() {
			return ((XSAny) getOrderedChildren().get(3)).getTextContent();
		}

	}

	public static class CustomSamlObjectBuilder
			extends AbstractXMLObjectBuilder<TestCustomOpenSamlObject.CustomSamlObject> {

		@Nonnull
		@Override
		public TestCustomOpenSamlObject.CustomSamlObject buildObject(@Nullable String namespaceURI,
				@Nonnull String localName, @Nullable String namespacePrefix) {
			return new TestCustomOpenSamlObject.CustomSamlObjectImpl(namespaceURI, localName, namespacePrefix);
		}

	}

	public static class CustomSamlObjectMarshaller extends AbstractXMLObjectMarshaller {

		public CustomSamlObjectMarshaller() {
			super();
		}

		@Override
		protected void marshallElementContent(@Nonnull XMLObject xmlObject, @Nonnull Element domElement) {
			final TestCustomOpenSamlObject.CustomSamlObject customSamlObject = (TestCustomOpenSamlObject.CustomSamlObject) xmlObject;

			for (XMLObject object : customSamlObject.getOrderedChildren()) {
				ElementSupport.appendChildElement(domElement, object.getDOM());
			}
		}

	}

	public static class CustomSamlObjectUnmarshaller extends AbstractXMLObjectUnmarshaller {

		public CustomSamlObjectUnmarshaller() {
			super();
		}

		@Override
		protected void processChildElement(@Nonnull XMLObject parentXMLObject, @Nonnull XMLObject childXMLObject)
				throws UnmarshallingException {
			final TestCustomOpenSamlObject.CustomSamlObject customSamlObject = (TestCustomOpenSamlObject.CustomSamlObject) parentXMLObject;
			super.processChildElement(customSamlObject, childXMLObject);
			customSamlObject.getUnknownXMLObjects().add(childXMLObject);
		}

		@Nonnull
		@Override
		protected XMLObject buildXMLObject(@Nonnull Element domElement) {
			return new TestCustomOpenSamlObject.CustomSamlObjectImpl(SAMLConstants.SAML20_NS,
					AttributeValue.DEFAULT_ELEMENT_LOCAL_NAME,
					TestCustomOpenSamlObject.CustomSamlObject.TYPE_CUSTOM_PREFIX);
		}

	}

}
