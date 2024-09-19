package me.reidj.pipeline.ports.qrcode

@FunctionalInterface
fun interface QrCodeGenerator {

    fun createQrCode(productId: Long): String

}
