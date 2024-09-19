package me.reidj.fixationproduct.dto.product

data class CreateProductRequest(
    val title: String,
    val stage: List<String>,
)
