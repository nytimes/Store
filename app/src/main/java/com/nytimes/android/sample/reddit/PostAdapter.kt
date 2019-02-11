package com.nytimes.android.sample.reddit

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.nytimes.android.sample.R
import com.nytimes.android.sample.data.model.Post


class PostAdapter : RecyclerView.Adapter<PostViewHolder>() {

    private val articles = ArrayList<Post>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(
                parent.context).inflate(R.layout.article_item, parent, false)
        return PostViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.onBind(articles[position])
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    fun setPosts(articlesToAdd: List<Post>) {
        articles.clear()
        articles.addAll(articlesToAdd)
        notifyDataSetChanged()
    }
}
