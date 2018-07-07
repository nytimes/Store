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
        article.nestedThumbnail()?.let { showImage(article) }
    }

    private fun showImage(article: Post) {
        val nestedImage = article.nestedThumbnail()
        nestedImage?.url?.let {
            Picasso.with(itemView.context)
                    .load(it)
                    .placeholder(R.color.gray80)
                    .into(itemView.thumbnail)
        }
    }
}
