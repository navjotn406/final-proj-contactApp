@file:Suppress("DEPRECATION")

package com.example.contactapp

import DatabaseHandler
import RecyclerAdapter
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    //Creating Variables
    var recyclerAdapter: RecyclerAdapter? = null;
    private var fab: FloatingActionButton? = null
    private var recyclerView: RecyclerView? = null
    private var dbHandler: DatabaseHandler? = null
    var listcontacts: List<ContactModel> = ArrayList<ContactModel>()
    private var linearLayoutManager: LinearLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Initializing every things
        initViews()
        initOperations()
        initDB()
    }

    //Initializing the database and getting all the contacts and feeding it to the adapter
    private fun initDB() {
        dbHandler = DatabaseHandler(this)
        listcontacts = (dbHandler as DatabaseHandler).contact()
        recyclerAdapter = RecyclerAdapter(contactList = listcontacts, context = applicationContext)
        (recyclerView as RecyclerView).adapter = recyclerAdapter
    }

    //Initializing the button and recyclerview with desired adapter
    private fun initViews() {
        fab = findViewById<FloatingActionButton>(R.id.fab)
        recyclerView = findViewById(R.id.recycler_view)
        recyclerAdapter = RecyclerAdapter(contactList = listcontacts, context = applicationContext)
        linearLayoutManager = LinearLayoutManager(applicationContext)
        (recyclerView as RecyclerView).layoutManager = linearLayoutManager
    }

    //send to AddorEditActivity when clicked on the floating action button
    private fun initOperations() {
        fab?.setOnClickListener {
            val i = Intent(applicationContext, AddOrEditActivity::class.java)
            startActivity(i)
        }
    }

    //Crating the option menu to delete all the item at once
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    //if the delete icon is pressed then show a confirmation dialog
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_delete) {
            //if yes clicked on the dialog then it will delete all the contact otherwise dismiss the dialog
            val dialog = AlertDialog.Builder(this).setTitle("Info").setMessage("Click 'YES' Delete All Tasks")
                .setPositiveButton("YES") { dialog, _ ->
                    dbHandler!!.deleteAllContact()
                    initDB()
                    dialog.dismiss()
                }
                .setNegativeButton("NO") { dialog, _ ->
                    dialog.dismiss()
                }
            dialog.show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    //initializing the database when activity lifecycle resumes
    override fun onResume() {
        super.onResume()
        initDB()
    }
}