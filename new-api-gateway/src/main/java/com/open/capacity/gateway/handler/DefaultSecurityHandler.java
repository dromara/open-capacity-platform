package com.open.capacity.gateway.handler;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.hdiv.exception.HDIVException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class DefaultSecurityHandler extends DefaultHandler {

	/**
	 * Location of the xml file with default editable validations.
	 */
	private static final String DEFAULT_VALIDATION_PATH = "defaultSecurity.xml";

	/**
	 * List with processed validations.
	 */
	private final List<Map<ValidationParam, String>> validations = new ArrayList<Map<ValidationParam, String>>();

	/**
	 * Current Validation data.
	 */
	private Map<ValidationParam, String> validation = null;

	/**
	 * Read xml file from the default path.
	 */
	public void readDefaultValidations() {
		this.readDefaultValidations(DEFAULT_VALIDATION_PATH);
	}

	/**
	 * Read xml file from the given path.
	 *
	 * @param filePath xml file path
	 */
	public void readDefaultValidations(final String filePath) {

		try {
			ClassLoader classLoader = DefaultSecurityHandler.class.getClassLoader();
			InputStream is = classLoader.getResourceAsStream(filePath);
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			sp.parse(is, this);
		}
		catch (ParserConfigurationException e) {
			throw new HDIVException(e.getMessage(), e);
		}
		catch (SAXException e) {
			throw new HDIVException(e.getMessage(), e);
		}
		catch (IOException e) {
			throw new HDIVException(e.getMessage(), e);
		}
	}

	@Override
	public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) {

		if ("validation".equals(qName)) {
			validation = new EnumMap<ValidationParam, String>(ValidationParam.class);
			String id = attributes.getValue("id");
			validation.put(ValidationParam.ID, id);
			validations.add(validation);
		}
	}

	@Override
	public void characters(final char[] ch, final int start, final int length) throws SAXException {
		String val = new String(ch, start, length).trim();
		if (val.length() > 0) {
			validation.put(ValidationParam.REGEX, val);
		}
	}

	/**
	 * @return the validations
	 */
	public List<Map<ValidationParam, String>> getValidations() {
		return validations;
	}

	public enum ValidationParam {
		ID, REGEX
	}


}
