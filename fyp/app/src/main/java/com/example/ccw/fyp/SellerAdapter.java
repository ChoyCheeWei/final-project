package com.example.ccw.fyp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SellerAdapter extends RecyclerView.Adapter<SellerAdapter.ImageViewHolder> {


    private Context mContext;
    private List<Seller> mseller;
    private OnItemClickListener mListener;

    public SellerAdapter(Context context, List<Seller> mSeller) {
        mContext = context;
        mseller = mSeller;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.seller_inflater, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SellerAdapter.ImageViewHolder holder, int position) {
        Seller seller = mseller.get(position);
        holder.tvemail.setText(seller.getEmail() + "@gmail.com");
        holder.tvname.setText(seller.getUsername());
        holder.tvpass.setText(seller.getPass());
        holder.tvphone.setText(seller.getPhone());

        Picasso.with(mContext)
                .load(seller.getImageUrl())
                .fit()
                .centerInside()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mseller.size();
    }

    public void setFilter(List<Seller> newlist) {

        mseller = new ArrayList<>();
        mseller.addAll(newlist);
        notifyDataSetChanged();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{
        public TextView tvemail,tvname,tvpass, tvphone;
        public CircleImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);

            tvemail = itemView.findViewById(R.id.tvselleremail);
            tvname = itemView.findViewById(R.id.tvsellername);
            tvpass = itemView.findViewById(R.id.tvsellerpass);
            tvphone = itemView.findViewById(R.id.tvsellerphone);
            imageView = itemView.findViewById(R.id.sellerimage);


            itemView.setOnCreateContextMenuListener(this);
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem delete = menu.add(Menu.NONE, 1, 1, "Delete");

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

                    }
                }
            }
            return false;
        }

    }

    public interface OnItemClickListener {

        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(SellerAdapter.OnItemClickListener listener) {
        mListener = listener;
    }
}