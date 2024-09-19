package me.reidj.pipeline.entity

import jakarta.persistence.*
import me.reidj.pipeline.converter.StageConverter
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Table(name = "products")
@Entity
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "title")
    var title: String = "",
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "qrcode", columnDefinition = "jsonb")
    var qrcode: String? = null,
    @Convert(converter = StageConverter::class)
    @Column(name = "stage")
    val stage: MutableList<String> = mutableListOf(),
)
