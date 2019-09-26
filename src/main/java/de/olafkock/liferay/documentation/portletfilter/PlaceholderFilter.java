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

	public PlaceholderFilter(String portletName) {
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
				&& ! "pop_up".equals(sr.getParameter("p_p_state"))
				;
		return doFilter;
	}
}
