package de.olafkock.liferay.documentation.portletfilter;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import javax.portlet.RenderRequest;

import de.olafkock.liferay.documentation.osgi.tracker.ControlPanelDocumentationConfiguration;
import de.olafkock.liferay.documentation.resources.PortletDocumentation;
import de.olafkock.liferay.documentation.resources.URLConfig;

/**
 * Temporary class to generate empty Markdown files for any content that
 * might have been requested.
 * Everything is hardcoded here. Ugly, but works for me :)
 * Did I mention that this is temporary?
 *  
 * @author Olaf Kock 
 *
 */
public class ContentInitializer {
	public static final Log log = LogFactoryUtil.getLog(ContentInitializer.class);
	
	public ContentInitializer(Map<String, PortletDocumentation> content) {
		this.content = content;
	}
	
	public void createTemplate(RenderRequest request, BaseFilter filter) throws IOException {
		if(_myConfiguration == null) {
			log.info("no configuration available - skipping template creation");
			return;
		}
		if(_myConfiguration.generateContent()) {
			rememberFilter(request, filter);
			
			String suggestedFile = filter.getSuggestedFile(request);
			File localdocs = new File(_myConfiguration.generateContentDirectory(), suggestedFile);
			if(! localdocs.exists()) {
				if(!localdocs.getParentFile().exists()) {
					createDirectory(localdocs.getParentFile());
				}
				localdocs.createNewFile();
				PrintWriter out = new PrintWriter(localdocs, "UTF-8");
				out.println("# Headline\n");
				out.println("## Documentation\n");
				out.println("* Sorry, no documentation linked yet - please contribute\n");
				out.println("## Related Topics\n\n");
				out.println("## Community Resources\n\n");
				out.println("### Contribute\n");
				out.println("[Edit this file on github](" + _myConfiguration.contentURLPrefix() + "/" + suggestedFile + ")");
				out.close();
			}
		}
	}
	

	/**
	 * temporary feature: Create json configuration for all pages that have been
	 * seen so far.
	 */
	public void dumpConfig() {
		if(_myConfiguration == null) {
			log.info("no configuration available - skipping content.json dump");
			return;
		}
		if(_myConfiguration.generateContent()) {
			log.debug("dumping Config to " + _myConfiguration.generateContentDirectory() + "/content.json");
			PortletDocumentation[] docs = new PortletDocumentation[content.size()];
			int i = 0;
			
			Collection<PortletDocumentation> configs = content.values();
			for (PortletDocumentation doc : configs) {
				docs[i++] = doc;
			}
			String json = JSONFactoryUtil.looseSerializeDeep(docs);
			PrintWriter writer;
			try {
				writer = new PrintWriter(_myConfiguration.generateContentDirectory() + "/content.json");
				writer.println(json);
				writer.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace(System.err);
			}
		}
	}

	public String getRepositoryURLPrefix() {
		return _myConfiguration.repositoryURLPrefix();
	}
	
	
	private void rememberFilter(RenderRequest request, BaseFilter f) {
		String primary = f.getPrimaryTopic();
		String secondary = f.getSecondaryTopic(request);
		PortletDocumentation doc = content.get(primary);
		if(doc == null) {
			doc = new PortletDocumentation();
			doc.portletId = primary;
			doc.urlConfig = new URLConfig[0];
			content.put(primary, doc);
		}
		rememberUrlConfig(doc, primary, secondary);
	}

	private void rememberUrlConfig(PortletDocumentation doc, String primary, String secondary) {
		// make sure we always have a "secondary" urlconfig as first entry by 
		// checking this before all other operations
		URLConfig theConfig = doc.getSecondaryUrlConfig("-");
		if(theConfig == null) {
			URLConfig uc = addUrlConfig(doc, primary, "-");
			uc.documentationURL = _myConfiguration.contentURLPrefix() + primary + ".html";
			uc.secondary = "-";
		}
		theConfig = doc.getSecondaryUrlConfig(secondary);
		if(theConfig == null) {
			URLConfig uc = addUrlConfig(doc, primary, secondary);
			uc.documentationURL = _myConfiguration.contentURLPrefix() + primary + "/" + secondary + ".html";;
			uc.secondary = secondary;
		}
	}

	private URLConfig addUrlConfig(PortletDocumentation doc, String primary, String string) {
		if(doc.urlConfig == null) {
			doc.urlConfig = new URLConfig[0];
		}
		URLConfig[] current = doc.urlConfig;
		URLConfig[] next = Arrays.copyOf(current, current.length+1);
		doc.urlConfig = next;
		URLConfig newValue = new URLConfig();
		doc.urlConfig[doc.urlConfig.length-1] = newValue;
		
		return newValue;
	}

	private static void createDirectory(File file) {
		if(file.exists() && file.isDirectory()) {
			return;
		} else if(!file.getParentFile().exists()) {
			createDirectory(file.getParentFile());
		} 
		file.mkdir();
	}
	
	private static ControlPanelDocumentationConfiguration _myConfiguration;
	
	public static void setConfiguration(ControlPanelDocumentationConfiguration configuration) {
		_myConfiguration = configuration;
	}

	public static ControlPanelDocumentationConfiguration getConfiguration() {
		return _myConfiguration;
	}
	
	private final Map<String, PortletDocumentation> content;

}
