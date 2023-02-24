package com.dbsh.wordbookapp

import android.content.ClipData.Item
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dbsh.wordbookapp.databinding.ItemWordBinding

class WordAdapter(val list: MutableList<Word>, private val itemClickListener: ItemClickListener? = null) :
    RecyclerView.Adapter<WordAdapter.WordViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val inflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = ItemWordBinding.inflate(inflater, parent, false)
        return WordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val word = list[position]
        holder.bind(word)
//        holder.binding.apply {
//            val word = list[position]
//            textTextView.text = word.text
//            meanTextView.text = word.mean
//            typeChip.text = word.type
//        }
        holder.itemView.setOnClickListener {
            itemClickListener?.onClick(word, position)
        }
    }

    override fun getItemCount() = list.size

    class WordViewHolder(private val binding: ItemWordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(word: Word) {
            binding.apply {
                textTextView.text = word.text
                meanTextView.text = word.mean
                typeChip.text = word.type
            }
        }
    }

    interface ItemClickListener {
        fun onClick(word: Word, position: Int)
    }
}