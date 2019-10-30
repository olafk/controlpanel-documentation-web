package de.olafkock.liferay.documentation.osgi.tracker;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

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

import de.olafkock.liferay.documentation.portletfilter.ContentInitializer;
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
	public static final Log log = LogFactoryUtil.getLog(PortletServiceListener.class);

	PortletServiceListener(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
		this.content = PortletDocumentationFactory.getConfiguration();
		
		// Temporary feature to generate new documentation pages for all
		// pages that have been seen and might not have configuration yet.
		this.contentInitializer = new ContentInitializer(content);
	}
	
	@Override
	public void serviceChanged(ServiceEvent event) {
		// we might have started before JSON could be deserialized - in that
		// case: try again to get our config before registering services
		// (in case we couldn't get our configuration yet, there won't be
		// any services that we could register anyway)
		if(this.content == null) {
			this.content = PortletDocumentationFactory.getConfiguration();
			// temporary content generator
			this.contentInitializer = new ContentInitializer(content);
		}
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

	/**
	 * register a PortletFilter for the given service.
	 * If we have an entry in the configuration, registers a filter that displays
	 * the configured values. Otherwise it registers a placeholder that will
	 * display the primary and secondary key for creating documentation for this 
	 * portlet 
	 * @param portletServiceReference
	 */
	private void registerFilter(ServiceReference<?> portletServiceReference) {
		String portletName = (String) portletServiceReference.getProperty("javax.portlet.name");
		PortletDocumentation conf = (content!=null)?content.get(portletName):null;
		RenderFilter portletFilter;
		if(conf != null) {
			portletFilter = new DocumentationFilter(conf, contentInitializer);
		} else {
			portletFilter = new PlaceholderFilter(portletName, contentInitializer);
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
		// Temporary feature: When shutting down, generate content.json for all
		// pages that could have been filtered
		if(services.isEmpty()) {
			contentInitializer.dumpConfig();
		}
	}

	private Dictionary<String, Object> getServiceProperties(String portletName) {
		Dictionary<String,Object> result = new Hashtable<String,Object>();
		result.put("javax.portlet.name", portletName);
		result.put("service.ranking", new Integer(10000));
		return result;
	}

	private BundleContext bundleContext;
	private HashMap<String, ServiceRegistration<?>> services = new HashMap<String, ServiceRegistration<?>>();
	private Map<String, PortletDocumentation> content;
	private ContentInitializer contentInitializer;
}