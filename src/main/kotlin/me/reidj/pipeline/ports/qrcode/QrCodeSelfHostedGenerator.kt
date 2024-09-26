package me.reidj.pipeline.ports.qrcode

import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import me.reidj.pipeline.exception.WebAppException
import me.reidj.pipeline.properties.QrCodeProperties
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.stereotype.Service
import java.awt.Color
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
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

            val qrImage = createQrImage(bitMatrix)
            val finalQrImage = addLogoToQrImage(qrImage)

            return convertToBase64(finalQrImage)
        } catch (e: WriterException) {
            log.error("QR code generation failed", e)
            throw WebAppException(INTERNAL_SERVER_ERROR, "Failed to generate QR code: ${e.message}")
        } catch (e: IOException) {
            log.error("Failed to process image", e)
            throw WebAppException(INTERNAL_SERVER_ERROR, "Failed to process QR code image: ${e.message}")
        }
    }

    private fun createQrImage(bitMatrix: BitMatrix): BufferedImage {
        val width = bitMatrix.width
        val height = bitMatrix.height
        val qrImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

        for (y in 0 until height) {
            for (x in 0 until width) {
                qrImage.setRGB(x, y, if (bitMatrix[x, y]) Color.BLACK.rgb else Color.WHITE.rgb)
            }
        }

        return qrImage
    }

    private fun addLogoToQrImage(qrImage: BufferedImage): BufferedImage {
        val logoResource = ClassPathResource("images/logo.png")

        if (!logoResource.exists()) {
            log.error("Logo resource not found")
            throw WebAppException(INTERNAL_SERVER_ERROR, "Logo file not found.")
        }

        val logo = ImageIO.read(logoResource.inputStream)
        val logoSize = qrImage.width / 8
        val g = qrImage.createGraphics()

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)

        val x = (qrImage.width - logoSize) / 2
        val y = (qrImage.height - logoSize) / 2

        drawWhiteBorderAroundLogo(g, x, y, logoSize)

        drawLogo(g, logo, x, y, logoSize)

        g.dispose()
        return qrImage
    }

    private fun drawWhiteBorderAroundLogo(g: Graphics2D, x: Int, y: Int, logoSize: Int) {
        val borderSize = logoSize / 2
        g.color = Color.WHITE
        g.fillRect(x - borderSize / 2, y - borderSize / 2, logoSize + borderSize, logoSize + borderSize)
    }

    private fun drawLogo(g: Graphics2D, logo: BufferedImage, x: Int, y: Int, logoSize: Int) {
        val at = AffineTransform()
        at.translate(x.toDouble(), y.toDouble())
        at.scale(logoSize.toDouble() / logo.width, logoSize.toDouble() / logo.height)
        g.drawRenderedImage(logo, at)
    }

    private fun convertToBase64(image: BufferedImage): String {
        return ByteArrayOutputStream().use { outputStream ->
            ImageIO.write(image, FORMAT_NAME, outputStream)
            val qrCodeBytes = outputStream.toByteArray()
            Base64.getEncoder().encodeToString(qrCodeBytes)
        }
    }

    companion object {
        private const val FORMAT_NAME = "PNG"
        private val log = LoggerFactory.getLogger(QrCodeSelfHostedGenerator::class.java)
    }
}
