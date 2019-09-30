package de.olafkock.liferay.documentation.osgi.tracker;

import aQute.bnd.annotation.metatype.Meta;

@Meta.OCD(
	    id = "de.olafkock.liferay.documentation.osgi.tracker.ControlPanelDocumentationConfiguration"
	    , localization = "content/Language"
	    , name = "controlpanel-documentation-configuration-name"
	)
public interface ControlPanelDocumentationConfiguration {
    
	@Meta.AD(
            deflt = "/home/olaf/ds72-workspace/controlpanel-documentation-docs/md/72en",
            description = "directory-for-generated-content-description",
            name = "directory-for-generated-content",
            required = false
        )
	public String generateContentDirectory();
    
    @Meta.AD(
            deflt = "https://github.com/olafk/controlpanel-documentation-docs/blob/master/md/72en/",
            description = "repository-url-prefix-description",
            name = "repository-url-prefix",
            required = false
        )
    public String repositoryURLPrefix();

	@Meta.AD(
            deflt = "https://www.olafkock.de/liferay/controlpaneldocumentation/",
            description = "content-url-prefix-description",
            name = "content-url-prefix",
            required = false
        )
	public String contentURLPrefix();
	
	@Meta.AD(
            deflt = "false",
            description = "generate-content-description",
            name = "generate-content",
            required = false
        )	
	public boolean generateContent();
}
