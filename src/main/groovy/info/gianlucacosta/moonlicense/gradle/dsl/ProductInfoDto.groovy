package info.gianlucacosta.moonlicense.gradle.dsl

import info.gianlucacosta.moonlicense.ProductInfo

/**
 * Exposes ProductInfo fields to the DSL.
 */
class ProductInfoDto {
    String productName
    int inceptionYear
    String copyrightHolder

    ProductInfo getProductInfo() {
        return new ProductInfo(productName, inceptionYear, copyrightHolder);
    }
}
