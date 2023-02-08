package com.daharansemar.adminresto.repository

import android.graphics.Bitmap
import com.daharansemar.adminresto.dataClass.ImageData

interface BannerInterface {

    fun editImageData(imageData: ImageData)
    fun deleteImageData(id:String)

}