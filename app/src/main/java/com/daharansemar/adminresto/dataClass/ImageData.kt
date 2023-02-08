package com.daharansemar.adminresto.dataClass

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class ImageData (
    var id:String?=null,
    var imageUrl:String = "",
    var time:Timestamp?=null,


        )

