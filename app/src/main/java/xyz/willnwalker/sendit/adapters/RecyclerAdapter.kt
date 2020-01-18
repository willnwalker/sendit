package xyz.willnwalker.sendit.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.synthetic.main.card_layout.view.*
import xyz.willnwalker.sendit.R
import xyz.willnwalker.sendit.models.SelectedClass

open class RecyclerAdapter(query: Query, private val listener: OnSelectedListener) :
    FirestoreAdapter<RecyclerAdapter.ViewHolder>(query){

    interface OnSelectedListener {

        fun onSelected(selected: DocumentSnapshot)
    }

    private var context: Context?= null
    private val db = FirebaseFirestore.getInstance()

    private val image = R.drawable.polling_icon

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(
            snapshot: DocumentSnapshot,
            listener: OnSelectedListener?
        ) {

            val selected = snapshot.toObject(SelectedClass::class.java)
            if (selected == null) {
                return
            }

            val resources = itemView.resources
            itemView.className.text = snapshot.id

            // Click listener
            itemView.setOnClickListener {
                listener?.onSelected(snapshot)
            }
        }
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        return ViewHolder(inflater.inflate(R.layout.card_layout, viewGroup, false))

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getSnapshot(position), listener)
    }

}