import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.contactapp.ContactModel

class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, DatabaseHandler.DB_NAME, null, DatabaseHandler.DB_VERSION) {

    //creating the database table with desired columns
    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TABLE = "CREATE TABLE $TABLE_NAME ($ID INTEGER PRIMARY KEY, $FIRSTNAME TEXT, $LASTNAME TEXT, $CONTACT TEXT, $EMAIL TEXT, $ADDRESS TEXT ,$PROFILE_IMAGE BLOB);"
        db.execSQL(CREATE_TABLE)
    }

    //checking if the database exits or not
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
        db.execSQL(DROP_TABLE)
        onCreate(db)
    }


    //add new contact to the database by the contact model
    fun addContact(tasks: ContactModel): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(FIRSTNAME, tasks.first_name)
        values.put(LASTNAME, tasks.last_name)
        values.put(CONTACT, tasks.contact_number)
        values.put(EMAIL, tasks.email)
        values.put(ADDRESS, tasks.address)
        values.put(PROFILE_IMAGE, tasks.byteArray)
        val _success = db.insert(TABLE_NAME, null, values)
        db.close()
        Log.v("InsertedId", "$_success")
        return (Integer.parseInt("$_success") != -1)
    }

    @SuppressLint("Range")
    //get the contact by its id from the database
    fun getContact(_id: Int): ContactModel {
        val contact = ContactModel()
        val db = writableDatabase
        val selectQuery = "SELECT  * FROM $TABLE_NAME WHERE $ID = $_id"
        val cursor = db.rawQuery(selectQuery, null)

        cursor?.moveToFirst()
        contact.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ID)))
        contact.first_name = cursor.getString(cursor.getColumnIndex(FIRSTNAME))
        contact.last_name = cursor.getString(cursor.getColumnIndex(LASTNAME))
        contact.contact_number = cursor.getString(cursor.getColumnIndex(CONTACT))
        contact.email = cursor.getString(cursor.getColumnIndex(EMAIL))
        contact.address = cursor.getString(cursor.getColumnIndex(ADDRESS))
        contact.byteArray = cursor.getBlob(cursor.getColumnIndex(PROFILE_IMAGE))

        cursor.close()
        return contact
    }

    @SuppressLint("Range")
    //getting all contacts from the database
    fun contact(): List<ContactModel> {
        val contactList = ArrayList<ContactModel>()
        val db = writableDatabase
        val selectQuery = "SELECT  * FROM $TABLE_NAME"
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val contact = ContactModel()
                    contact.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ID)))
                    contact.first_name = cursor.getString(cursor.getColumnIndex(FIRSTNAME))
                    contact.last_name = cursor.getString(cursor.getColumnIndex(LASTNAME))
                    contact.contact_number = cursor.getString(cursor.getColumnIndex(CONTACT))
                    contact.email = cursor.getString(cursor.getColumnIndex(EMAIL))
                    contact.address = cursor.getString(cursor.getColumnIndex(ADDRESS))
                    contact.byteArray = cursor.getBlob(cursor.getColumnIndex(PROFILE_IMAGE))
                    contactList.add(contact)
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        return contactList
    }

    //editing the existing contact
    fun updateContact(contact: ContactModel): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(FIRSTNAME, contact.first_name)
        values.put(LASTNAME, contact.last_name)
        values.put(CONTACT, contact.contact_number)
        values.put(EMAIL, contact.email)
        values.put(ADDRESS, contact.address)
        values.put(PROFILE_IMAGE, contact.byteArray)
        val _success = db.update(TABLE_NAME, values, ID + "=?", arrayOf(contact.id.toString())).toLong()
        db.close()
        return Integer.parseInt("$_success") != -1
    }

    //deleting the contact by its id
    fun deleteContact(_id: Int): Boolean {
        val db = this.writableDatabase
        val _success = db.delete(TABLE_NAME, ID + "=?", arrayOf(_id.toString())).toLong()
        db.close()
        return Integer.parseInt("$_success") != -1
    }

    //deleting all the contacts
    fun deleteAllContact(): Boolean {
        val db = this.writableDatabase
        val _success = db.delete(TABLE_NAME, null, null).toLong()
        db.close()
        return Integer.parseInt("$_success") != -1
    }

    //creating all the required variables
    companion object {
        private const val DB_VERSION = 1
        private const val DB_NAME = "Contacts"
        private const val TABLE_NAME = "Contact_Table"
        private const val ID = "Id"
        private const val FIRSTNAME = "first_name"
        private const val LASTNAME = "last_name"
        private const val CONTACT = "contact"
        private const val EMAIL = "email"
        private const val ADDRESS = "address"
        private const val PROFILE_IMAGE = "img"
    }
}