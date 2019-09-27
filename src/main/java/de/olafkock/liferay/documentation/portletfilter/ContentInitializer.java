package de.olafkock.liferay.documentation.portletfilter;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;

import javax.portlet.RenderRequest;

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
	
	public ContentInitializer(Map<String, PortletDocumentation> configuration) {
		this.configuration = configuration;
	}
	
	public void createTemplate(RenderRequest request, BaseFilter filter) throws IOException {
		rememberFilter(request, filter);
		
//		String suggestedFile = filter.getSuggestedFile(request);
//		File localdocs = new File("/home/olaf/ds72-workspace/controlpanel-documentation-docs/md/72en", suggestedFile);
//		if(! localdocs.exists()) {
//			if(!localdocs.getParentFile().exists()) {
//				createDirectory(localdocs.getParentFile());
//			}
//			localdocs.createNewFile();
//			PrintWriter out = new PrintWriter(localdocs, "UTF-8");
//			out.println("# Headline\n");
//			out.println("## Documentation\n");
//			out.println("* Sorry, no documentation linked yet - please contribute\n");
//			out.println("## Related Topics\n\n");
//			out.println("## Community Resources\n\n");
//			out.println("### Contribute\n");
//			out.println("[Edit this file on github](https://github.com/olafk/controlpanel-documentation-docs/blob/master/md/72en/" + suggestedFile + ")");
//			out.close();
//		}
	}
	
	private void rememberFilter(RenderRequest request, BaseFilter f) {
		String primary = f.getPrimaryTopic();
		String secondary = f.getSecondaryTopic(request);
		PortletDocumentation doc = configuration.get(primary);
		if(doc == null) {
			doc = new PortletDocumentation();
			doc.portletId = primary;
			doc.urlConfig = new URLConfig[0];
			configuration.put(primary, doc);
		}
		rememberUrlConfig(doc, primary, secondary);
	}

	private void rememberUrlConfig(PortletDocumentation doc, String primary, String secondary) {
		// make sure we always have a "secondary" urlconfig as first entry by 
		// checking this before all other operations
		URLConfig theConfig = doc.getSecondaryUrlConfig("-");
		if(theConfig == null) {
			URLConfig uc = addUrlConfig(doc, primary, "-");
			uc.documentationURL = "https://www.olafkock.de/liferay/controlpaneldocumentation/" + primary + ".html";
			uc.secondary = "-";
		}
		theConfig = doc.getSecondaryUrlConfig(secondary);
		if(theConfig == null) {
			URLConfig uc = addUrlConfig(doc, primary, secondary);
			uc.documentationURL = "https://www.olafkock.de/liferay/controlpaneldocumentation/" + primary + "/" + secondary + ".html";;
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
	
	private final Map<String, PortletDocumentation> configuration;
}
