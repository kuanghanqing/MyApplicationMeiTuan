package com.example.myapplicationmeituan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.myapplicationmeituan.R;
import com.example.myapplicationmeituan.bean.Dish;
import java.util.List;

public class DishAdapter extends RecyclerView.Adapter<DishAdapter.DishHolder> {

    private List<Dish> dishList;
    private OnDishClickListener listener;

    public interface OnDishClickListener{
        void onAdd(Dish dish);
        void onMinus(Dish dish);
    }

    public void setListener(OnDishClickListener listener) {
        this.listener = listener;
    }

    public DishAdapter(List<Dish> list) {
        this.dishList = list;
    }

    @NonNull
    @Override
    public DishHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dish, parent, false);
        return new DishHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DishHolder holder, int position) {
        Dish dish = dishList.get(position);
        holder.tvName.setText(dish.getName());
        holder.tvSale.setText("月售" + dish.getSale());
        holder.tvPrice.setText("¥" + dish.getPrice());

        if (dish.getImageRes() != 0) {
            Glide.with(holder.itemView.getContext())
                    .load(dish.getImageRes())
                    .centerCrop()
                    .into(holder.ivDish);
        }

        int count = dish.getCount();
        if(count > 0){
            holder.tvCount.setVisibility(View.VISIBLE);
            holder.ivMinus.setVisibility(View.VISIBLE);
            holder.tvCount.setText(String.valueOf(count));
        }else{
            holder.tvCount.setVisibility(View.GONE);
            holder.ivMinus.setVisibility(View.GONE);
        }

        holder.ivAdd.setOnClickListener(v->{
            if(listener != null) listener.onAdd(dish);
        });
        holder.ivMinus.setOnClickListener(v->{
            if(listener != null) listener.onMinus(dish);
        });
    }

    @Override
    public int getItemCount() {
        return dishList.size();
    }

    public static class DishHolder extends RecyclerView.ViewHolder{
        TextView tvName, tvSale, tvPrice, tvCount;
        ImageView ivDish, ivAdd, ivMinus;
        public DishHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_dish_name);
            tvSale = itemView.findViewById(R.id.tv_sale);
            tvPrice = itemView.findViewById(R.id.tv_dish_price);
            tvCount = itemView.findViewById(R.id.tv_count);
            ivDish = itemView.findViewById(R.id.iv_dish);
            ivAdd = itemView.findViewById(R.id.iv_add);
            ivMinus = itemView.findViewById(R.id.iv_minus);
        }
    }
}