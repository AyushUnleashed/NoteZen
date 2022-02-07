package com.ayushunleashed.notezen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.ayushunleashed.notezen.models.NotesModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.synthetic.main.activity_create_note.*
import kotlinx.android.synthetic.main.item_note.view.*

class MyAdapter(options: FirestoreRecyclerOptions<NotesModel>,val listner:INotesRVAdapter) :FirestoreRecyclerAdapter<NotesModel,MyAdapter.MyViewHolder>(
    options
){

    public class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    {
        var title:TextView =itemView.findViewById(R.id.noteTitle)
        val description:TextView = itemView.findViewById(R.id.noteDescription)
        var date:TextView=itemView.findViewById(R.id.noteDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =LayoutInflater.from(parent.context).inflate(R.layout.item_note,parent,false)
        val itemViewViewHolder = MyViewHolder(itemView)

        itemView.setOnClickListener {
            listner.onItemClicked(snapshots.getSnapshot(itemViewViewHolder.adapterPosition).id,itemViewViewHolder.title.text.toString(),itemViewViewHolder.description.text.toString())

        }

        itemView.setOnLongClickListener {
            listner.onItemLongClicked(snapshots.getSnapshot(itemViewViewHolder.adapterPosition).id)
            true }


        return  itemViewViewHolder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: NotesModel) {
        holder.title.text = model.title
        holder.description.text=model.description
        holder.date.text =model.date
    }
}


interface INotesRVAdapter{
    fun onItemClicked(note: String,title:String,description:String)
    fun onItemLongClicked(note: String)
}