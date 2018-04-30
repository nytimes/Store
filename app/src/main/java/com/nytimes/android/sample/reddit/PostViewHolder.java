package com.nytimes.android.sample.reddit;

import android.app.Application;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nytimes.android.sample.R;
import com.nytimes.android.sample.data.model.Image;
import com.nytimes.android.sample.data.model.ImmutableImage;
import com.nytimes.android.sample.data.model.Post;
import com.nytimes.android.sample.util.BitmapTransform;
import com.nytimes.android.sample.util.DeviceUtils;
import com.squareup.picasso.Picasso;


public class PostViewHolder extends RecyclerView.ViewHolder {

    private int maxHeight;
    private int maxWidth;
    private TextView title;
    private ImageView thumbnail;
    private View topSpacer;
    private final DeviceUtils deviceUtils;

    public PostViewHolder(View itemView) {
        super(itemView);
        deviceUtils = new DeviceUtils((Application) itemView.getContext().getApplicationContext());
        findViews(itemView);
        setMaxDimensions(itemView);
    }

    public void onBind(Post article) {
        title.setText(article.title());
        if (article.nestedThumbnail().isPresent()) {
            showImage(article);
        }
    }

    private void showImage(Post article) {
        Image nestedImage = article.nestedThumbnail().get();
        Image image = ImmutableImage
                .builder()
                .height(nestedImage.height())
                .width(nestedImage.width())
                .url(nestedImage.url())
                .build();
        BitmapTransform bitmapTransform = new BitmapTransform(maxWidth, maxHeight, image);

        int targetWidth = bitmapTransform.targetWidth;
        int targetHeight = bitmapTransform.targetHeight;

        setSpacer(targetWidth, targetHeight);

        setupThumbnail(targetWidth, targetHeight);

        Picasso.with(itemView.getContext())
                .load(image.url())
                .transform(bitmapTransform)
                .resize(targetWidth, targetHeight)
                .centerInside()
                .placeholder(R.color.gray80)
                .into(thumbnail);
    }

    private void setSpacer(int targetWidth, int targetHeight) {
        if (targetWidth >= targetHeight) {
            topSpacer.setVisibility(View.GONE);
        } else {
            topSpacer.setVisibility(View.VISIBLE);
        }
    }

    private void setupThumbnail(int targetWidth, int targetHeight) {
        thumbnail.setMaxWidth(targetWidth);
        thumbnail.setMaxHeight(targetHeight);
        thumbnail.setMinimumWidth(targetWidth);
        thumbnail.setMinimumHeight(targetHeight);
        thumbnail.requestLayout();
    }

    private void setMaxDimensions(View itemView) {
        int screenWidth;
        int screenHeight;
        screenWidth = deviceUtils.getScreenWidth();
        screenHeight = deviceUtils.getScreenHeight();

        if (screenWidth > screenHeight) {
            screenHeight = deviceUtils.getScreenWidth();
            screenWidth = deviceUtils.getScreenHeight();
        }

        maxHeight = (int) (screenHeight * .55f);
        int margin = itemView.getContext().getResources().getDimensionPixelSize(R.dimen.post_horizontal_margin);
        maxWidth = screenWidth - (2 * margin);
    }

    private void findViews(View itemView) {
        title = (TextView) itemView.findViewById(R.id.title);
        thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
        topSpacer = itemView.findViewById(R.id.topSpacer);
    }
}
