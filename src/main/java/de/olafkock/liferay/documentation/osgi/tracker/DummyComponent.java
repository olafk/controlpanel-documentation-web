package de.olafkock.liferay.documentation.osgi.tracker;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import de.olafkock.liferay.documentation.portletfilter.ContentInitializer;


/**
 * This plugin contains a lot of dynamically (manually) registered services. In order to 
 * shortcut their configuration, this DummyComponent listens to configuration events and
 * forwards them to a static member that is used by those other services.
 * 
 * Yes, it's a weird hack. But so simple and hopefully won't bite hard in future updates
 * 
 * @author Olaf Kock 
 */

@Component(
		configurationPid = "de.olafkock.liferay.documentation.osgi.tracker.ControlPanelDocumentationConfiguration"
		)
public class DummyComponent {
	public static final Log log = LogFactoryUtil.getLog(DummyComponent.class);

	@Reference
	protected void setConfigurationProvider(ConfigurationProvider configurationProvider) {
	    // configuration update will actually be handled in the @Modified event,
		// which will only be triggered in case we have a @Reference to the 
		// ConfigurationProvider
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		ControlPanelDocumentationConfiguration config = ConfigurableUtil.createConfigurable(ControlPanelDocumentationConfiguration.class, properties);
		log.info( (config.generateContent()?"":"no") 
				+ " content will be generated"
				+ (config.generateContent()?" to "+config.generateContentDirectory():"")
				+ " - Configuration for ControlPanelDocumentation" 

		); 
	    ContentInitializer.setConfiguration(config);
	}
}
