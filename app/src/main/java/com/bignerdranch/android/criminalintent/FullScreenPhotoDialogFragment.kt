package com.bignerdranch.android.criminalintent

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import java.io.File
import java.nio.file.attribute.AclEntry.Builder

private const val DATA_TAG = "data"

class FullScreenPhotoDialogFragment: DialogFragment() {

    lateinit var fullImageView: ImageView
    lateinit var photoFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        photoFile = arguments?.getSerializable(DATA_TAG) as File

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dialog_fullscreen_photo, container, false)

        fullImageView = view.findViewById(R.id.fullscreen_photo) as ImageView

        return view
    }

    override fun onStart() {
        super.onStart()

        updatePhotoView()
    }

    private fun updatePhotoView(){
        if(photoFile.exists()){
            val bitmap = getScaledBitmap(photoFile.path, requireActivity())
            fullImageView.setImageBitmap(bitmap)
        } else{
            fullImageView.setImageBitmap(null)
        }
    }


    companion object{
        fun newInstance(photoFile: File): FullScreenPhotoDialogFragment{
            val args = Bundle().apply {
                putSerializable(DATA_TAG, photoFile)
            }

            return FullScreenPhotoDialogFragment().apply {
                arguments = args
            }
        }
    }
}