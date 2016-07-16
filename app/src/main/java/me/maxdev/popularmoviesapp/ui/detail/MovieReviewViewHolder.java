package me.maxdev.popularmoviesapp.ui.detail;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.maxdev.popularmoviesapp.R;
import me.maxdev.popularmoviesapp.util.OnItemClickListener;

public class MovieReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.text_movie_review_content)
    TextView content;
    @BindView(R.id.text_movie_review_author)
    TextView author;

    @Nullable
    private OnItemClickListener onItemClickListener;

    public MovieReviewViewHolder(View itemView, @Nullable OnItemClickListener onItemClickListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.onItemClickListener = onItemClickListener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(view, getAdapterPosition());
        }
    }
}
