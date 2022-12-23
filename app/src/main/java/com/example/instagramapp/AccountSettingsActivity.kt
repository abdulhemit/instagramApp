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
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.instagramapp.databinding.ActivityAccountSettingsBinding
import com.example.instagramapp.model.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class AccountSettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountSettingsBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser : FirebaseUser
    private lateinit var firebaseDatabase : FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private var checker = ""
    private var clicked = ""
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedPicture : Uri? =  null
    var myUri = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        firebaseDatabase = FirebaseDatabase.getInstance()
        storage = Firebase.storage
        registerLauncher()

        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(
                Intent(this@AccountSettingsActivity, SingInActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
            finish()
        }

        binding.saveInfoProfileEdit.setOnClickListener {
            if (checker == "clicked")
            {
                uploadImageAndUpdateInfo()
            }else
            {
                updateUserInfoOnly()
            }
        }

        binding.textviewChacgeImage.setOnClickListener {
            checker = "clicked"
            selectedimage(it)
        }


        getUserInfo()
    }

    // kullanici bilgilerini guncellemek
    private fun updateUserInfoOnly() {
        when {
            TextUtils.isEmpty(binding.editFullName.text.toString()) -> {
                Toast.makeText(this, "Please write fullName first", Toast.LENGTH_LONG).show()
            }
            TextUtils.isEmpty(binding.editUserName.text.toString()) -> {
                Toast.makeText(this, "Please write userName first", Toast.LENGTH_LONG).show()
            }
            TextUtils.isEmpty(binding.editBioProfile.text.toString()) -> {
                Toast.makeText(this, "Please write your bio first", Toast.LENGTH_LONG).show()
            }
            else -> {
                val userRef = firebaseDatabase.reference.child("users")

                val userMap = HashMap<String, Any>()
                userMap["fullName"] = binding.editFullName.text.toString().toLowerCase()
                userMap["userName"] = binding.editUserName.text.toString().toLowerCase()
                userMap["bio"] = binding.editBioProfile.text.toString().toLowerCase()

                userRef.child(firebaseUser.uid).updateChildren(userMap)

                Toast.makeText(this, "Account Information  has been updated successful", Toast.LENGTH_LONG).show()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    // kullanici bilgilerini getirmek
    private fun getUserInfo(){

        val usersRaf = firebaseDatabase.reference.child("users").child(firebaseUser.uid)

        usersRaf.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {


                if (snapshot.exists()){
                    val user = snapshot.getValue<User>(User::class.java)
                    user.let {
                        binding.editUserName.setText(user?.userName)
                        binding.editFullName.setText(user?.fullName)
                        binding.editBioProfile.setText(user?.bio)
                        Picasso.get().load(user?.image).into(binding.imageProfileSettingsActivity)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


    private fun selectedimage(view : View){
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Permission needed ",Snackbar.LENGTH_INDEFINITE).setAction("give permission"){
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
    private fun registerLauncher(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
            if (result.resultCode == RESULT_OK){
                val intentForResult = result.data
                if (intentForResult != null){
                    selectedPicture = intentForResult.data
                    selectedPicture.let {
                        binding.imageProfileSettingsActivity.setImageURI(selectedPicture)
                    }
                }
            }
        }


        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result->
            if (result){
                // permission granted
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)


            }else{
                // permission denied
                Toast.makeText(this@AccountSettingsActivity,"Permission needed!!",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun uploadImageAndUpdateInfo() {

        when {

            TextUtils.isEmpty(selectedPicture.toString()) -> {
                Toast.makeText(this,"Please select image first", Toast.LENGTH_LONG).show()
            }
            TextUtils.isEmpty(binding.editFullName.text.toString()) -> {
                Toast.makeText(this,"Please write fullName first", Toast.LENGTH_LONG).show()
            }
            TextUtils.isEmpty(binding.editUserName.text.toString()) -> {
                Toast.makeText(this,"Please write userName first", Toast.LENGTH_LONG).show()
            }
            TextUtils.isEmpty(binding.editBioProfile.text.toString()) -> {
                Toast.makeText(this,"Please write your bio first", Toast.LENGTH_LONG).show()
            }
            else ->{
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Account Settings")
                progressDialog.setMessage("Please wait, we are updating your profile...")
                progressDialog.show()

                val fileRef  = storage.reference.child("Profile pictures").child(firebaseUser.uid + "jpg")
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

                        val userRef : DatabaseReference = FirebaseDatabase.getInstance().reference.child("users")

                        val userMap = HashMap<String,Any>()
                        userMap["fullName"] = binding.editFullName.text.toString().toLowerCase()
                        userMap["userName"] = binding.editUserName.text.toString().toLowerCase()
                        userMap["bio"] = binding.editBioProfile.text.toString().toLowerCase()
                        userMap["image"] = myUri

                        userRef.child(firebaseUser.uid).updateChildren(userMap)

                        Toast.makeText(this,"Account Information  has been updated successful",Toast.LENGTH_LONG).show()

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
}