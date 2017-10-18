package com.nytimes.android.sample.reddit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Optional;
import com.nytimes.android.sample.R;
import com.nytimes.android.sample.data.model.Image;
import com.nytimes.android.sample.data.model.Post;
import com.squareup.picasso.Picasso;


class PostViewHolder extends RecyclerView.ViewHolder {

    private final ImageView thumbnail;
    private final TextView title;
    private final TextView byline;
    private final TextView points;

    PostViewHolder(View itemView) {
        super(itemView);
        thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
        title = (TextView) itemView.findViewById(R.id.title);
        byline = (TextView) itemView.findViewById(R.id.byline);
        points = (TextView) itemView.findViewById(R.id.points);
    }

    @SuppressLint("StringFormatInvalid")
    public void onBind(Post post) {
        title.setText(post.title());
        points.setText(String.valueOf(post.score()));

        CharSequence timePosted = DateUtils.getRelativeTimeSpanString(
                post.created() * 1000,
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE);
        byline.setText(itemView.getContext().getString(R.string.byline,
                timePosted, post.author()));
        showImage(post.nestedThumbnail());

        itemView.setOnClickListener(v -> {
            Context context = itemView.getContext();
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(post.url()));
            context.startActivity(browserIntent);
        });
    }

    private void showImage(Optional<Image> post) {
        if (post.isPresent()) {
            Picasso.with(itemView.getContext())
                    .load(post.get().url())
                    .fit()
                    .centerInside()
                    .placeholder(R.color.gray80)
                    .into(thumbnail);
        } else {
            thumbnail.setImageResource(R.color.gray80);
        }
    }

    public void onUnbind() {
        Picasso.with(itemView.getContext()).cancelRequest(thumbnail);
    }

}
