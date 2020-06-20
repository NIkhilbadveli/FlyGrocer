package com.titos.flygrocer

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {
    var imageFilePath: String? = null
    val REQUEST_CAPTURE_IMAGE = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layoutView = inflater.inflate(R.layout.fragment_home, container, false)

        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.banner1))
        imageList.add(SlideModel(R.drawable.banner2))
        imageList.add(SlideModel(R.drawable.banner3))
        imageList.add(SlideModel(R.drawable.banner4))

        val imageSlider = layoutView.findViewById<ImageSlider>(R.id.bannerSlider)
        imageSlider.setImageList(imageList)

        layoutView.findViewById<CardView>(R.id.superGrocerContainer).setOnClickListener {

            findNavController().navigate(R.id.action_homeFragment_to_shopFragment)
        }

        layoutView.findViewById<CardView>(R.id.foodGrocerContainer).setOnClickListener {

            findNavController().navigate(R.id.action_homeFragment_to_shopFragment)
        }

        layoutView.findViewById<CardView>(R.id.greenGrocerContainer).setOnClickListener {

            findNavController().navigate(R.id.action_homeFragment_to_shopFragment)
        }

        layoutView.findViewById<Button>(R.id.uploadButton).setOnClickListener {
            openCameraIntent()
        }

        return layoutView
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val timeStamp: String = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir: File = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        val image: File = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
        imageFilePath = image.absolutePath
        return image
    }

    private fun openCameraIntent() {
        val pictureIntent = Intent(
            MediaStore.ACTION_IMAGE_CAPTURE)
        if(pictureIntent.resolveActivity(activity?.packageManager!!) != null){
            //Create a file to store the image
            var photoFile:File? = null;
            try {
                photoFile = createImageFile();
            } catch (ex:IOException) {
            }

            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(requireContext(),"${activity?.packageName!!}.provider",photoFile)
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(pictureIntent,
                    REQUEST_CAPTURE_IMAGE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CAPTURE_IMAGE && resultCode==Activity.RESULT_OK) {
            val storageRef = FirebaseStorage.getInstance().reference
            val file = Uri.fromFile(File(imageFilePath!!))

            val user = FirebaseAuth.getInstance().currentUser!!
            val userRef = storageRef.child("${user.uid}/${file.lastPathSegment}")
            val uploadTask = userRef.putFile(file)

            uploadTask.addOnFailureListener {
                Toast.makeText(requireContext(), "Your list upload is failed", Toast.LENGTH_SHORT).show()
            }.addOnSuccessListener {
                Toast.makeText(requireContext(), "Your list is uploaded", Toast.LENGTH_SHORT).show()
            }
        }
    }
}