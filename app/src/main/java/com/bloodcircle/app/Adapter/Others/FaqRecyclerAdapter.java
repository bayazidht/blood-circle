package com.bloodcircle.app.Adapter.Others;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bloodcircle.app.Model.Others.FaqItem;
import com.bloodcircle.app.R;

import java.util.ArrayList;

public class FaqRecyclerAdapter extends RecyclerView.Adapter<FaqRecyclerAdapter.ViewHolder> {

    private final Context mContext;
    private final ArrayList<FaqItem> faqItems;

    public FaqRecyclerAdapter(Context context, ArrayList<FaqItem> faqItems) {
        this.mContext = context;
        this.faqItems = faqItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_faq, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FaqItem item = faqItems.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvDesc.setText(item.getDesc());
    }

    @Override
    public int getItemCount() {
        return faqItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView tvTitle, tvDesc;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDesc = itemView.findViewById(R.id.tv_desc);
        }
    }

}
