package com.dbsh.frameapp

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dbsh.frameapp.databinding.ItemImageBinding
import com.dbsh.frameapp.databinding.ItemLoadMoreBinding

class ImageAdapter(private val itemClickListener: ItemClickListener): ListAdapter<ImageItems , RecyclerView.ViewHolder>(
    // 데이터가 변경됨을 체킹하기 위해 Implement한 메서드 -> RecyclerView와 달리 알아서 UI 처리
    object : DiffUtil.ItemCallback<ImageItems>() {
        override fun areItemsTheSame(oldItem: ImageItems, newItem: ImageItems): Boolean {
            // Identity, 같은 값을 참조하고 있는지 체크 (동일성 체크)
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ImageItems, newItem: ImageItems): Boolean {
            // Equality, 같은 값인지 체크 (동등성 체크)
            return oldItem == newItem
        }

    }
) {
    override fun getItemCount(): Int {
        val originSize = currentList.size
        return if(originSize == 0) 0 else originSize.inc()
    }

    // 두 가지 이상 타입을 체크하기 위함
    override fun getItemViewType(position: Int): Int {
        return if(itemCount.dec() == position) ITEM_LOAD_MORE else ITEM_IMAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return when(viewType) {
            ITEM_IMAGE -> {
                val binding = ItemImageBinding.inflate(inflater, parent, false)
                ImageViewHolder(binding)
            } else -> {
                val binding = ItemLoadMoreBinding.inflate(inflater, parent, false)
                LoadMoreViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is ImageViewHolder -> {
                holder.bind(currentList[position] as ImageItems.Image)
            }
            is LoadMoreViewHolder -> {
                holder.bind(itemClickListener)
            }
        }
    }

    interface ItemClickListener {
        fun onLoadMoreClick()
    }

    companion object {
        const val ITEM_IMAGE = 0
        const val ITEM_LOAD_MORE = 1
    }
}

sealed class ImageItems {

    data class Image(
        val uri: Uri,
    ): ImageItems()

    object LoadMore: ImageItems()
}

class ImageViewHolder(private val binding: ItemImageBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ImageItems.Image) {
        binding.previewImageView.setImageURI(item.uri)
    }
}

class LoadMoreViewHolder(binding: ItemLoadMoreBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(itemClickListener: ImageAdapter.ItemClickListener) {
        itemView.setOnClickListener {
            itemClickListener.onLoadMoreClick()
        }
    }
}