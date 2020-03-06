package com.xando.chefsclub.helper

import android.widget.EditText
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import com.xando.chefsclub.R

fun EditText.setErrorColoredIcon(error: String?, colorRes: Int){
    val icon = AppCompatResources.getDrawable(context, R.drawable.ic_error_white_24dp)!!
    DrawableCompat.setTint(icon, getColorFrom(colorRes))
    icon.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)
    setError(error, icon)
}
