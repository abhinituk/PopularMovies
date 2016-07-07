package com.sunshine.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sunshine.popularmovies.R;
import com.sunshine.popularmovies.data.MovieContract;

/**
 * Created by Abhishek on 04-07-2016.
 */
public class CustomReviewAdapter extends RecyclerView.Adapter<CustomReviewAdapter.ViewHolder> {
    String noReview = "No Review Available";

    private Cursor mCursor;
    private final Context mContext;

    public CustomReviewAdapter(Context context) {
        super();
        mContext = context;
    }

    @Override
    public CustomReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_review, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomReviewAdapter.ViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        String author = mCursor.getString(mCursor.getColumnIndex(MovieContract.ReviewEntry.COL_REVIEW_AUTHOR));
        String content = mCursor.getString(mCursor.getColumnIndex(MovieContract.ReviewEntry.COL_REVIEW_CONTENT));
        int positionForView= position+1;
        String positionUsed= "Review #"+positionForView+":\n"+author;

        holder.authorTextView.setText(positionUsed);
        holder.contentTextView.setText(content);

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        final CardView cardView;
        final TextView authorTextView;
        final TextView contentTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.grid_item_review_cardView);
            authorTextView = (TextView) itemView.findViewById(R.id.list_item_review_author_textView);
            contentTextView = (TextView) itemView.findViewById(R.id.list_item_review_content_textView);
        }
    }

    public void swapCursor(Cursor data) {
        mCursor = data;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return mCursor;
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) {
            return 0;
        } else
            return mCursor.getCount();
    }
}
