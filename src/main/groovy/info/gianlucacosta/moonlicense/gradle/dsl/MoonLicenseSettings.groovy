package info.gianlucacosta.moonlicense.gradle.dsl

import info.gianlucacosta.moonlicense.LicensedFileSet

/**
 * Plugin settings that are exposed by the DSL.
 *
 * It inherits properties from LicensedFileSetBlock, which will be taken into account
 * only after all the items in the <i>fileSets</i> collection.
 */
class MoonLicenseSettings extends LicensedFileSetBlock {
    boolean skipDotItems = true

    boolean updateLicenseFile = true

    boolean updatePom = true

    List<LicensedFileSet> fileSets = [];


    MoonLicenseSettings() {
        excludes = [
                "^build/"
        ]
    }

    def fileSet(Closure closure) {
        LicensedFileSetDto licensedFileSetDto = new LicensedFileSetDto()

        closure.delegate = licensedFileSetDto
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure()

        LicensedFileSet licensedFileSet = licensedFileSetDto.getLicensedFileSet()

        fileSets.add(licensedFileSet);
    }
}
