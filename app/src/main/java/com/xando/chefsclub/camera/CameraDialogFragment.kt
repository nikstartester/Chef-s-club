package com.xando.chefsclub.camera

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
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
                    camera_dialog_flash.setImageDrawable(resources.getDrawable(R.drawable.ic_flash_on_white_24dp))
                    flashToast = Toast.makeText(context!!.applicationContext, "Flash", Toast.LENGTH_SHORT).apply {
                        setGravity(Gravity.TOP, 0, camera_dialog_toolbar_container.measuredHeight)
                    }
                    flashToast!!.show()
                    Flash.ON
                }
                else -> {
                    camera_dialog_flash.setImageDrawable(resources.getDrawable(R.drawable.ic_flash_off_white_24dp))
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
    }

    override fun onResume() {
        camera.open()
        super.onResume()
    }

    override fun onPause() {
        camera.close()
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

    private fun sendResult(resultCode: Int, uri: Uri) {
        targetFragment!!.onActivityResult(targetRequestCode,
                resultCode,
                Intent().apply { putExtra(CAMERA_DIALOG_PHOTO_URI, uri) }
        )
    }
}