package de.olafkock.liferay.documentation.portletfilter;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ResourceBundle;

import javax.portlet.RenderRequest;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import de.olafkock.liferay.documentation.resources.PortletDocumentation;
import de.olafkock.liferay.documentation.resources.URLConfig;

/**
 * This PortletFilter will add media elements before the original portlet's
 * content and a "toast" style "additional information" afterwards. Everything
 * is self-contained, thus the styling is hardcoded in order to not rely on
 * additional external plugins to bring the CSS and JS 
 * (for the price of being hardcoded, and partially challenging to read)
 * 
 * @author Olaf Kock
 */
public class DocumentationFilter extends BaseFilter {

	final PortletDocumentation portletDocumentation;
	private final String elementId;
	private final String showId;
	private final String hideId;
	private final String videoId;
	private final String containerId;
	private final String showJS;
	private final String hideJS;
	private final String largerVideoJS;
	private final String smallerVideoJS;
	private final String hideVideoJS;

	public DocumentationFilter(PortletDocumentation portletDocumentation, ContentInitializer contentInitializer) {
		super(contentInitializer);
		this.portletDocumentation = portletDocumentation;
		String namespace = PortalUtil.getPortletNamespace(portletDocumentation.portletId);
		elementId = namespace + "_additionalDocumentation";
		showId = elementId + "_show";
		hideId = elementId + "_hide";
		videoId = elementId + "_video";
		containerId = elementId + "_container";
		showJS = "document.getElementById('" + elementId + "').style.height='50%';" + "document.getElementById('"
				+ showId + "').style.color='#999999';" + "document.getElementById('" + hideId
				+ "').style.color='#000000';";
		hideJS = "document.getElementById('" + elementId + "').style.height='2em';" + "document.getElementById('"
				+ showId + "').style.color='#000000';" + "document.getElementById('" + hideId
				+ "').style.color='#999999';";
		largerVideoJS = "var videoElement = document.getElementById('" + videoId + "');"
				+ "videoElement.parentNode.parentNode.style.position = 'absolute';"
				+ "videoElement.style.maxHeight='24em'; "
				+ "videoElement.style.removeProperty('margin-bottom');"  
				+ "var containerElement = document.getElementById('" + containerId + "');"
				+ "containerElement.style.removeProperty('height');"
				+ ";";
		smallerVideoJS = "var videoElement = document.getElementById('" + videoId + "');"
				+ "videoElement.parentNode.parentNode.style.position = 'relative';"
				+ "videoElement.style.maxHeight='8em'; "
				+ "videoElement.style.removeProperty('margin-bottom');"  
				+ "var containerElement = document.getElementById('" + containerId + "');"
				+ "containerElement.style.removeProperty('height');"
				;
		hideVideoJS = "var videoElement = document.getElementById('" + videoId + "');"
				+ "videoElement.parentNode.parentNode.style.position = 'relative';"
				+ "videoElement.style.maxHeight='2em';"
				+ "videoElement.style.marginBottom='-0.8em';"  
				+ "var containerElement = document.getElementById('" + containerId + "');"
				+ "containerElement.style.height = containerElement.nextElementSibling.offsetHeight + 'px';"
				;
	}

