package livroandroid.com.recyclerviewexample.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File

object ImageUtils {

    fun resize(file: File, reqWidth: Int, reqHeight:Int) :Bitmap{

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(file.absolutePath, options)

        //Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(file.absolutePath, options)

    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {

        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth){
            val halfHeight = height /2
            val halWidth = width /2

            while (halfHeight / inSampleSize >= reqHeight && halWidth / inSampleSize >= reqWidth){
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}