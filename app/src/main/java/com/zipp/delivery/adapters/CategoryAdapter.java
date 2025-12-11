package com.zipp.delivery.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.zipp.delivery.R;
import com.zipp.delivery.models.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Category> categories;
    private OnCategoryClickListener listener;
    private int selectedPosition = -1;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public CategoryAdapter(List<Category> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.bind(category, position);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView card;
        ImageView ivIcon;
        TextView tvName;

        ViewHolder(View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card_category);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvName = itemView.findViewById(R.id.tv_name);
        }

        void bind(Category category, int position) {
            tvName.setText(category.getName());
            ivIcon.setImageResource(category.getIconResId());

            int color = ContextCompat.getColor(itemView.getContext(), category.getColorResId());
            ivIcon.setColorFilter(color);

            boolean isSelected = position == selectedPosition;
            if (isSelected) {
                card.setCardBackgroundColor(color);
                ivIcon.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.white));
                tvName.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.white));
            } else {
                card.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.surface));
                ivIcon.setColorFilter(color);
                tvName.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_primary));
            }

            itemView.setOnClickListener(v -> {
                int previousSelected = selectedPosition;
                selectedPosition = position;
                notifyItemChanged(previousSelected);
                notifyItemChanged(selectedPosition);
                listener.onCategoryClick(category);
            });
        }
    }
}



