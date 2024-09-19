package me.reidj.pipeline.repository

import me.reidj.pipeline.entity.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProductRepository : JpaRepository<Product, Long> {

    fun findProductById(id: Long): Product?

    @Modifying
    @Query("UPDATE Product p SET p.qrcode = :qrCode WHERE p.id = :productId")
    fun setQrCode(
        @Param("productId") productId: Long,
        @Param("qrCode") qrCode: String,
    )
}
