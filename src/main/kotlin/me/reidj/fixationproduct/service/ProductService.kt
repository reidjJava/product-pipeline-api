package me.reidj.fixationproduct.service

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.transaction.Transactional
import me.reidj.fixationproduct.dto.product.*
import me.reidj.fixationproduct.mapper.ProductMapper
import me.reidj.fixationproduct.repository.ProductRepository
import org.springframework.data.crossstore.ChangeSetPersister.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
@Transactional
class ProductService(
    private val productRepository: ProductRepository,
    private val productMapper: ProductMapper,
    private val qrCodeService: QrCodeService,
    private val objectMapper: ObjectMapper,
) {
    fun create(createProductRequest: CreateProductRequest): ResponseEntity<CreateProductResponse> {
        val newProduct = productMapper.toProduct(createProductRequest)
        productRepository.save(newProduct)
        return ResponseEntity(
            CreateProductResponse(newProduct.id!!),
            HttpStatus.CREATED,
        )
    }

    fun findProduct(productId: Long): ResponseEntity<FindProductResponse> {
        val product = productRepository.findProductById(productId) ?: throw NotFoundException()
        val findProductResponse = productMapper.toFindProductResponse(product)
        return ResponseEntity(findProductResponse, HttpStatus.OK)
    }

    fun updateProductStage(
        productId: Long,
        stage: String,
    ) {
        val product = productRepository.findProductById(productId) ?: throw NotFoundException()
        product.stage.add(stage)
        productRepository.save(product)
    }

    fun generateQRCode(productId: Long): ResponseEntity<GenerateQrCodeResponse> {
        productRepository.findProductById(productId) ?: throw NotFoundException()
        val qrCode = qrCodeService.generateQRCodeBase64("http://localhost:8080/api/v1/product/find?product_id=$productId")

        productRepository.setQrCode(productId, objectMapper.writeValueAsString(qrCode))

        return ResponseEntity(
            GenerateQrCodeResponse(qrCode),
            HttpStatus.OK,
        )
    }
}
