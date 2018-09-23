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

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ImageViewHolder> {

    private Context mContext;
    private List<FoodInfo> mfoodinfo;
    private OnItemClickListener mListener;

    public MenuAdapter(Context context, List<FoodInfo> mFoodInfo) {
        mContext = context;
        mfoodinfo = mFoodInfo;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.menu_inflater, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        FoodInfo foodInfo = mfoodinfo.get(position);
        holder.tvfood.setText(foodInfo.getFoodname());
        holder.tvprice.setText(foodInfo.getFoodprice());
        holder.tvdesc.setText(foodInfo.getFooddesc());
        holder.tvstore.setText(foodInfo.getStorename());

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

    public class ImageViewHolder extends RecyclerView.ViewHolder implements
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{
        public TextView tvfood,tvprice,tvdesc, tvstore;
        public ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);

            tvstore = itemView.findViewById(R.id.tvstorename);
            tvfood = itemView.findViewById(R.id.tvfoodname);
            tvprice = itemView.findViewById(R.id.tvfoodprice);
            tvdesc = itemView.findViewById(R.id.tvfooddesc);
            imageView = itemView.findViewById(R.id.imageViewfood);

            itemView.setOnCreateContextMenuListener(this);
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem order = menu.add(Menu.NONE, 1, 1, "Order");

            order.setOnMenuItemClickListener(this);
        }
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {

                    switch (item.getItemId()) {
                        case 1:
                            mListener.onAddClick(position);
                            return true;

                    }
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener {

        void onAddClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}







