package de.olafkock.liferay.documentation.osgi.tracker;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.portlet.filter.PortletFilter;
import javax.portlet.filter.RenderFilter;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import de.olafkock.liferay.documentation.portletfilter.DocumentationFilter;
import de.olafkock.liferay.documentation.portletfilter.PlaceholderFilter;
import de.olafkock.liferay.documentation.resources.PortletDocumentation;
import de.olafkock.liferay.documentation.resources.PortletDocumentationFactory;

/**
 * Registers or Unregisters a PortletFilter for each portlet service that's
 * being started or stopped.
 * 
 * @author Olaf Kock
 *
 */
final class PortletServiceListener implements ServiceListener {

	PortletServiceListener(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
		this.config = PortletDocumentationFactory.getConfiguration();
	}
	
	@Override
	public void serviceChanged(ServiceEvent event) {
		switch (event.getType()) {
		case ServiceEvent.REGISTERED:
			registerFilter(event.getServiceReference());
			break;
		case ServiceEvent.UNREGISTERING:
			unregisterFilter(event.getServiceReference());
			break;
		case ServiceEvent.MODIFIED:
			unregisterFilter(event.getServiceReference());
			registerFilter(event.getServiceReference());
			break;
		default:
			break;
		}
	}

	private void registerFilter(ServiceReference<?> portletServiceReference) {
		String portletName = (String) portletServiceReference.getProperty("javax.portlet.name");
		PortletDocumentation conf = config.get(portletName);
		RenderFilter portletFilter;
		if(conf != null) {
			portletFilter = new DocumentationFilter(conf);
		} else {
			portletFilter = new PlaceholderFilter(portletName);
		}
		ServiceRegistration<?> portletFilterReference = bundleContext.registerService(
				new String[] {PortletFilter.class.getName()}, 
				portletFilter, 
				getServiceProperties(portletName));
		services.put(portletName, portletFilterReference);
	}

	private void unregisterFilter(ServiceReference<?> portletServiceReference) {
		String portletName = (String) portletServiceReference.getProperty("javax.portlet.name");
		if(services.containsKey(portletName)) {
			services.remove(portletName).unregister();
		}
	}

	@SuppressWarnings("deprecation")
	private Dictionary<String, Object> getServiceProperties(String portletName) {
		Dictionary<String,Object> result = new Hashtable<String,Object>();
		result.put("javax.portlet.name", portletName);
		result.put("service.ranking", new Integer(10000));
		return result;
	}

	private BundleContext bundleContext;
	private HashMap<String, ServiceRegistration<?>> services = new HashMap<String, ServiceRegistration<?>>();
	private Map<String, PortletDocumentation> config;
}