package com.example.sharingang

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description
import org.junit.Test
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

// Testing gallery code found here: https://proandroiddev.com/testing-camera-and-galley-intents-with-espresso-218eb9f59da9?gi=b50f44bef82a
fun savePickedImage(activity: Activity) {
    val d = activity.resources.getDrawable(R.drawable.ic_baseline_image_24, activity.theme)
    val dir = activity.externalCacheDir
    val file = File(dir?.path, "pickImageResult.jpg")
    val outStream: FileOutputStream?
    try {
        outStream = FileOutputStream(file)
        val bm = d.toBitmap(40, 40)
        bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
        with(outStream) {
            flush()
            close()
        }
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun createImageGallerySetResultStub(activity: Activity): Instrumentation.ActivityResult {
    val bundle = Bundle()
    val parcels = ArrayList<Parcelable>()
    val resultData = Intent()
    val dir = activity.externalCacheDir
    val file = File(dir?.path, "pickImageResult.jpg")
    val uri = Uri.fromFile(file)
    val parcelable1 = uri as Parcelable
    parcels.add(parcelable1)
    bundle.putParcelableArrayList(Intent.EXTRA_STREAM, parcels)
    resultData.putExtras(bundle)
    return Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
}
