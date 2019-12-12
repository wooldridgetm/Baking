package com.tomwo.app.baking.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tomwo.app.baking.R;
import com.tomwo.app.baking.domain.Direction;

import java.util.List;

/**
 * Created by wooldridgetm on 9/22/17.
 */

public final class DetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    public static final int VIEW_TYPE_DEFAULT = 1000;
    public static final int VIEW_TYPE_INTRO = 1001;

    public interface IListener
    {
        void listItem_triggeredHandler(int position);
    }

    private Context mContext;
    private IListener mParent;
    private List<Direction> mData;

    public DetailAdapter(Context context, List<Direction> dir)
    {
        this.mContext = context;

        if (context instanceof IListener)
        {
            this.mParent = (IListener) context;
        }
        this.mData = dir;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case VIEW_TYPE_INTRO:
                return IntroViewHolder.newInstance(parent,this.mParent);

            default:
                return DefaultViewHolder.newInstance(parent, this.mParent);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        final Direction item = this.mData.get(position);

        if (holder.getItemViewType() == VIEW_TYPE_INTRO)
        {
            IntroViewHolder vh = (IntroViewHolder) holder;

            vh.title.setText(item.getTitle());

        } else
        {
            DefaultViewHolder vh = (DefaultViewHolder) holder;

            vh.order.setText(String.valueOf(item.getOrder()+"."));
            vh.title.setText(item.getTitle());
            vh.detail.setText(item.getDetail());
        }
    }

    @Override
    public int getItemCount()
    {
        return this.mData != null ? this.mData.size() : 0;
    }

    @Override
    public int getItemViewType(int position)
    {
        return this.mData.get(position).getOrder() == -1 ? VIEW_TYPE_INTRO : VIEW_TYPE_DEFAULT;
    }

    /**
     * ViewHolder
     */
    private static final class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private static final String TAG = "DefaultViewHolder";

        final TextView order;
        final TextView title;
        final TextView detail;

        // set in Fx 'newInstance'
        private IListener mListener;

        private DefaultViewHolder(View itemView)
        {
            super(itemView);

            this.order = itemView.findViewById(R.id.orderTextView);
            this.title = itemView.findViewById(R.id.title);
            this.detail = itemView.findViewById(R.id.detailTextView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            Log.d(TAG, "Fx listItem_triggeredHandler(View view)");
            if (this.mListener != null)
            {
                this.mListener.listItem_triggeredHandler(this.getAdapterPosition());
            }
        }

        static DefaultViewHolder newInstance(@NonNull ViewGroup parent, IListener listener)
        {
            DefaultViewHolder vh = new DefaultViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_direction, parent, false));
            vh.mListener = listener;
            return vh;
        }
    }

    /**
     * ViewHolder (for Introduction)
     */
    private static final class IntroViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private IListener mListener;
        final TextView title;

        private IntroViewHolder(View itemView)
        {
            super(itemView);

            this.title = itemView.findViewById(R.id.servings);
        }

        @Override
        public void onClick(View view)
        {
            if (this.mListener != null)
            {
                this.mListener.listItem_triggeredHandler(this.getAdapterPosition());
            }
        }

        static IntroViewHolder newInstance(@NonNull ViewGroup parent, IListener listener)
        {
            IntroViewHolder vh = new IntroViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_direction, parent, false));
            vh.mListener = listener;
            return vh;
        }
    }
}
