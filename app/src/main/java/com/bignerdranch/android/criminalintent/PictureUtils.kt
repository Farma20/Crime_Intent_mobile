package com.bignerdranch.android.criminalintent

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import kotlin.math.roundToInt

//функция узнавания размера экрана и подгон изображения под этот размер
fun getScaledBitmap(path: String, activity: Activity): Bitmap{
    val size = Point()
    activity.windowManager.defaultDisplay.getSize(size)
    return getScaleBitmap(path, size.x, size.y)
}

//функция сжатия размера исходного изображения
fun getScaleBitmap(path: String, destWidth: Int, destHeight: Int): Bitmap{
    //чтение размеров изображения на диске
    var options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(path, options)

    val srcWidth = options.outWidth.toFloat()
    val srcHeight = options.outHeight.toFloat()

    //выясняем на сколько нужно уменьшить
    var inSampleSize = 1
    if(srcWidth > destWidth || srcHeight > destHeight){
        val widthScale = srcWidth/destWidth
        val heightScale = srcHeight/destHeight

        val sampleScale = if(widthScale > heightScale){
            widthScale
        }else{
            heightScale
        }

        inSampleSize = sampleScale.roundToInt()
    }

    options = BitmapFactory.Options()
    options.inSampleSize = inSampleSize

    //Чтение и создание окончательного растрового изображения
    return BitmapFactory.decodeFile(path, options)
}