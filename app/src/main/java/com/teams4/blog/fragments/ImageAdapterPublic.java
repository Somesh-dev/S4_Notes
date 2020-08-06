package com.teams4.blog.fragments;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.teams4.blog.Databases.Upload;
import com.teams4.blog.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ImageAdapterPublic extends RecyclerView.Adapter <ImageAdapterPublic.ImageViewHolder> {
    private Context mContext;
    private List<Upload> mUploads;
    private OnItemClickListener mListener;

    public ImageAdapterPublic(Context context, List<Upload> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(v);
    }
    //.load(uploadCurrent.getImageUrl())

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Upload uploadCurrent = mUploads.get(position);
        holder.tvName.setText(uploadCurrent.getName());
        Glide.with(mContext)
                .load(uploadCurrent.getImageUrl())
                .placeholder(R.drawable.loading_logo1)
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnCreateContextMenuListener,
            MenuItem.OnMenuItemClickListener {
        //Declaring Views and Variables
        public TextView tvName;
        public ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            imageView = itemView.findViewById(R.id.imv_image);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }


        @Override
        public void onClick(View view) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
//            contextMenu.setHeaderTitle("Select Action");
            MenuItem delete = contextMenu.add(Menu.NONE, 1, 1, "Delete Post");

            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {

                    switch (menuItem.getItemId()) {

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

        void onItemClick(int position);

        void onDeleteClick(int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;

    }

}
