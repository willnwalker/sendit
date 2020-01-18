package xyz.willnwalker.sendit

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.google.android.material.snackbar.Snackbar

class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    private var context: Context?= null
    private val titles = arrayOf("Class One",
        "Class Two", "Class Three", "Class Four",
        "Class Five", "Class Six", "Class Seven",
        "Class Eight")

    private val details = arrayOf("about class one", "about class two",
        "about class three", "about class four",
        "about class file", "about class six",
        "about class seven", "about class eight")

    private val image = R.drawable.polling_icon

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var itemImage: ImageView
        var itemTitle: TextView
        var itemDetail: TextView

        init {
            itemImage = itemView.findViewById(R.id.item_image)
            itemTitle = itemView.findViewById(R.id.item_title)
            itemDetail = itemView.findViewById(R.id.item_detail)

            itemView.setOnClickListener { v: View  ->
                var position: Int = getAdapterPosition() + 1

                Snackbar.make(v, "Enter class $position?",
                    Snackbar.LENGTH_LONG).setAction("Join Session",
                    View.OnClickListener { context?.startActivity(Intent(context, PollActivity::class.java)) }).show()
            }
        }
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.card_layout, viewGroup, false)
        val vh = ViewHolder(v)
        // set the Context here
        context = viewGroup.context
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.itemTitle.text = titles[i]
        viewHolder.itemDetail.text = details[i]
        viewHolder.itemImage.setImageResource(image)
        val context = viewHolder.itemView.context
    }

    override fun getItemCount(): Int {
        return titles.size
    }
}