package de.olafkock.liferay.documentation.osgi.tracker;

import aQute.bnd.annotation.metatype.Meta;

@Meta.OCD(
	    id = "de.olafkock.liferay.documentation.osgi.tracker.ControlPanelDocumentationConfiguration"
	    , localization = "content/Language"
	    , name = "controlpanel-documentation-configuration-name"
	)
public interface ControlPanelDocumentationConfiguration {
    
	@Meta.AD(
			deflt = "false",
			description = "show-undocumented-keys-description",
			name = "show-undocumented-keys",
			required = false
			)
	public boolean showUndocumentedKeys();
	
	@Meta.AD(
			deflt = "8",
			description = "default-video-height-description",
			name = "default-video-height",
			required = false
			)
	public int defaultVideoHeight();

	@Meta.AD(
			deflt = "4",
			description = "default-doc-height-description",
			name = "default-doc-height",
			required = false
			)
	public int defaultDocHeight();
	
	@Meta.AD(
            deflt = "/tmp/md/72en",
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
