package com.aashushaikh.journalapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.aashushaikh.journalapp.databinding.JournalRowBinding
import com.aashushaikh.journalapp.model.Journal
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat

class JournalRecyclerAdapter(val context: Context, var journalList: List<Journal>): RecyclerView.Adapter<JournalRecyclerAdapter.MyViewHolder>() {

    lateinit var binding: JournalRowBinding

    inner class MyViewHolder(binding: JournalRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(journal: Journal) {
            binding.journal = journal
            val date = journal.timestamp.toDate()
            val dateFormat = SimpleDateFormat("dd-MM-yyyy")
            val timeFormat = SimpleDateFormat("HH:mm:ss")

            val dateString: String = dateFormat.format(date) // e.g., "2024-06-14"
            val timeString: String = timeFormat.format(date)
            binding.journalTimestamp.text = "$dateString, $timeString"
            Glide.with(context).load(journal.imageUrl).into(binding.imgJournal)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        binding = JournalRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return journalList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val journal = journalList[position]
        holder.bind(journal)
        binding.btnShare.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, journal.title)
            intent.putExtra(Intent.EXTRA_TEXT, "$journal.title\n\n$journal.thoughts\n\n$journal.imageUrl")
            context.startActivity(Intent.createChooser(intent, "Share using"))
        }
    }
}