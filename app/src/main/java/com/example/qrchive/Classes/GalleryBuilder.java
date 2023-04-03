package com.example.qrchive.Classes;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qrchive.Fragments.ProfileFragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * GalleryBuilder is a helper class that populates a RecyclerView with a grid of images
 * retrieved from Firebase. It uses the GalleryAdapter to display the images in the grid.
 * Usage:
 *  create a FirebaseWrapper instance and a GalleryBuilder instance
 *  populate the gallery with the images
 *
 *  Link: https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/
 *  Author: Adam Sinicki
 */
public class GalleryBuilder {


    private static FirebaseWrapper fbw;
    /**
     * GalleryBuilder constructor.
     *
     * @param fbw An instance of the FirebaseWrapper class.
     */
    public GalleryBuilder(FirebaseWrapper fbw){
        this.fbw = fbw;
    }

    /**
     * Populates the gallery RecyclerView with images.
     *
     * @param player             The Player object containing information about the user.
     * @param galleryRecyclerView The RecyclerView to populate with the gallery images.
     * @param context            The application context.
     */
    public void populateGallery(Player player, RecyclerView galleryRecyclerView, Context context){

        //set up the RecyclerView for the gallery
        galleryRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context,4);
        galleryRecyclerView.setLayoutManager(gridLayoutManager);

        ArrayList<GalleryListItem> listOfImages = updateImageList(player);

        GalleryAdapter galleryListAdapter = new GalleryAdapter(context, listOfImages);
        galleryRecyclerView.setAdapter(galleryListAdapter);
    }

    /**
     * Updates the list of images to be displayed in the gallery.
     * @param user
     *    The Player object containing information about the user.
     * @return
     *    An ArrayList of GalleryListItem objects representing the images to display.
     */
    public static ArrayList<GalleryListItem> updateImageList(Player user){
        fbw.refreshScannedCodesForUser(fbw.getUserDID(user.getDeviceID()));

        // Testing qrcode gallery remove the following code block
//        List<ScannedCode> qrCodes = new ArrayList<>();
//        ScannedCode sc = new ScannedCode("ranaddshb", null, null, "nYGO0D2cAmW0FO4EsuUe", null);
//        for(int i = 1; i<6;i++){
//            qrCodes.add(sc);
//        }
        List<ScannedCode> qrCodes = fbw.getScannedCodesDict().get(fbw.getUserDID(user.getDeviceID()));
        System.out.println("Qrcodes : " + qrCodes);
        if (qrCodes == null){
            qrCodes = new ArrayList<>();
        }
        System.out.println("this is updateImageList "+ qrCodes);
        ArrayList<GalleryListItem> listOfImages = new ArrayList<>();

        for(ScannedCode qr : qrCodes ){

            GalleryListItem galleryListItem = new GalleryListItem();

            // Set the photo
            // TODO: Fix this, for now it is default photo
            if (qr.getLocationImage() == "placeholder_img") {
                ProfileFragment.setDefaultQr(qr);
                galleryListItem.setImage(ProfileFragment.defaultQr);
            } else {
                ProfileFragment.setDefaultQr(qr);
                galleryListItem.setImage(ProfileFragment.defaultQr); // TODO
            }
            galleryListItem.setHash(qr.getHash());
            listOfImages.add(galleryListItem);
        }
        return listOfImages;
    }



}
