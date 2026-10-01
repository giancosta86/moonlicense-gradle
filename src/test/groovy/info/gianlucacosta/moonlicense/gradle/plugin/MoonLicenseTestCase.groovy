package info.gianlucacosta.moonlicense.gradle.plugin

import info.gianlucacosta.moonlicense.gradle.dsl.MoonLicenseSettings
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.rules.TemporaryFolder


abstract class MoonLicenseTestCase extends GroovyTestCase {
    private TemporaryFolder tempFolder = new TemporaryFolder()
    private Project project

    @Override
    void setUp() {
        super.setUp()

        tempFolder.create()

        project = ProjectBuilder.builder()
                .withName("MoonTest")
                .withProjectDir(tempFolder.getRoot())
                .build()

        project.plugins.apply("maven")
        project.plugins.apply("info.gianlucacosta.moonlicense")
    }


    @Override
    void tearDown() {
        tempFolder.delete()

        super.tearDown();
    }


    protected Project getProject() {
        return project
    }

    protected MoonLicenseSettings getSettings() {
        return project.moonLicense
    }
}
