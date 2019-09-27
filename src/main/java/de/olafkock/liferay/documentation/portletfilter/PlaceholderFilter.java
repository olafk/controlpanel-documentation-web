package de.olafkock.liferay.documentation.portletfilter;

import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;

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
		String temp = " "; // + sr.getParameter("p_p_state");

		String string = "<div style=\"float:right; "
				+ "background-color:#ffcccc; "
				+ "line-height:1.5; "
				+ "z-index:99;\">" 
				+ portletName 
				+ "<br/>" 
				+ getSecondaryTopic(request) 
				+ temp
				+ "</div>";
		return string;
	}

	@Override
	protected String getFooterContent(RenderRequest request) {
		return "<div style=\"background-color:#ffcccc; position:fixed; bottom:0; width:auto; height:3em; padding:10px;\">"
				+ portletName + "<br/>" + getSecondaryTopic(request) + "</div>";
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
