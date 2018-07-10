package com.nytimes.android.sample.reddit

import android.support.v7.widget.RecyclerView
import android.view.View
import com.nytimes.android.sample.R
import com.nytimes.android.sample.data.model.Post
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.article_item.view.*


class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun onBind(article: Post) {
        itemView.title!!.text = article.title
        article.nestedThumbnail()?.url?.let { showImage(it) }
    }

    private fun showImage(url: String) {
        Picasso.with(itemView.context)
                .load(url)
                .placeholder(R.color.gray80)
                .into(itemView.thumbnail)
    }
}
