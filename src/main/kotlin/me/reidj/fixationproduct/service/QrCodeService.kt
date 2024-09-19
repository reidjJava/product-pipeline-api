package me.reidj.fixationproduct.service

import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import me.reidj.fixationproduct.exception.WebAppException
import me.reidj.fixationproduct.service.contract.QrCodeGenerator
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import javax.imageio.ImageIO

@Service
class QrCodeService : QrCodeGenerator {
    companion object {
        private val WHITE = Color.WHITE.rgb
        private val BLACK = Color.BLACK.rgb
        private val log = LoggerFactory.getLogger(QrCodeService::class.java)
    }

    override fun generateQRCodeBase64(
        text: String,
        width: Int,
        height: Int,
    ): String {
        log.info("Starting QR code generation for text: \"$text\" with dimensions: ${width}x$height")
        val qrCodeWriter = QRCodeWriter()
        return try {
            val bitMatrix: BitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height)
            val image = createImage(bitMatrix)
            ByteArrayOutputStream().use { byteArrayOutputStream ->
                ImageIO.write(image, "PNG", byteArrayOutputStream)
                val qrCodeBytes = byteArrayOutputStream.toByteArray()
                val base64QrCode = Base64.getEncoder().encodeToString(qrCodeBytes)
                log.info("QR code generation completed successfully for text: \"$text\"")
                base64QrCode
            }
        } catch (e: WriterException) {
            throw WebAppException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate QR code $e")
        } catch (e: IOException) {
            throw WebAppException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save QR code $e")
        }
    }

    private fun createImage(bitMatrix: BitMatrix): BufferedImage {
        val width = bitMatrix.width
        val height = bitMatrix.height
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

        for (y in 0 until height) {
            for (x in 0 until width) {
                image.setRGB(x, y, if (bitMatrix[x, y]) BLACK else WHITE)
            }
        }

        return image
    }
}
