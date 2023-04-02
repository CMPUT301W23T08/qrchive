package com.example.qrchive.Classes;

import android.graphics.Bitmap;

/**
 * GalleryListItem is a class that represents an individual image item in a gallery.
 * Each GalleryListItem object contains a Bitmap image and a hash (the image ID).
 * This class is intended to be used with the GalleryAdapter and GalleryBuilder classes for populating a RecyclerView with a grid of images.
 * Link: https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/
 * Author: Adam Sinicki
 */
public class GalleryListItem {

    private Bitmap image;
    private String hash;

    /**
     * Returns the Bitmap image associated with this GalleryListItem.
     *
     * @return The Bitmap image.
     */
    public Bitmap getImage(){
        return image;
    }

    /**
     * Sets the Bitmap image for this GalleryListItem.
     *
     * @param bitmapImage The Bitmap image to set.
     */
    public void setImage(Bitmap bitmapImage){
        this.image = bitmapImage;
    }

    /**
     * get the hashCode of the GalleryListItem object
     * @return hashCode
     */
    public String getHash(){
        return this.hash;
    }

    /**
     * sets the hashCode of the object to the param provided
     * @param hash
     *   the hash of the QR of the object
     */
    public void setHash(String hash){
        this.hash = hash;
    }


}
