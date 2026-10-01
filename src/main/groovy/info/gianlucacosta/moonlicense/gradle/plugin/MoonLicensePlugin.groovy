package info.gianlucacosta.moonlicense.gradle.plugin

import info.gianlucacosta.moonlicense.gradle.dsl.MoonLicenseSettings
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * MoonLicense plugin for Gradle.
 * <p>
 * It creates 2 tasks:
 * <ul>
 *     <li><b>setNotices</b>, of type <i>SetNoticesTask</i></li>
 *     <li><b>removeNotices</b>, of type <i>RemoveNoticesTask</i></li>
 * </ul>
 * <p>
 * It also makes the "moonLicense" block available at project level, to configure the plugin.
 */
class MoonLicensePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create("moonLicense", MoonLicenseSettings);

        project.task("setNotices", type: SetNoticesTask)
        project.task("removeNotices", type: RemoveNoticesTask)
    }
}
