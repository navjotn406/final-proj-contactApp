package com.example.contactapp

import DatabaseHandler
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import java.io.ByteArrayOutputStream

class AddOrEditActivity : AppCompatActivity() {

    //Creating Variables
    var dbHandler: DatabaseHandler? = null
    var isEditMode = false
    var btn_delete: Button? = null
    var btn_save: Button? = null
    var first_name: EditText? = null
    var last_name: EditText? = null
    var contact_number: EditText? = null
    var email: EditText? = null
    var address: EditText? = null
    var image: ImageView? = null
    var add_img: CardView? = null
    private val pickImage = 100
    private var imageUri: Uri? = null
    var photo:Bitmap?=null

    val PREFERRED_IMAGE_SIZE = 1000
    val ONE_MB_TO_KB = 1024

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_or_edit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Initializing every things
        initViews()
        initDB()
        initOperations()
    }

    //Initializing the buttons, CardView and EditTexts
    private fun initViews(){
        btn_delete = findViewById(R.id.btn_delete)
        btn_save = findViewById(R.id.btn_save)
        first_name = findViewById(R.id.first_name)
        last_name = findViewById(R.id.last_name)
        contact_number = findViewById(R.id.contact_number)
        email = findViewById(R.id.email)
        address = findViewById(R.id.address)
        image = findViewById(R.id.imgview)
        add_img = findViewById(R.id.add_img)
    }

    //Initializing the database and the data by id and setting it to the respected EditText and to the ImageView
    private fun initDB() {
        dbHandler = DatabaseHandler(this)
        btn_delete?.visibility = View.INVISIBLE
        //If activity is for edit then it will get data and load to the views also make the delete button visible
        if (intent.getStringExtra("Mode") == "E") {
            isEditMode = true
            val contact: ContactModel = dbHandler!!.getContact(intent.getIntExtra("Id", 0))
            first_name?.setText(contact.first_name)
            last_name?.setText(contact.last_name)
            contact_number?.setText(contact.contact_number)
            email?.setText(contact.email)
            address?.setText(contact.address)
            val by: ByteArray = contact.byteArray
            if(by.size==1){
                Log.e("Empty", contact.byteArray.toString())
            }else{
                val bmp: Bitmap = BitmapFactory.decodeByteArray(by, 0, by.size)
                image?.setImageBitmap(Bitmap.createScaledBitmap(bmp, 70, 70, false))
                photo = bmp
            }

            btn_delete?.visibility = View.VISIBLE

        }
    }

    //Get the image when selected from gallery
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            image?.setImageURI(imageUri)
            photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri)
        }
    }

    //Perform different button click operations
    private fun initOperations() {
        // this will save the data to the database also check if any data is null or not
        btn_save?.setOnClickListener {
            var success: Boolean = false
            val stream = ByteArrayOutputStream()

            photo?.compress(Bitmap.CompressFormat.PNG, 100, stream)
            if(stream.size()==0){
                Toast.makeText(this, "Image is Required",Toast.LENGTH_SHORT).show()
            }else if(first_name?.text.toString().length==0||last_name?.text.toString().length==0||contact_number?.text.toString().length==0||email?.text.toString().length==0||address?.text.toString().length==0){
                Toast.makeText(this, "All Fields Are Required",Toast.LENGTH_SHORT).show()
            }else{
                if (stream.toByteArray().size / ONE_MB_TO_KB > PREFERRED_IMAGE_SIZE) {
                    Toast.makeText(this, "Image Size has to be less then 1MB",Toast.LENGTH_SHORT).show()
                }else{
                    if (!isEditMode) {
                        val contact: ContactModel = ContactModel()
                        contact.first_name = first_name?.text.toString()
                        contact.last_name = last_name?.text.toString()
                        contact.contact_number = contact_number?.text.toString()
                        contact.email = email?.text.toString()
                        contact.address = address?.text.toString()
                        contact.byteArray = stream.toByteArray()
                        success = dbHandler?.addContact(contact) as Boolean
                    } else {
                        val contact: ContactModel = ContactModel()
                        contact.id = intent.getIntExtra("Id", 0)
                        contact.first_name = first_name?.text.toString()
                        contact.last_name = last_name?.text.toString()
                        contact.contact_number = contact_number?.text.toString()
                        contact.email = email?.text.toString()
                        contact.address = address?.text.toString()
                        contact.byteArray = stream.toByteArray()
                        success = dbHandler?.updateContact(contact) as Boolean
                    }
                }
            }
            if (success)
                finish()
        }
        //this will delete the current contact from database with a conformation dialog
        btn_delete?.setOnClickListener {
            val dialog = AlertDialog.Builder(this).setTitle("Info")
                .setMessage("Click 'YES' Delete the Contact.")
                .setPositiveButton("YES") { dialog, i ->
                    val success = dbHandler?.deleteContact(intent.getIntExtra("Id", 0)) as Boolean
                    if (success)
                        finish()
                    dialog.dismiss()
                }
                .setNegativeButton("NO") { dialog, i ->
                    dialog.dismiss()
                }
            dialog.show()
        }

        //this will pick image and open the gallery
        add_img?.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }
    }

    //menu to send back to the mainactivity
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}