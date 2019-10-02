package de.olafkock.liferay.documentation.resources;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.ReleaseInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
	public static final Log log = LogFactoryUtil.getLog(PortletDocumentationFactory.class);
	/**
	 * @return null if infrastructure isn't ready yet, e.g. JSON deserializing fails
	 * for any reason (typically because our bundle may be started before Liferay's 
	 * JSONFactory is initialized). Try again later if this appears.
	 */
	public static Map<String, PortletDocumentation> getConfiguration() {
		if(JSONFactoryUtil.getJSONFactory() == null) return null; // not yet initialized
		
		HashMap<String, PortletDocumentation> result = new HashMap<String, PortletDocumentation>();
		try {
			String json = null;
			try {
				String location = "https://www.olafkock.de/liferay/controlpaneldocumentation/content.json?v=" + ReleaseInfo.getVersion();
				json = HttpUtil.URLtoString(location);
				log.info("read " + json.length() + " characters config from " + location);
			} catch (IOException e) {
				log.error(e);
			} catch (NullPointerException e) {
				return null; // not yet initialized
			}
			if(json == null || json.length() < 200) {
				InputStream configuration = PortletDocumentationFactory.class.getResourceAsStream("/content.json");
				InputStream bis = configuration;
				ByteArrayOutputStream buf = new ByteArrayOutputStream();
				int conf_read = bis.read();
				while(conf_read != -1) {
				    buf.write((byte) conf_read);
				    conf_read = bis.read();
				}
				json = buf.toString("UTF-8");
				log.info("read " + json.length() + " characters config from embedded file");
			}
			PortletDocumentation[] docs;
			docs = JSONFactoryUtil.looseDeserialize(json, PortletDocumentation[].class);
			for (PortletDocumentation doc : docs) {
				result.put(doc.portletId, doc);
			}
		} catch (Exception e) {
			log.error(e);
		}
		return result;
	}
}
