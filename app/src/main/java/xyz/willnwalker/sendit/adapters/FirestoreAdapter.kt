package xyz.willnwalker.sendit.adapters

import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import java.util.ArrayList

/**
 * RecyclerView adapter for displaying Firestore queries
 *
 */
abstract class FirestoreAdapter<VH : RecyclerView.ViewHolder>(private var query: Query?) :
    RecyclerView.Adapter<VH>(),
    EventListener<QuerySnapshot> {

    private var registration: ListenerRegistration? = null

    private val snapshots = ArrayList<DocumentSnapshot>()

    override fun onEvent(documentSnapshots: QuerySnapshot?, e: FirebaseFirestoreException?) {
        if (e != null) {
            Log.w(TAG, "onEvent:error", e)
            onError(e)
            return
        }

        if (documentSnapshots == null) {
            return
        }

        for (change in documentSnapshots.documentChanges) {
            when (change.type) {
                DocumentChange.Type.ADDED -> onDocumentAdded(change)
                DocumentChange.Type.MODIFIED -> onDocumentModified(change)
                DocumentChange.Type.REMOVED -> onDocumentRemoved(change)
            }
        }

        onDataChanged()
    }

    fun startListening() {
        if (query != null && registration == null) {
            registration = query!!.addSnapshotListener(this)
        }
    }

    fun stopListening() {
        registration?.remove()
        registration = null
        snapshots.clear()
        notifyDataSetChanged()
    }

    fun setQuery(query: Query) {
        // Stop listening & clear data
        stopListening()
        snapshots.clear()
        notifyDataSetChanged()
        // Listen to new query
        this.query = query
        startListening()
    }

    open fun onError(e: FirebaseFirestoreException) {
        Log.w(TAG, "onError", e)
    }

    open fun onDataChanged() {
        // upon dataChange, call this function.
    }

    override fun getItemCount(): Int {
        return snapshots.size
    }

    protected fun getSnapshot(index: Int): DocumentSnapshot {
        return snapshots[index]
    }

    private fun onDocumentAdded(change: DocumentChange) {
        snapshots.add(change.newIndex, change.document)
        notifyItemInserted(change.newIndex)
    }

    private fun onDocumentModified(change: DocumentChange) {
        if (change.oldIndex == change.newIndex) {
            // Item changed but remained in same position
            snapshots[change.oldIndex] = change.document
            notifyItemChanged(change.oldIndex)
        } else {
            // Item changed and changed position
            snapshots.removeAt(change.oldIndex)
            snapshots.add(change.newIndex, change.document)
            notifyItemMoved(change.oldIndex, change.newIndex)
        }
    }

    private fun onDocumentRemoved(change: DocumentChange) {
        snapshots.removeAt(change.oldIndex)
        notifyItemRemoved(change.oldIndex)
    }

    companion object {

        private const val TAG = "FirestoreAdapter"
    }
}