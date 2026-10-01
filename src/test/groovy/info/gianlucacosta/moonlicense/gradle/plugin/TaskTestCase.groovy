package info.gianlucacosta.moonlicense.gradle.plugin

import info.gianlucacosta.moonlicense.ProductInfo
import info.gianlucacosta.moonlicense.ProductInfoInjector
import info.gianlucacosta.moonlicense.testing.TestTreeService
import info.gianlucacosta.moonlicense.testing.licenses.dummy.Dummy
import info.gianlucacosta.moonlicense.testing.licenses.dummy2.Dummy2
import org.gradle.api.Task

import java.nio.file.Files
import java.nio.file.Path
import java.time.Year

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotEquals


abstract class TaskTestCase extends MoonLicenseTestCase {

    protected void testTreeTask(String taskName) {
        project.moonLicense {
            license = new Dummy()
            productInfo = new ProductInfo("Test product", Year.now().getValue(), "Any holder")

            includes = [
                    /\.java\.txt$/: javaFormat,
                    /\.html?$/    : htmlFormat
            ]

            excludes = [
                    '/Sigma\\.java\\.txt$'
            ]


            fileSet {
                license = new Dummy2()
                productInfo = new ProductInfo("Special product", Year.now().getValue(), "Special copyright holder");

                includes = [
                        '/Omicron\\.java\\.txt$': javaFormat
                ]
            }
        }

        TestTreeService testTreeService = new TestTreeService()
        String treeName = taskName
        testTreeService.copyInitialTreeToDirectory(treeName, project.projectDir)

        Task task = project.tasks[taskName]
        task.execute()

        checkProjectTree(testTreeService, treeName)
    }


    private void checkProjectTree(TestTreeService testTreeService, String treeName)
            throws IOException {

        ProductInfoInjector productInfoInjector = new ProductInfoInjector(project.moonLicense.productInfo);
        Path projectDirPath = project.projectDir.toPath()

        int checkedFiles = 0


        Files.walk(projectDirPath).forEach { currentPath ->
            if (!currentPath.toFile().isFile()) {
                return;
            }

            Path relativePath = projectDirPath.relativize(currentPath);

            String expectedTemplate
            try {
                expectedTemplate = testTreeService.getExpectedFileContent(treeName, relativePath.toString());
            } catch (IOException ex) {
                return
            }


            String expectedContent = productInfoInjector.inject(expectedTemplate)

            String fileContent = new String(Files.readAllBytes(currentPath))

            assertEquals("Difference found in file: " + relativePath, expectedContent, fileContent)

            checkedFiles++
        }

        System.out.println("Checked files: " + checkedFiles)
        assertNotEquals(0, checkedFiles)
    }
}
