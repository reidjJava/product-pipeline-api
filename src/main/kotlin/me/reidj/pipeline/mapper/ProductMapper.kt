package me.reidj.pipeline.mapper

import me.reidj.pipeline.dto.product.CreateProductRequest
import me.reidj.pipeline.dto.product.FindProductResponse
import me.reidj.pipeline.entity.Product
import org.mapstruct.BeanMapping
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring")
interface ProductMapper {
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    fun toProduct(createProductRequest: CreateProductRequest): Product

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    fun toFindProductResponse(product: Product): FindProductResponse
}
