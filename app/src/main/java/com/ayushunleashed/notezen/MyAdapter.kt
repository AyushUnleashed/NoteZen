package com.ayushunleashed.notezen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.ayushunleashed.notezen.models.NotesModel
import kotlinx.android.synthetic.main.item_note.view.*

class MyAdapter(private var notesList:ArrayList<NotesModel>): RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.MyViewHolder {
        val itemView =LayoutInflater.from(parent.context).inflate(R.layout.item_note,parent,false)

        return  MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyAdapter.MyViewHolder, position: Int) {
        val note:NotesModel =notesList[position]
        holder.title.text = note.title
        holder.description.text=note.description
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    public class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    {
        var title:TextView =itemView.findViewById(R.id.noteTitle)
        val description:TextView = itemView.findViewById(R.id.noteDescription)
    }

}

//interface INotesRVAdapter{
//    fun onItemClicked(note: NotesModel)
//}