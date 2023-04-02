package com.example.qrchive.Classes;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.qrchive.R;
import com.google.firebase.firestore.auth.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * takes images and binds them to view holders
 * Link: https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/
 * Author: Adam Sinicki
 * This is a class for a custom adapter for the recycler View
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private static ArrayList<GalleryListItem> galleryList;
    private static Context context;
    private Player player;
    private static GalleryAdapter galleryAdapterInstance;

    /**
     * GalleryAdapter constructor.
     * @param context
     *   context is where the adapter is being used
     * @param galleryList
     *  galleryList An ArrayList of GalleryListItem objects representing the images to display.
     */
    public GalleryAdapter(Context context, ArrayList<GalleryListItem> galleryList) {
        this.galleryList = galleryList;
        this.context = context;

    }

    /**
     *sets up the ViewHolder for the recycler View
     * @param viewGroup The parent view group.
     * @param i The position of the item in the gallery.
     * @return ViewHolder with the defined view parameters
     */
    @NonNull
    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_cell_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    /**
     * Binds the data to the ViewHolder.
     * @param viewHolder
     *      The ViewHolder to hold the data
     * @param viewHolder
     *      The ViewHolder containing the image.
     * @param i
     *      The position of the item in the gallery.
     */
    @Override
    public void onBindViewHolder(GalleryAdapter.ViewHolder viewHolder, int i) {
        viewHolder.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        viewHolder.image.setImageBitmap(galleryList.get(i).getImage());
    }

    /**
     * Returns the number of items in the gallery.
     *
     * @return size of the galleryList of type int
     */

    @Override
    public int getItemCount() {
        return galleryList.size();
    }

    /**
     * Removes an image from the gallery.
     *
     * @param codeImage The ScannedCode object representing the image to be removed.
     */
    public void removeImage(ScannedCode codeImage){
        galleryList.remove(codeImage);

    }

    /**
     * This class creates the ViewHolder which makes it easier to iterate through images
     */
    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView image;

        /**
         * ViewHolder constructor.
         *
         * @param view The inflated view containing the image.
         */
        public ViewHolder(View view) {
            super(view);

            image = view.findViewById(R.id.image);

        }
    }

    /**
     * get instance of the GalleryAdapater
     * @return A singleton instance of the GalleryAdapter.
     */
    public static GalleryAdapter getInstance() {
        if (galleryAdapterInstance == null) {
            galleryAdapterInstance = new GalleryAdapter(context,galleryList);
        }
        return galleryAdapterInstance;
    }


}