	protected String getHeaderContent(RenderRequest request) {
		String secondaryTopic = getSecondaryTopic(request);
		URLConfig uc = portletDocumentation.getSecondaryUrlConfig(secondaryTopic);
		if (uc == null)
			return "";

		ResourceBundle bundle = ResourceBundleUtil.getBundle(PortalUtil.getLocale(request), this.getClass());
		String largerLabel = LanguageUtil.get(bundle, "larger[command]");
		String smallerLabel = LanguageUtil.get(bundle, "smaller[command]");
		String minLabel = LanguageUtil.get(bundle, "min[command]");

		
		String result = "<div style=\"float:right; line-height:1.5; padding-bottom:0; z-index:99;\""
				+ "id=\"" + containerId + "\" >";
		if (null == uc.mediaType) {
			// ignore
		} else if ("audio".equals(uc.mediaType)) {
			result += "<audio"
					+ " controls=\"play\""
					+ " preload=\"metadata\""
					+ " style=\"width:300px; max-height:2.5em; \""
					+ " onplay=\"" + showJS + "\">\n" 
					+ "	<source"
					+ " src=\"" + uc.mediaURL + "\" "
					+ "type=\"audio/mpeg\">\n" 
					+ "	<a href=\"" + uc.mediaURL + "\" target=\"_blank\">Audio documentation</a>\n" 
					+ "</audio>";
		} else if ("video".equals(uc.mediaType)) {
			int videoHeight = Math.max(0, ContentInitializer.getConfiguration().defaultVideoHeight());
			if(videoHeight > 0) {
				videoHeight = Math.max(videoHeight, 2);
				result += "<div style=\"position:relative; display:block; z-index:1000; top:0; right:0;\""
						+ " class=\"navigation-bar-secondary\">"
						+ "<span style=\"display:block;\">"
						+ "<video"
						+ " controls preload=\"metadata\""
						+ " style=\"top:2em; right:0px; max-height:" + videoHeight + "em; padding-bottom:0;"
						    + "transition-property: size top height; transition-duration: 1s; transition-timing-function: ease;\""
						+ " id=\"" + videoId + "\""
						+ " onplay=\"" + showJS + "\">\n" 
						+ "<source"
						+ " src=\"" + uc.mediaURL + "\""
						+ " type=\"video/mp4\">\n" 
						+ "	<a href=\"" + uc.mediaURL + "\" target=\"_blank\">Video documentation</a>\n" 
						+ "</video></span>"
						+ "<span style=\"display:block; z-index:999; font-size:0.8rem; line-height:1rem; text-align:right;\">"
						+ "<span onclick=\"" + largerVideoJS + "\" style=\"cursor:pointer;\">" + largerLabel + "</span> / "
						+ "<span onclick=\"" + smallerVideoJS + "\" style=\"cursor:pointer;\">" + smallerLabel + "</span> / "
						+ "<span onclick=\"" + hideVideoJS + "\" style=\"cursor:pointer;\">" + minLabel + "</span>"
						+ "</span>"
						+ "</div>"
						;
			}
		} else if ("youtube".equals(uc.mediaType)) {
			result += "<iframe id=\"" + videoId + "\" max-width=\"300\" max-height=\"8em\" src=\"" + uc.mediaURL
					+ "\" frameborder=\"0\" allow=\"accelerometer; autoplay; encrypted-media; "
					+ "gyroscope; picture-in-picture\" allowfullscreen></iframe>\n";
		} else {
			result += "unknown mediaType " + HtmlUtil.escape(uc.mediaType);
		}
		result += "</div>";
		return result;
	}

