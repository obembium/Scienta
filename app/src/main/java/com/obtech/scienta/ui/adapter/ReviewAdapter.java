package com.obtech.scienta.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.obtech.scienta.model.Review.Review;
import com.obtech.scienta.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<com.obtech.scienta.model.Review.Review> reviews;
    private Context context;

    public ReviewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        com.obtech.scienta.model.Review.Review review = reviews.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        if (reviews == null) {
            return 0;
        }
        return reviews.size();
    }

    public void setReviews(List<com.obtech.scienta.model.Review.Review> review) {
        this.reviews = review;
        notifyDataSetChanged();

    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.author)
        TextView authorView;
        @BindView(R.id.content)
        TextView contentView;

        private ReviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void bind(Review review) {
            authorView.setText(review.getAuthor());
            contentView.setText(review.getContent());
        }
    }
}
