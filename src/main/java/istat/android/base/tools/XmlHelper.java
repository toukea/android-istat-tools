package istat.android.base.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/*
 * Copyright (C) 2014 Istat Dev.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 
 * @author Toukea Tatsi (Istat)
 * 
 */
public class XmlHelper {
	Document DOCM;

	public XmlHelper(InputStream inputStream)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		DOCM = builder.parse(inputStream);
		inputStream.close();
	}

	public XmlHelper(String urlpath) throws ParserConfigurationException,
			SAXException, IOException {
		URL url;
		url = new URL(urlpath);
		InputStream inputStream = url.openConnection().getInputStream();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		try {
			DOCM = builder.parse(inputStream);
		} catch (Exception e) {
			DOCM = null;
		}
		inputStream.close();
		// log("Client XML response", "execute");
	}

	private XmlHelper(Document doc) {
		DOCM = doc;
	}

	public Node getElementNode(String Name) {
		return DOCM.getElementsByTagName(Name).item(0);
	}

	public Node getElementNode(String Name, int NodeOcurance) {
		return DOCM.getElementsByTagName(Name).item(NodeOcurance);
	}

	public String getAttribut(String Name, String Attribut) {

		// dom.getElementsByTagName(Name).item(0).
		return DOCM.getElementsByTagName(Name).item(0).getAttributes()
				.getNamedItem(Attribut).getNodeValue();
	}

	public String getTextContent(String TagName) {

		// dom.getElementsByTagName(Name).item(0).
		return DOCM.getElementsByTagName(TagName).item(0).getTextContent();
	}

	// ---------------------------------------------------------------
	public String getAttribut(String TagName, int Tag_Ocurance, String Attribut) {

		// dom.getElementsByTagName(Name).item(0).
		return DOCM.getElementsByTagName(TagName).item(Tag_Ocurance)
				.getAttributes().getNamedItem(Attribut).getNodeValue();
	}

	public String getTextContent(String TagName, int Tag_Ocurance) {

		// dom.getElementsByTagName(Name).item(0).
		return DOCM.getElementsByTagName(TagName).item(Tag_Ocurance)
				.getTextContent();
	}

	/**
	 * 
	 * @param xPath
	 * @return the corresponding text of the param's path Ex: get attibut value
	 *         of the second node node /nodes/node[2]/@value"); get node
	 *         TextContent at: /nodes/node[2];
	 * @throws XPathExpressionException
	 */
	public String getTextFromPath(String xPath) throws XPathExpressionException {
		return XPathFactory.newInstance().newXPath()
				.evaluate(xPath, DOCM.getChildNodes());
	}

	public static String getTextFromPath(String xPath, Node node)
			throws XPathExpressionException {
		return XPathFactory.newInstance().newXPath().evaluate(xPath, node);
	}

	public static XmlHelper LoadXml(String xmlString)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;

		builder = factory.newDocumentBuilder();
		// Document document = builder.parse( new InputSource( new StringReader(
		// xmlString ) ) );
		return new XmlHelper(builder.parse(new InputSource(new StringReader(
				xmlString))));

	}

}