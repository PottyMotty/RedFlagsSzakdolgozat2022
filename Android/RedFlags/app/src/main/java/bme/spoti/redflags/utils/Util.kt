package bme.spoti.redflags.utils

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder


fun generateQR(content: String?, size: Int): Bitmap? {
	val bitmap: Bitmap?
	val barcodeEncoder = BarcodeEncoder()
	bitmap = barcodeEncoder.encodeBitmap(
		content,
		BarcodeFormat.QR_CODE, size, size
	)
	return bitmap
}

fun Bitmap.changeColor(
	oldColor: Long,
	newColor: Long
): Bitmap? {
	val width = this.width
	val height = this.height
	val pixels = IntArray(width * height)
	this.getPixels(pixels, 0, width, 0, 0, width, height)
	for (x in pixels.indices) {
		pixels[x] = if (pixels[x] == oldColor.toInt()) newColor.toInt() else pixels[x]
	}
	val newBitmap = Bitmap.createBitmap(
		width, height,
		this.config
	)
	newBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
	return newBitmap
}