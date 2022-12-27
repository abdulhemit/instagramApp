package com.example.instagramapp

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.instagramapp.databinding.ActivityAccountSettingsBinding
import com.example.instagramapp.databinding.ActivityAddPostBinding
import com.example.instagramapp.fragments.HomeFragment
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage

class AddPostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPostBinding
    private lateinit var storage: FirebaseStorage
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedPicture : Uri? =  null
    var myUri = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val view = binding.root
        storage = Firebase.storage
        registerLauncher()


        binding.saveNewPostAddPostActivity.setOnClickListener {
            if (selectedPicture != null){
                uploadImage()
            }else{

                Toast.makeText(this,"Please select a image",Toast.LENGTH_LONG).show()

            }

        }
        binding.newImageAddPostActivity.setOnClickListener {
            // permission granted
            val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            // start activity for result
            activityResultLauncher.launch(intentToGallery)
        }
        binding.closeImageBtnAddPostActivity.setOnClickListener {

            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()

        }



        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Permission needed ", Snackbar.LENGTH_INDEFINITE).setAction("give permission"){
                    // request permission
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }.show()
            }else{
                // request permission
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }else{
            // permission granted
            val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            // start activity for result
            activityResultLauncher.launch(intentToGallery)
        }
    }

    private fun uploadImage() {
        when{
            TextUtils.isEmpty(selectedPicture.toString()) -> {
                Toast.makeText(this,"Please select image first", Toast.LENGTH_LONG).show()
            }
            else ->{
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle(" Adding New Post")
                progressDialog.setMessage("Please wait, we are adding your picture...")
                progressDialog.show()


                val fileRef  = storage.reference.child("post pictures").child(System.currentTimeMillis().toString() + "jpg")
                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(selectedPicture!!)

                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->

                    if (! task.isSuccessful)
                    {
                        task.exception?.let {
                            throw it
                            progressDialog.dismiss()
                        }
                    }


                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener ( OnCompleteListener<Uri>{ task ->
                    if (task.isSuccessful){
                        val downloadUri = task.result
                        myUri = downloadUri.toString()

                        val postRef : DatabaseReference = FirebaseDatabase.getInstance().reference.child("Post")
                        val postId = postRef.push().key

                        val postMap = HashMap<String,Any>()
                        postMap["postId"] = postId!!
                        postMap["discription"] = binding.discriptionPost.text.toString().toLowerCase()
                        postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                        postMap["postImage"] = myUri

                        postRef.child(postId).updateChildren(postMap)

                        Toast.makeText(this,"Post updated successful",Toast.LENGTH_LONG).show()

                        val intent = Intent(this,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        progressDialog.dismiss()
                    }else{
                        progressDialog.dismiss()
                    }
                })

            }

        }

    }

    private fun registerLauncher(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
            if (result.resultCode == RESULT_OK){
                val intentForResult = result.data
                if (intentForResult != null){
                    selectedPicture = intentForResult.data
                    selectedPicture.let {
                        binding.newImageAddPostActivity.setImageURI(selectedPicture)
                    }
                }
            }
        }


        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result->
            if (result){
                // permission granted
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)


            }else{
                // permission denied
                Toast.makeText(this@AddPostActivity,"Permission needed!!", Toast.LENGTH_LONG).show()
            }
        }
    }

}