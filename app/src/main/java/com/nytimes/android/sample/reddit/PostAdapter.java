package com.nytimes.android.sample.reddit;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nytimes.android.sample.R;
import com.nytimes.android.sample.data.model.Post;

import java.util.ArrayList;
import java.util.List;


public class PostAdapter extends RecyclerView.Adapter<PostViewHolder> {

    private List<Post> articles = new ArrayList<>();
    private LayoutInflater inflater;

    public PostAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_store, parent, false);
        return new PostViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        holder.onBind(articles.get(position));
    }

    @Override
    public void onViewRecycled(PostViewHolder holder) {
        super.onViewRecycled(holder);
        holder.onUnbind();
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public void setPosts(List<Post> posts) {
        articles = new ArrayList<>(posts);
        notifyDataSetChanged();
    }
}
