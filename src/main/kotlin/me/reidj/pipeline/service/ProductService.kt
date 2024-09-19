package me.reidj.pipeline.service

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.transaction.Transactional
import me.reidj.pipeline.dto.product.*
import me.reidj.pipeline.mapper.ProductMapper
import me.reidj.pipeline.ports.qrcode.QrCodeSelfHostedGenerator
import me.reidj.pipeline.repository.ProductRepository
import org.springframework.data.crossstore.ChangeSetPersister.*
import org.springframework.stereotype.Service

@Service
@Transactional
class ProductService(
    private val productRepository: ProductRepository,
    private val productMapper: ProductMapper,
    private val qrCodeSelfHostedGenerator: QrCodeSelfHostedGenerator,
    private val objectMapper: ObjectMapper,
) {
    fun create(createProductRequest: CreateProductRequest): CreateProductResponse {
        val product = productMapper.toProduct(createProductRequest)
        productRepository.save(product)
        return CreateProductResponse(product.id!!)
    }

    fun findProduct(productId: Long): FindProductResponse {
        val product = productRepository.findProductById(productId) ?: throw NotFoundException()
        return productMapper.toFindProductResponse(product)
    }

    fun updateProductStage(
        productId: Long,
        stage: String,
    ) {
        val product = productRepository.findProductById(productId) ?: throw NotFoundException()
        product.stage.add(stage)
        productRepository.save(product)
    }

    fun generateQRCode(productId: Long): GenerateQrCodeResponse {
        productRepository.findProductById(productId) ?: throw NotFoundException()

        val qrCode = qrCodeSelfHostedGenerator.createQrCode(
            productId = productId,
        )

        productRepository.setQrCode(productId, objectMapper.writeValueAsString(qrCode))

        return GenerateQrCodeResponse(qrCode)
    }
}
