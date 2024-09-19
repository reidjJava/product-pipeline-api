package me.reidj.pipeline.controller

import me.reidj.pipeline.dto.product.*
import me.reidj.pipeline.service.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1/product")
class ProductController(
    private val productService: ProductService,
) {
    @PostMapping("/create")
    fun createProduct(
        @RequestBody createProductRequest: CreateProductRequest,
    ): ResponseEntity<CreateProductResponse> {
        val product = productService.create(createProductRequest)
        return ResponseEntity.ok(product)
    }

    @GetMapping("/find")
    fun findProduct(
        @RequestParam(name = "product_id") productId: Long,
    ): ResponseEntity<FindProductResponse> {
        val product = productService.findProduct(productId)
        return ResponseEntity.ok(product)
    }

    @PostMapping("/stage")
    fun updateProductStage(
        @RequestBody updateProductStageRequest: UpdateProductStageRequest,
    ) {
        productService.updateProductStage(
            productId = updateProductStageRequest.productId,
            stage = updateProductStageRequest.stage
        )
    }

    @PostMapping("/qr_code")
    fun generateQRCode(
        @RequestBody generateQRCodeRequest: GenerateQrCodeRequest,
    ): ResponseEntity<GenerateQrCodeResponse> {
        val qrCode = productService.generateQRCode(generateQRCodeRequest.productId)
        return ResponseEntity.ok(qrCode)
    }
}
