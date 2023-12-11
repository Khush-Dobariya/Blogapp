package com.example.blogapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blogapp.Model.BlogItemModel
import com.example.blogapp.databinding.ArticalItemBinding

class ArticleAdapter(
    private val context: Context,
    private var blogList: List<BlogItemModel>,
    private val itemClickListener: OnItemClickListener

) : RecyclerView.Adapter<ArticleAdapter.BlogViewHolder>() {

    interface OnItemClickListener{
        fun onEditClick(blogItem: BlogItemModel)
        fun onReadMoreClick(blogItem: BlogItemModel)
        fun onDeleteClick(blogItem: BlogItemModel)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ArticalItemBinding.inflate(inflater, parent, false)
        return BlogViewHolder(binding)
    }


    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        val blogItem = blogList[position]
        holder.bind(blogItem)
    }

    override fun getItemCount(): Int {
        return blogList.size
    }

    fun setData(blogSavedList: ArrayList<BlogItemModel>) {
        this.blogList = blogSavedList
        notifyDataSetChanged()
    }

    inner class BlogViewHolder(private val binding: ArticalItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(blogItem: BlogItemModel) {

            binding.heading.text = blogItem.heading
            Glide.with(binding.profile.context)
                .load(blogItem.profileImage)
                .into(binding.profile)
            binding.userName.text = blogItem.username
            binding.date.text = blogItem.date
            binding.post.text = blogItem.post

            //Handle Read More Click
            binding.readmorebutton.setOnClickListener {
                itemClickListener.onReadMoreClick(blogItem)
            }
            //Handle Edit Click
            binding.editbutton.setOnClickListener {
                itemClickListener.onEditClick(blogItem)
            }
            //Handle Delete Click
            binding.deletebutton.setOnClickListener {
                itemClickListener.onDeleteClick(blogItem)
            }
        }

    }
}