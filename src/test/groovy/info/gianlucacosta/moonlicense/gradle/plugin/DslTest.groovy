package info.gianlucacosta.moonlicense.gradle.plugin

import info.gianlucacosta.moonlicense.LicensedFileSet
import info.gianlucacosta.moonlicense.ProductInfo
import info.gianlucacosta.moonlicense.licenses.apache2.Apache2
import info.gianlucacosta.moonlicense.licenses.gpl3.Gpl3
import info.gianlucacosta.moonlicense.licenses.lgpl3.Lgpl3
import info.gianlucacosta.moonlicense.noticeformats.HtmlNoticeFormat
import info.gianlucacosta.moonlicense.noticeformats.JavaNoticeFormat
import info.gianlucacosta.moonlicense.noticeformats.PascalNoticeFormat
import info.gianlucacosta.moonlicense.noticeformats.XmlNoticeFormat
import org.gradle.api.Project


public class DslTest extends MoonLicenseTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();

        Project project = getProject()

        project.moonLicense {
            license = apache2

            includes = [
                    /.*\.java$/ : javaFormat,
                    /.*\.html?$/: htmlFormat,
                    /.*\.xml$/  : xmlFormat
            ]


            skipDotItems = true

            productInfo {
                productName = "A simple DSL test"
                inceptionYear = 2014
                copyrightHolder = "A copyright holder"
            }

            fileSet {
                license = gpl3

                productInfo {
                    productName = "SubProject!"
                    inceptionYear = 2011
                    copyrightHolder = "Another copyright holder"
                }

                includes = [
                        "^lambda/": pascalFormat
                ]

                excludes = ["^sigma/", "^tau/"]
            }


            fileSet {
                license = lgpl3

                productInfo {
                    productName = "SubProject 2"
                    inceptionYear = 2012
                    copyrightHolder = "Yet another copyright holder"
                }

                includes = [
                        "^epsilon/": xmlFormat,
                        "^omega/"  : javaFormat
                ]
            }

            skipDotItems = false

            updateLicenseFile = false

            updatePom = false
        }
    }


    void testLicense() {
        assertEquals(
                new Apache2(),
                getSettings().license
        )
    }


    void testProductInfo() {
        assertEquals(
                new ProductInfo(
                        "A simple DSL test",
                        2014,
                        "A copyright holder"
                ),
                getSettings().productInfo
        )
    }


    void testIncludes() {
        assertEquals(
                [
                        /.*\.java$/ : new JavaNoticeFormat(),
                        /.*\.html?$/: new HtmlNoticeFormat(),
                        /.*\.xml$/  : new XmlNoticeFormat()
                ],

                getSettings().includes
        )
    }


    void testDefault_ProjectExcludes() {
        assertEquals(
                ["^build/"],

                getSettings().excludes
        )
    }


    void testFileSets() {
        assertEquals(
                [
                        new LicensedFileSet(
                                new Gpl3(),
                                new ProductInfo(
                                        "SubProject!",
                                        2011,
                                        "Another copyright holder"
                                ),

                                [
                                        "^lambda/": new PascalNoticeFormat()
                                ],

                                ["^sigma/", "^tau/"]
                        ),

                        new LicensedFileSet(
                                new Lgpl3(),
                                new ProductInfo(
                                        "SubProject 2",
                                        2012,
                                        "Yet another copyright holder"
                                ),

                                [
                                        "^epsilon/": new XmlNoticeFormat(),
                                        "^omega/"  : new JavaNoticeFormat()
                                ],

                                []
                        )

                ],

                getSettings().fileSets
        )
    }


    void testSkipDotItems() {
        assertEquals(
                false,
                getSettings().skipDotItems
        )
    }


    void testUpdateLicenseFile() {
        assertEquals(
                false,
                getSettings().updateLicenseFile
        )
    }

    void testUpdatePom() {
        assertEquals(
                false,
                getSettings().updatePom
        )
    }


    void testGetCopyrightYears_forMoonLicenseBlock() {
        assertEquals(
                "2014-2015",

                project.moonLicense.getCopyrightYears(2015)
        )
    }
}
