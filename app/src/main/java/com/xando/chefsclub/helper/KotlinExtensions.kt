package com.xando.chefsclub.helper

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import com.xando.chefsclub.App

fun Fragment.getApp() = activity!!.application as App

inline fun <reified T: ViewModel> Fragment.getViewModel() =
        ViewModelProviders.of(this).get(T::class.java)

inline fun <reified T: ViewModel> Fragment.getHostViewModel() =
        ViewModelProviders.of(activity!!).get(T::class.java)
