package me.reidj.pipeline.dto.product

data class CreateProductRequest(
    val title: String,
    val stage: List<String>,
)
