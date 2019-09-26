package de.olafkock.liferay.documentation.resources;

import com.liferay.portal.kernel.json.JSON;

public class PortletDocumentation {
	@JSON
	public String portletId;
	@JSON
	public URLConfig[] urlConfig;
	
	public URLConfig getSecondaryUrlConfig(String key) {
		if(urlConfig!=null) {
			for (URLConfig uc : urlConfig) {
				if(key.equals(uc.secondary))
					return uc;
			}
		}
		return null;
	}
}
