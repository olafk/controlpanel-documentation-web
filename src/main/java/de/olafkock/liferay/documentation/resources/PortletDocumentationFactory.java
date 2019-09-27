package de.olafkock.liferay.documentation.resources;

import com.liferay.portal.kernel.json.JSONFactoryUtil;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Read configuration from whereever. Yes, it's completely hardcoded in this 
 * proof-of-concept.
 * 
 * @author Olaf Kock
 *
 */
public class PortletDocumentationFactory {

	/**
	 * @return null if infrastructure isn't ready yet, e.g. JSON deserializing fails
	 * for any reason (typically because our bundle may be started before Liferay's 
	 * JSONFactory is initialized). Try again later if this appears.
	 */
	public static Map<String, PortletDocumentation> getConfiguration() {
		if(JSONFactoryUtil.getJSONFactory() == null) return null; // not yet initialized
		
		HashMap<String, PortletDocumentation> result = new HashMap<String, PortletDocumentation>();
		try {
			InputStream configuration = PortletDocumentationFactory.class.getResourceAsStream("/content.json");
			InputStream bis = configuration;
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			int conf_read = bis.read();
			while(conf_read != -1) {
			    buf.write((byte) conf_read);
			    conf_read = bis.read();
			}
			String json = buf.toString("UTF-8");
			PortletDocumentation[] docs;
			docs = JSONFactoryUtil.looseDeserialize(json, PortletDocumentation[].class);
			for (PortletDocumentation doc : docs) {
				result.put(doc.portletId, doc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
