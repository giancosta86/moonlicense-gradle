package info.gianlucacosta.moonlicense.gradle.plugin

import info.gianlucacosta.moonlicense.License
import info.gianlucacosta.moonlicense.ProductInfoInjector
import info.gianlucacosta.moonlicense.TreeNoticeService
import info.gianlucacosta.moonlicense.gradle.dsl.MoonLicenseSettings
import org.gradle.api.tasks.TaskAction

import java.nio.file.Files
import java.time.Year

/**
 * Task employing MoonLicense to set the license notice for a subset of the project files.
 */
class SetNoticesTask extends NoticesTask {
    @TaskAction
    def setNotices() {
        run()
    }


    @Override
    protected void updateLicenseFile(MoonLicenseSettings settings) {
        File licenseTextFile = getLicenseTextFile()

        String initialLicenseFileContent = licenseTextFile.exists() ?
                licenseTextFile.getText("utf-8")
                :
                null


        String fullTemplate = settings.license.fullTemplate

        ProductInfoInjector productInfoInjector = new ProductInfoInjector(settings.productInfo)
        String fullText = productInfoInjector.inject(fullTemplate)

        if (fullText.equals(initialLicenseFileContent)) {
            return
        }

        Files.write(
                licenseTextFile.toPath(),
                fullText.getBytes()
        )

        getLogger().warn("License text file updated")
    }

    @Override
    protected void runNoticesOperation(TreeNoticeService treeNoticeService, File projectDir) {
        treeNoticeService.setNotices(projectDir, Year.now().getValue())
    }

    @Override
    protected void updatePom(MoonLicenseSettings settings) {
        List<License> projectLicenseSet = [settings.license]

        settings.fileSets.forEach {
            licensedFileSet ->
                License currentLicense = licensedFileSet.license
                if (!projectLicenseSet.contains(currentLicense)) {
                    projectLicenseSet.add(currentLicense);
                }
        }

        project.uploadArchives {
            repositories {
                mavenDeployer {
                    pom.project {
                        inceptionYear settings.productInfo.inceptionYear

                        licenses {
                            projectLicenseSet.forEach {
                                currentLicense ->
                                    license {
                                        name currentLicense.name
                                        url currentLicense.url
                                        distribution "repo"
                                    }
                            }

                        }
                    }
                }
            }
        }

        getLogger().warn("POM updated")
    }
}
