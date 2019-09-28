package de.olafkock.liferay.documentation.portletfilter;

import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;

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
	private final String showJS;
	private final String hideJS;

	public DocumentationFilter(PortletDocumentation portletDocumentation, ContentInitializer contentInitializer) {
		super(contentInitializer);
		this.portletDocumentation = portletDocumentation;
		String namespace = PortalUtil.getPortletNamespace(portletDocumentation.portletId);
		elementId = namespace + "_additionalDocumentation";
		showId = elementId + "_show";
		hideId = elementId + "_hide";
		showJS = "document.getElementById('" + elementId + "').style.height='50%';" + "document.getElementById('"
				+ showId + "').style.color='#999999';" + "document.getElementById('" + hideId
				+ "').style.color='#000000';";
		hideJS = "document.getElementById('" + elementId + "').style.height='2em';" + "document.getElementById('"
				+ showId + "').style.color='#000000';" + "document.getElementById('" + hideId
				+ "').style.color='#999999';";
	}

	protected String getHeaderContent(RenderRequest request) {
		String secondaryTopic = getSecondaryTopic(request);
		URLConfig uc = portletDocumentation.getSecondaryUrlConfig(secondaryTopic);
		if (uc == null)
			return "";
		
		String result = "<div style=\"float:right; line-height:1.5; padding-bottom:0;" + "z-index:99;\">";
		if (null == uc.mediaType) {
			// ignore
		} else if ("audio".equals(uc.mediaType)) {
			result += "<audio controls=\"play\" preload=\"metadata\" style=\" width:300px; max-height:2.5em; \""
					+ " onplay=\"" + showJS + "\">\n" 
					+ "	<source src=\"" + uc.mediaURL + "\" type=\"audio/mpeg\">\n" 
					+ "	<a href=\"" + uc.mediaURL + "\" target=\"_blank\">Audio documentation</a>\n" 
					+ "</audio>";
		} else if ("video".equals(uc.mediaType)) {
			result += "<video controls preload=\"metadata\" style=\" z-index:1000; top:2em; right:0px; max-height:8em; padding-bottom:0;\" "
					+ "onplay=\"" + showJS + "\">\n" 
					+ "	<source src=\"" + uc.mediaURL + "\" type=\"video/mp4\">\n" 
					+ "	<a href=\"" + uc.mediaURL + "\" target=\"_blank\">Video documentation</a>\n" 
					+ "</video>";
		} else if ("youtube".equals(uc.mediaType)) {
			result += "<iframe max-width=\"300\" max-height=\"8em\" src=\"" + uc.mediaURL
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

		StringBuilder content = new StringBuilder("<div id=\"");
		content.append(elementId);
		content.append("\" style=\"background-color:#cccccc; " + "position:fixed; " + "bottom:0; "
				+ "width:auto; height:4em; max-width:50%; " + "transition-property: size top height; "
				+ "transition-duration: 1s; " + "transition-timing-function: ease; " + "padding:10px;\">");
		if (uc != null && uc.documentationURL != null && !uc.documentationURL.isBlank()) {
			content.append((uc == null) ? "" : "More documentation and pointers ");
			content.append("<span style=\"float:right; \">");
			content.append("<a href=\"https://github.com/olafk/controlpanel-documentation-docs/blob/master/md/72en/"
					+ getSuggestedFile(request) + "\" target=\"_blank\">edit on github</a> / ");
			content.append("<span style=\"cursor: pointer; \" id=\"" + showId + "\" onclick=\"" + showJS
					+ "\">show</span> / ");
			content.append(
					"<span style=\"cursor: pointer; \" id=\"" + hideId + "\" onclick=\"" + hideJS + "\">hide</span>");
			content.append("</span>");
			content.append("<br/>");
			content.append("<iframe src=\"");
			content.append(HtmlUtil.escape(uc.documentationURL));
			content.append("\" width=\"100%\" height=\"80%\" > </iframe>");
		}
		content.append(portletDocumentation.portletId);
		content.append(" | ");
		content.append(getSecondaryTopic(request));
		content.append("</div>");

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
