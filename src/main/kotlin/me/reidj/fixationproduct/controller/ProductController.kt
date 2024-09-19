package me.reidj.fixationproduct.controller

import me.reidj.fixationproduct.dto.product.*
import me.reidj.fixationproduct.service.ProductService
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
        return productService.create(createProductRequest)
    }

    @GetMapping("/find")
    fun findProduct(
        @RequestParam(name = "product_id") productId: Long,
    ): ResponseEntity<FindProductResponse> {
        return productService.findProduct(productId)
    }

    @PostMapping("/stage")
    fun updateProductStage(
        @RequestBody updateProductStageRequest: UpdateProductStageRequest,
    ) {
        return productService.updateProductStage(updateProductStageRequest.productId, updateProductStageRequest.stage)
    }

    @PostMapping("/qr_code")
    fun generateQRCode(
        @RequestBody generateQRCodeRequest: GenerateQrCodeRequest,
    ): ResponseEntity<GenerateQrCodeResponse> {
        return productService.generateQRCode(generateQRCodeRequest.productId)
    }
}
