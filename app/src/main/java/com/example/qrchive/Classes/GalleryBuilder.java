package com.example.qrchive.Classes;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qrchive.Fragments.ProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        galleryListAdapter.notifyDataSetChanged();
    }

    /**
     * Updates the list of images to be displayed in the gallery.
     * @param user
     *    The Player object containing information about the user.
     * @return
     *    An ArrayList of GalleryListItem objects representing the images to display.
     */
    public static ArrayList<GalleryListItem> updateImageList(Player user){

        String userDID = fbw.getUserDID(user.getDeviceID());
        ArrayList<ScannedCode> scannedCodes = new ArrayList<>();
        ArrayList<GalleryListItem> listOfImages = new ArrayList<>();
        fbw.db.collection("ScannedCodes").whereEqualTo("userDID", userDID).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> docs = task.getResult().getDocuments();
                        for (DocumentSnapshot document : docs) {
                            Map<String, Object> docData = document.getData();
                            ScannedCode scannedCode = new ScannedCode
                                    (docData.get("hash").toString(),
                                            Integer.parseInt(docData.get("hashVal").toString()),
                                            document.getTimestamp("date").toDate(),
                                            (GeoPoint) docData.get("location"),
                                            (boolean) docData.get("hasLocation"),
                                            docData.get("locationImage").toString(),
                                            docData.get("userDID").toString(),
                                            document.getId());
                            scannedCodes.add(scannedCode);
                        }


                        for(ScannedCode qr : scannedCodes){

                            GalleryListItem galleryListItem = new GalleryListItem();

                            // Set the photo
                            ProfileFragment.setDefaultQr(qr);
                            galleryListItem.setImage(ProfileFragment.defaultQr);
                            galleryListItem.setQr(qr);
                            listOfImages.add(galleryListItem);
                        }
                    }
                });

        return listOfImages;
    }



}
