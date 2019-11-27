package de.olafkock.liferay.documentation.portletfilter;

import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.IOException;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.filter.FilterChain;
import javax.portlet.filter.FilterConfig;
import javax.portlet.filter.RenderFilter;

/**
 * Just common functionality for the PortletFilters in this module, that I
 * didn't want to duplicate...
 * 
 * @author Olaf Kock
 */

public abstract class BaseFilter implements RenderFilter {

	protected ContentInitializer contentInitializer;

	public BaseFilter(ContentInitializer contentInitializer) {
		super();
		this.contentInitializer = contentInitializer;
	}

	@Override
	public void init(FilterConfig filterConfig) throws PortletException {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(RenderRequest request, RenderResponse response, FilterChain chain) 
			throws IOException, PortletException {
			ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
			boolean isFiltered = isFiltered(request, themeDisplay);
	
			if (isFiltered) {
				response.getWriter().append(getHeaderContent(request));
			}
	
			chain.doFilter(request, response);

			if (isFiltered) {
				response.getWriter().append(getFooterContent(request));
				// temporarily: Make sure to create content for every possible
				// primary/secondary combination that we see.
				contentInitializer.createTemplate(request, this);
			}
		}

	// cosmetic shortening of ever-the-same portletid-fragments during development
	protected String shorten(String portletId) {
		return portletId.replaceAll("com_liferay_", "cl..").replaceAll("_web_portlet_", "..wp..")
				.replaceAll("_web_internal_portlet_", "..wip..").replaceAll("_admin..w", "..aw");
	}

	protected abstract String getPrimaryTopic();
	
	/**
	 * This retrieves an indicator if the portlet utilizes different Tabs for different content.
	 * For some reason the tabs are not using consistent indicators. As we don't care about the 
	 * implementation detail, just about the chosen tab, they're all combined into the one 
	 * return value.
	 * 
	 * @param request
	 * @return whatever is used as the key for the tab. Typically alphanumeric, but can be 
	 *         numeric as well. "-" if no tab is selected, so that it can be used as non-null 
	 *         key to look up content. Safe to be used in HTML (value is already escaped)
	 */
	protected String getSecondaryTopic(RenderRequest request) {
		final String[] params = new String[] { "toolbarItem", "type", "navigation", "tab", 
				"tabs1", "tabs2", "configurationScreenKey", "pid", "factoryPid", "roleType",
				"mvcRenderCommandName",	"mvcPath"};
		for (String p : params) {
			String result = request.getParameter(p);
			if (result != null)
				return HtmlUtil.escape(simplify(result));
		}
		return "-";
	}

	private String simplify(String result) {
		if(result.startsWith("/")) {
			result = result.substring(1);
		}
		result = result.replace('/', '_');
		return result;
	}

	protected abstract String getFooterContent(RenderRequest request);

	protected abstract String getHeaderContent(RenderRequest request);

	protected abstract boolean isFiltered(RenderRequest request, ThemeDisplay themeDisplay);
	
	protected abstract String getSuggestedFile(RenderRequest request);
}