	protected String getFooterContent(RenderRequest request) {
		String secondaryTopic = getSecondaryTopic(request);
		URLConfig uc = portletDocumentation.getSecondaryUrlConfig(secondaryTopic);

		StringBuilder content = new StringBuilder();

		ResourceBundle bundle = ResourceBundleUtil.getBundle(PortalUtil.getLocale(request), this.getClass());
		if (uc != null && uc.documentationURL != null && !"".equals(uc.documentationURL.trim())) {
			content.append("<div id=\"");
			content.append(elementId);
			content.append("\" style=\"background-color:#cccccc; position:fixed; bottom:0; "
					+ "width:auto; width:40%; min-height:1em; z-index:10;" 
					+ "transition-property: size top height; "
					+ "transition-duration: 1s; transition-timing-function: ease; padding:10px;"
					+ "height:");
			content.append(""+ ContentInitializer.getConfiguration().defaultDocHeight());
			content.append("em;\">");
			
			String moreDocumentationLabel = LanguageUtil.get(bundle, "more-documentation-and-pointers");
			String editOnGithubLabel = LanguageUtil.get(bundle, "edit-on-github");
			String openInNewWindowLabel = LanguageUtil.get(bundle, "open-in-new-window");
			String clickDownLabel = LanguageUtil.get(bundle, "click-to-slide-down");
			String clickUpLabel = LanguageUtil.get(bundle, "click-to-slide-up");
			String editLabel = LanguageUtil.get(bundle, "edit[command]");
			String openLabel = LanguageUtil.get(bundle, "open[command]");
			String hideLabel = LanguageUtil.get(bundle, "hide[command]");

			content.append((uc == null) ? "" : "<span "
					+ " style=\"cursor: pointer; \""
					+ " id=\"" + showId + "\""
					+ " onclick=\"" + showJS + "\""
					+ " title=\"" + clickUpLabel + "\">" 
					+ moreDocumentationLabel 
					+ "</span> / ");
			
//			content.append("<span style=\"float:right; \">&nbsp;");
			content.append("<a href=\"" + HtmlUtil.escape(uc.documentationURL) + "\""
					+ " target=\"_blank\"" 
					+ " title=\"" + openInNewWindowLabel + "\" >" + openLabel + "</a> / ");
			content.append("<a href=\"" 
					+ this.contentInitializer.getRepositoryURLPrefix()
					+ getSuggestedFile(request) + "\""
					+ " target=\"_blank\""
					+ " title=\"" + editOnGithubLabel + "\">" 
					+ editLabel 
					+ "</a> / ");
			content.append(
					"<span style=\"cursor: pointer; \""
					+ " id=\"" + hideId + "\""
					+ " onclick=\"" + hideJS + "\""
					+ " title=\"" + clickDownLabel + "\">" 
					+ hideLabel 
					+ "</span>");
//			content.append("</span>");
			content.append("<br/>");
			content.append("<iframe src=\"");
			content.append(HtmlUtil.escape(uc.documentationURL));
			content.append("\" width=\"100%\" height=\"90%\" > </iframe>");
			content.append("</div>");
		} else {
			content.append("<div id=\"");
			content.append(elementId);
			content.append("\" style=\"background-color:#cccccc; position:fixed; bottom:0; "
					+ "width:auto; height:2em; width:40%; min-height:1em; z-index:10;" 
					+ "transition-property: size top height; "
					+ "transition-duration: 1s; transition-timing-function: ease; padding:10px;\">");

			String createOnGithubLabel = LanguageUtil.get(bundle, "create-on-github");
			String showMainDocLabel = LanguageUtil.get(bundle, "show-main-doc");
			String clickDownLabel = LanguageUtil.get(bundle, "click-to-slide-down");
			String clickUpLabel = LanguageUtil.get(bundle, "click-to-slide-up");
			String hideLabel = LanguageUtil.get(bundle, "hide[command]");

			URLConfig mainDocUC = portletDocumentation.getSecondaryUrlConfig("-");
			if(mainDocUC != null && mainDocUC.documentationURL != null && ! mainDocUC.documentationURL.isEmpty()) {
				content.append("<span "
						+ " style=\"cursor: pointer; \""
						+ " id=\"" + showId + "\""
						+ " onclick=\"" + showJS 
						+ "\" title=\"" + clickUpLabel + "\">" 
						+ showMainDocLabel 
						+ "</span> / ");
				content.append(
						"<span style=\"cursor: pointer; color:#999999; \""
						+ " id=\"" + hideId + "\""
						+ " onclick=\"" + hideJS + "\""
						+ " title=\"" + clickDownLabel + "\">" 
						+ hideLabel 
						+ "</span>");
			}

			content.append("<span style=\"float:right; \">&nbsp;");
			String createURLPrefix = StringUtil.replace(this.contentInitializer.getRepositoryURLPrefix(), "/blob/", "/new/");
			content.append("<a href=\"" 
					+ createURLPrefix
					+ "?filename="
					+ getSuggestedFile(request)
					+ "&value="
					+ HtmlUtil.escapeURL(contentInitializer.generateMarkdown(request, getSuggestedFile(request)))
					+ "\" target=\"_blank\">" + createOnGithubLabel + "</a>");
			content.append("</span>");
			content.append("<br/>");
			content.append("<iframe src=\"");
			content.append(HtmlUtil.escape(mainDocUC.documentationURL));
			content.append("\" width=\"100%\" height=\"90%\" > </iframe>");
			if(ContentInitializer.getConfiguration().showUndocumentedKeys()) {
				content.append(portletDocumentation.portletId);
				content.append(" | ");
				content.append(getSecondaryTopic(request));
			}
			content.append("</div>");
		}

		return content.toString();
	}

	protected boolean isFiltered(RenderRequest request, ThemeDisplay themeDisplay) {
		HttpServletRequest hsr = PortalUtil.getHttpServletRequest(request);
		ServletRequest sr = PortalUtil.getOriginalServletRequest(hsr);

		boolean doFilter = themeDisplay.getTheme().isControlPanelTheme()
				&& ! portletDocumentation.portletId.equals(
						"com_liferay_product_navigation_product_menu_web_portlet_ProductMenuPortlet")
				&& ! portletDocumentation.portletId.equals(
						"com_liferay_marketplace_store_web_portlet_MarketplaceStorePortlet")
				&& ! portletDocumentation.portletId.equals(
						"com_liferay_staging_processes_web_portlet_StagingProcessesPortlet")
				&& ! "pop_up".equals(sr.getParameter("p_p_state"));
		return doFilter;
	}

	@Override
	protected String getSuggestedFile(RenderRequest request) {
		String secondaryTopic = getSecondaryTopic(request);
		if("-".equals(secondaryTopic)) {
			return portletDocumentation.portletId + ".md";
		} else {
			return portletDocumentation.portletId + "/" + secondaryTopic + ".md";
		}
	}
	
	protected String getPrimaryTopic() {
		return portletDocumentation.portletId;
	}
}
