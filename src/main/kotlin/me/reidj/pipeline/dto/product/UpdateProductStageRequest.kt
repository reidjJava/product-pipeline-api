package me.reidj.pipeline.dto.product

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class UpdateProductStageRequest(
    val productId: Long,
    val stage: String,
)
