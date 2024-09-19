package me.reidj.pipeline.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("ports.qr-code")
data class QrCodeProperties(
    val size: Int,
    val urlPrefix: String
)