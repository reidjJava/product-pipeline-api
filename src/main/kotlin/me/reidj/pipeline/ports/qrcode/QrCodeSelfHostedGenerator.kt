package me.reidj.pipeline.ports.qrcode

import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import me.reidj.pipeline.exception.WebAppException
import me.reidj.pipeline.properties.QrCodeProperties
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.stereotype.Service
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import javax.imageio.ImageIO

@Service
class QrCodeSelfHostedGenerator(
    private val qrCodeProperties: QrCodeProperties
) : QrCodeGenerator {

    override fun createQrCode(productId: Long): String {
        log.info("Starting QR code generation with product id: `$productId`")

        val qrCodeWriter = QRCodeWriter()

        try {
            val bitMatrix: BitMatrix = qrCodeWriter.encode(
                qrCodeProperties.urlPrefix + productId,
                BarcodeFormat.QR_CODE,
                qrCodeProperties.size,
                qrCodeProperties.size
            )
            val image = createImage(bitMatrix)

            return ByteArrayOutputStream().use { outputStream ->
                ImageIO.write(image, FORMAT_NAME, outputStream)

                val qrCodeBytes = outputStream.toByteArray()
                val base64QrCode = Base64.getEncoder().encodeToString(qrCodeBytes)

                log.info("QR code generation completed successfully with product id: `$productId`")

                base64QrCode
            }
        } catch (e: WriterException) {
            throw WebAppException(INTERNAL_SERVER_ERROR, "Failed to generate QR code $e")
        } catch (e: IOException) {
            throw WebAppException(INTERNAL_SERVER_ERROR, "Failed to save QR code $e")
        }
    }

    private fun createImage(bitMatrix: BitMatrix): BufferedImage {
        val width = bitMatrix.width
        val height = bitMatrix.height
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

        for (y in 0 until height) {
            for (x in 0 until width) {
                image.setRGB(x, y, if (bitMatrix[x, y]) Color.BLACK.rgb else Color.WHITE.rgb)
            }
        }

        return image
    }

    companion object {
        private const val FORMAT_NAME = "PNG"
        private val log = LoggerFactory.getLogger(QrCodeSelfHostedGenerator::class.java)
    }
}
