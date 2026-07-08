package com.assasement.dummyShop

import com.assasement.dummyShop.model.Dimensions
import com.assasement.dummyShop.model.Meta
import com.assasement.dummyShop.model.Product
import com.assasement.dummyShop.model.Review
import com.assasement.dummyShop.utils.toEntity
import com.assasement.dummyShop.utils.toProduct
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProductEntityMapperTest {
    @Test
    fun productToEntity_preservesImportantFields() {
        val product = sampleProduct()

        val entity = product.toEntity()

        assertEquals(product.id, entity.id)
        assertEquals(product.title, entity.title)
        assertEquals(product.category, entity.category)
        assertEquals(product.price, entity.price, 0.0)
        assertEquals(product.rating, entity.rating, 0.0)
        assertEquals(product.images, entity.images)
        assertEquals(product.thumbnail, entity.thumbnail)
    }

    @Test
    fun productEntityRoundTrip_preservesNullableBrandAndNestedFields() {
        val product = sampleProduct(brand = null)

        val roundTrip = product.toEntity().toProduct()

        assertNull(roundTrip.brand)
        assertEquals(product.dimensions, roundTrip.dimensions)
        assertEquals(product.reviews, roundTrip.reviews)
        assertEquals(product.meta, roundTrip.meta)
        assertEquals(product.minimumOrderQuantity, roundTrip.minimumOrderQuantity)
    }

    private fun sampleProduct(brand: String? = "Dummy Brand") = Product(
        id = 7,
        title = "Test Phone",
        description = "A product used by unit tests.",
        category = "smartphones",
        price = 299.99,
        discountPercentage = 12.5,
        rating = 4.6,
        stock = 42,
        tags = listOf("phone", "android"),
        brand = brand,
        sku = "SKU-7",
        weight = 2,
        dimensions = Dimensions(width = 7.0, height = 15.0, depth = 0.8),
        warrantyInformation = "1 year warranty",
        shippingInformation = "Ships in 2 days",
        availabilityStatus = "In Stock",
        reviews = listOf(
            Review(
                rating = 5,
                comment = "Great",
                date = "2026-07-08T00:00:00Z",
                reviewerName = "Tester",
                reviewerEmail = "tester@example.com",
            )
        ),
        returnPolicy = "30 days",
        minimumOrderQuantity = 1,
        meta = Meta(
            createdAt = "2026-07-08T00:00:00Z",
            updatedAt = "2026-07-08T00:00:00Z",
            barcode = "12345",
            qrCode = "https://example.com/qr",
        ),
        images = listOf("https://example.com/image.png"),
        thumbnail = "https://example.com/thumb.png",
    )
}
