
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.contactapp.AddOrEditActivity
import com.example.contactapp.ContactModel
import com.example.contactapp.R


class RecyclerAdapter(contactList: List<ContactModel>, internal var context: Context) : RecyclerView.Adapter<RecyclerAdapter.ContactViewHolder>() {

    //initialzing constructors variables
    internal var contactList: List<ContactModel> = ArrayList()
    init {
        this.contactList = contactList
    }

    //view holder to set the xml file
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.rv_items, parent, false)
        return ContactViewHolder(view)
    }

    //Binding the view holder with the views and setting the texts and images
    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contactList[position]
        holder.name.text = contact.first_name +" " + contact.last_name
        holder.contact.text = contact.contact_number
        holder.email.text = contact.email
        holder.address.text = contact.address
        Log.e("ASS", contact.byteArray.toString())
        val by: ByteArray = contact.byteArray
        if(by.size==1){
            Log.e("Empty", contact.byteArray.toString())
        }else{
            val bmp: Bitmap = BitmapFactory.decodeByteArray(by, 0, by.size)
            holder.img.setImageBitmap(Bitmap.createScaledBitmap(bmp, 70, 70, false))
        }

        //on click to send to the addoreditactivity to edit the item
        holder.itemView.setOnClickListener {
            val i = Intent(context, AddOrEditActivity::class.java)
            i.putExtra("Mode", "E")
            i.putExtra("Id", contact.id)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(i)
        }
    }

    //getting the item count on the list
    override fun getItemCount(): Int {
        return contactList.size
    }

    //initializing the view holder
    inner class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.name) as TextView
        var email: TextView = view.findViewById(R.id.email) as TextView
        var contact: TextView = view.findViewById(R.id.contact) as TextView
        var address: TextView = view.findViewById(R.id.address) as TextView
        var img: ImageView = view.findViewById(R.id.imgview) as ImageView
        var itemView: LinearLayout = view.findViewById(R.id.c) as LinearLayout
    }

}