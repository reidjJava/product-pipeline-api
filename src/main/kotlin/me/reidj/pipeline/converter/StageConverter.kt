package me.reidj.pipeline.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class StageConverter : AttributeConverter<List<String>, String> {
    override fun convertToDatabaseColumn(attribute: List<String>?): String {
        return attribute?.joinToString(", ") ?: ""
    }

    override fun convertToEntityAttribute(dbData: String?): List<String> {
        return dbData?.split(", ")?.map { it.trim() } ?: emptyList()
    }
}
