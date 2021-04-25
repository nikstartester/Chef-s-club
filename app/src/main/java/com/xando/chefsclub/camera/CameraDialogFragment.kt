package com.xando.chefsclub.camera

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.controls.Flash
import com.xando.chefsclub.R
import com.xando.chefsclub.constants.Constants
import kotlinx.android.synthetic.main.fragment_camera.*
import java.io.File
import java.io.IOException
import java.util.*

const val CAMERA_DIALOG_PHOTO_URI = "CAMERA_DIALOG_PHOTO_URI"

class CameraDialogFragment : AppCompatDialogFragment() {

    private var flashToast: Toast? = null

    companion object {
        fun newInstance(): DialogFragment = CameraDialogFragment()

        private const val IS_CAMERA_PERMISSION_ALREADY_REQUESTED = "IS_CAMERA_PERMISSION_ALREADY_REQUESTED"
        private const val CAMERA_CODE_REQUEST_PERMISSION = 213123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogFragment)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_camera, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() {
        take_capture.setOnClickListener {
            take_capture.isEnabled = false
            camera.takePicture()
        }
        camera_dialog_back.setOnClickListener { dismiss() }
        camera_dialog_flash.setOnClickListener {
            camera.flash = when (camera.flash) {
                Flash.OFF -> {
                    camera_dialog_flash.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_flash_on_white_24dp,
                            null
                        )
                    )
                    flashToast?.cancel()
                    val topOffset = camera_dialog_toolbar_container.measuredHeight +
                            resources.getDimensionPixelSize(R.dimen.camera_dialog_toast_top_padding)
                    flashToast =
                        Toast.makeText(requireContext().applicationContext, "Flash", Toast.LENGTH_SHORT).apply {
                            setGravity(Gravity.TOP, 0, topOffset)
                        }
                    flashToast!!.show()
                    Flash.ON
                }
                else -> {
                    camera_dialog_flash.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_flash_off_white_24dp,
                            null
                        )
                    )
                    flashToast?.cancel()
                    Flash.OFF
                }
            }
        }
        camera.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                try {
                    val savedPhoto = createPhotoFile().createIfNotExist()
                    result.toFile(savedPhoto) {
                        sendResult(Activity.RESULT_OK, Uri.fromFile(savedPhoto))
                        dismiss()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    take_capture.isEnabled = true
                }
            }
        })

        camera_dialog_stub_to_settings.setOnClickListener { toAppSettings() }
    }

    override fun onResume() {
        startCameraOrRequestPermission()
        super.onResume()
    }

    override fun onPause() {
        camera.close()
        flashToast?.cancel()
        super.onPause()
    }

    override fun onDestroyView() {
        camera.destroy()
        flashToast?.cancel()
        flashToast = null
        super.onDestroyView()
    }

    private fun createPhotoFile(): File {
        val unique = UUID.randomUUID().toString()
        return createPhotoFile(unique)
    }

    private fun sendResult(resultCode: Int, uri: Uri) {
        targetFragment!!.onActivityResult(
            targetRequestCode,
            resultCode,
            Intent().apply { putExtra(CAMERA_DIALOG_PHOTO_URI, uri) }
        )
    }

    private fun toAppSettings() {
        startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:${requireContext().packageName}")
            )
        )
    }

    //region Camera Permission
    private fun startCameraOrRequestPermission() {
        context?.let { context ->
            val rc = ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            if (rc == PackageManager.PERMISSION_GRANTED) {
                camera_dialog_stub_container.isVisible = false
                camera.open()
            } else {
                camera_dialog_stub_container.isVisible = true
                if (isCameraPermissionAlreadyRequested().not()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) requestCameraPermission()
                    setCameraPermissionAlreadyRequested()
                }
            }
        }
    }

    private fun isCameraPermissionAlreadyRequested(): Boolean =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(IS_CAMERA_PERMISSION_ALREADY_REQUESTED, false)

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestCameraPermission() {
        activity?.requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_CODE_REQUEST_PERMISSION)
    }

    private fun setCameraPermissionAlreadyRequested() {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
            .putBoolean(IS_CAMERA_PERMISSION_ALREADY_REQUESTED, true)
            .apply()
    }
    //endregion

    private fun createPhotoFile(unique: String): File {
        return File(getParentDirectoryPath(), getPhotoName(unique))
    }

    private fun getParentDirectoryPath(): String? {
        return Constants.Files.getDirectoryForCaptures(activity)
    }

    private fun getPhotoName(unique: String): String {
        return "IMG_$unique.jpg"
    }

    @Throws(IOException::class)
    private fun File.createIfNotExist() = apply {
        if (parentFile.exists().not()) parentFile.mkdirs()
        if (exists().not()) createNewFile()
    }
}