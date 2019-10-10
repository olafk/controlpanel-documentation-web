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

/**
 * This portletfilter will just display keys required to create content for
 * the currently displayed Control Panel Portlet, in addition to the otherwise
 * unchanged original portlet. 
 * 
 * @author Olaf Kock
 */

public class PlaceholderFilter extends BaseFilter {

	private String portletName;

	public PlaceholderFilter(String portletName, ContentInitializer contentInitializer) {
		super(contentInitializer);
		this.portletName = portletName;
	}

	protected String getHeaderContent(RenderRequest request) {
//		HttpServletRequest hsr = PortalUtil.getHttpServletRequest(request);
//		ServletRequest sr = PortalUtil.getOriginalServletRequest(hsr);
//		String temp = " "; // + sr.getParameter("p_p_state");
//
//		String string = "<div style=\"float:right; "
//				+ "background-color:#ffcccc; "
//				+ "line-height:1.5; "
//				+ "z-index:99;\">" 
//				+ portletName 
//				+ "<br/>" 
//				+ getSecondaryTopic(request) 
//				+ temp
//				+ "</div>";
//		return string;
		return "";
	}

	@Override
	protected String getFooterContent(RenderRequest request) {
		ResourceBundle bundle = ResourceBundleUtil.getBundle(PortalUtil.getLocale(request), this.getClass());
		String createOnGithub = LanguageUtil.get(bundle, "create-on-github");
		StringBuffer content = new StringBuffer();
		content.append("<div style=\"background-color:#ffcccc; position:fixed; bottom:0; width:auto; height:3em; padding:10px;\">");
		content.append("<span style=\"float:right; \">&nbsp;");
		String createURLPrefix = StringUtil.replace(this.contentInitializer.getRepositoryURLPrefix(), "/blob/", "/new/");
		content.append("<a href=\"" 
				+ createURLPrefix
				+ "?filename="
				+ getSuggestedFile(request)
				+ "&value="
				+ HtmlUtil.escapeURL(contentInitializer.generateMarkdown(request, getSuggestedFile(request)))
				+ "\" target=\"_blank\">" + createOnGithub + "</a>");
		content.append("</span>");
		content.append(portletName);
		content.append("<br/>");
		content.append(getSecondaryTopic(request));
		content.append("</div>");
		return content.toString();
	}

	protected boolean isFiltered(RenderRequest request, ThemeDisplay themeDisplay) {
		HttpServletRequest hsr = PortalUtil.getHttpServletRequest(request);
		ServletRequest sr = PortalUtil.getOriginalServletRequest(hsr);

		boolean doFilter = themeDisplay.getTheme().isControlPanelTheme()
				&& ! portletName.equals("com_liferay_product_navigation_product_menu_web_portlet_ProductMenuPortlet")
				&& ! portletName.equals("com_liferay_marketplace_store_web_portlet_MarketplaceStorePortlet")
				&& ! portletName.equals("com_liferay_marketplace_store_web_portlet_MarketplacePurchasedPortlet")
				&& ! "pop_up".equals(sr.getParameter("p_p_state"))
				;
		return doFilter;
	}
	
	@Override
	protected String getSuggestedFile(RenderRequest request) {
		String secondaryTopic = getSecondaryTopic(request);
		String name = "";
		if("-".equals(secondaryTopic)) {
			name = portletName + ".md";
		} else {
			name = portletName + "/" + secondaryTopic + ".md";
		}
		return name;
	}
	
	protected String getPrimaryTopic() {
		return portletName;
	}
}
