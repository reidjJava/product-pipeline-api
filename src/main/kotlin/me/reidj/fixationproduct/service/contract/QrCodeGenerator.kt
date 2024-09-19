package me.reidj.fixationproduct.service.contract

interface QrCodeGenerator {
    fun generateQRCodeBase64(
        text: String,
        width: Int = 300,
        height: Int = 300,
    ): String
}
