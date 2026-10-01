package info.gianlucacosta.moonlicense.gradle.plugin

import info.gianlucacosta.moonlicense.TreeNoticeService
import info.gianlucacosta.moonlicense.gradle.dsl.MoonLicenseSettings
import org.gradle.api.GradleScriptException
import org.gradle.api.tasks.TaskAction

/**
 * Task employing MoonLicense to remove the license notice from a subset of the project files.
 */
class RemoveNoticesTask extends NoticesTask {
    @TaskAction
    def removeNotices() {
        run()
    }

    @Override
    protected void updateLicenseFile(MoonLicenseSettings settings) {
        File licenseTextFile = getLicenseTextFile()

        if (!licenseTextFile.isFile()) {
            return;
        }

        if (!licenseTextFile.delete()) {
            throw new GradleScriptException("Cannot delete the license text file", null)
        }


        getLogger().warn("License text file deleted");
    }

    @Override
    protected void runNoticesOperation(TreeNoticeService treeNoticeService, File projectDir) {
        treeNoticeService.removeNotices(projectDir);
    }

    @Override
    protected void updatePom(MoonLicenseSettings settings) {
        //Just do nothing
    }
}
