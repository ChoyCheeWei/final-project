package com.example.ccw.fyp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ImageViewHolder> {

    private Context mContext;
    private List<FoodInfo> mfoodinfo;
    private OnItemClickListener mListener;

    public FoodAdapter(Context context, List<FoodInfo> mFoodInfo) {
        mContext = context;
        mfoodinfo = mFoodInfo;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.food_inflater, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        FoodInfo foodInfo = mfoodinfo.get(position);
        holder.tvfood.setText(foodInfo.getFoodname());
        holder.tvprice.setText(foodInfo.getFoodprice());
        holder.tvdesc.setText(foodInfo.getFooddesc());

        Picasso.with(mContext)
                .load(foodInfo.getImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mfoodinfo.size();
    }

    public void setFilter(List<FoodInfo> newlist) {

        mfoodinfo = new ArrayList<>();
        mfoodinfo.addAll(newlist);
        notifyDataSetChanged();
    }

    public void addFood(FoodInfo newFoodInfo) {
        mfoodinfo.add(newFoodInfo);
        notifyDataSetChanged();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{
        public TextView tvfood,tvprice,tvdesc;
        public ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);

            tvfood = itemView.findViewById(R.id.tvfoodname);
            tvprice = itemView.findViewById(R.id.tvfoodprice);
            tvdesc = itemView.findViewById(R.id.tvfooddesc);
            imageView = itemView.findViewById(R.id.imageViewfood);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);

        }





        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position);
                }
            }
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem delete = menu.add(Menu.NONE, 1, 1, "Delete");
            MenuItem update = menu.add(Menu.NONE, 2, 2, "Update ");


            update.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {

                    switch (item.getItemId()) {

                        case 1:
                            mListener.onDeleteClick(position);
                            return true;
                        case 2:
                            mListener.UpdateFood(position);
                            return true;

                    }
                }
            }
            return false;
        }

    }





    public interface OnItemClickListener {
        void onItemClick(int position);

        void onDeleteClick(int position);

        void UpdateFood(int position);


    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}







