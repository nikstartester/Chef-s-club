package com.xando.chefsclub.helper

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.xando.chefsclub.App

fun Fragment.getApp() = activity!!.application as App

inline fun <reified T : ViewModel> Fragment.getViewModel() =
        ViewModelProviders.of(this).get(T::class.java)

inline fun <reified T : ViewModel> Fragment.getHostViewModel() =
        ViewModelProviders.of(activity!!).get(T::class.java)

fun View.getColorFrom(colorRes: Int) = context.resources.getColor(colorRes)