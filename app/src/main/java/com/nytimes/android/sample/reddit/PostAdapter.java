package com.nytimes.android.sample.reddit;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nytimes.android.sample.R;
import com.nytimes.android.sample.data.model.Post;

import java.util.ArrayList;
import java.util.List;



public class PostAdapter extends RecyclerView.Adapter<PostViewHolder> {

    private final List<Post> articles = new ArrayList<>();

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.article_item, parent, false);
        return new PostViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        holder.onBind(articles.get(position));
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public void setPosts(List<Post> articlesToAdd) {
        articles.clear();
        articles.addAll(articlesToAdd);
        notifyDataSetChanged();
    }
}
