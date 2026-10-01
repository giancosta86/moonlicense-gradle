package info.gianlucacosta.moonlicense.gradle.plugin

import info.gianlucacosta.moonlicense.DefaultStringNoticeService
import info.gianlucacosta.moonlicense.DefaultTreeNoticeService
import info.gianlucacosta.moonlicense.TreeNoticeService
import info.gianlucacosta.moonlicense.gradle.dsl.MoonLicenseSettings
import org.gradle.api.DefaultTask

/**
 * Generic notice-related task.
 */
abstract class NoticesTask extends DefaultTask {
    protected void run() {
        MoonLicenseSettings settings = project.moonLicense

        TreeNoticeService treeNoticeService = createTreeNoticeService(settings)


        if (settings.updateLicenseFile) {
            updateLicenseFile(settings);
        }


        updateNotices(treeNoticeService);


        boolean isMavenApplied = (project.getPlugins().findPlugin("maven") != null)
        if (isMavenApplied && settings.updatePom) {
            updatePom(settings)
        }
    }


    private static TreeNoticeService createTreeNoticeService(MoonLicenseSettings settings) {
        TreeNoticeService treeNoticeService = new DefaultTreeNoticeService(
                new DefaultStringNoticeService(),
                settings.skipDotItems
        )

        settings.fileSets.forEach { licensedFileSet -> treeNoticeService.addLicensedFileSet(licensedFileSet) }

        treeNoticeService.addLicensedFileSet(settings.getLicensedFileSet())

        return treeNoticeService
    }


    protected abstract void updateLicenseFile(MoonLicenseSettings settings);

    protected void updateNotices(TreeNoticeService treeNoticeService) {
        int openedFiles = 0
        int writtenFiles = 0

        treeNoticeService.addOnFileOpeningHandler { path ->
            openedFiles++
        }

        treeNoticeService.addOnFileWrittenHandler { path ->
            getLogger().debug("Notice updated for '{}'", path)
            writtenFiles++
        }


        runNoticesOperation(treeNoticeService, project.projectDir)

        if (writtenFiles == 0) {
            getLogger().info("No license notices updated")
        } else {
            getLogger().warn("Files updated: {}. Total files checked: {}.", writtenFiles, openedFiles)
        }
    }

    protected abstract void runNoticesOperation(TreeNoticeService treeNoticeService, File projectDir);

    protected abstract void updatePom(MoonLicenseSettings settings);

    protected File getLicenseTextFile() {
        return project.file("LICENSE")
    }
}
