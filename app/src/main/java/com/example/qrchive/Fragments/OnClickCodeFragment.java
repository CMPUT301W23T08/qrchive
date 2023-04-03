package com.example.qrchive.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qrchive.Classes.Comment;
import com.example.qrchive.Classes.FirebaseWrapper;
import com.example.qrchive.Classes.MyCommentCardRecyclerViewAdapter;
import com.example.qrchive.Classes.Player;
import com.example.qrchive.Classes.ScannedCode;
import com.example.qrchive.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 *
 * @author Shelly
 * @version 1.0
 */
public class OnClickCodeFragment extends Fragment {

    ScannedCode scannedCode;
    FirebaseWrapper fbw;
    ArrayList<Comment> comments = new ArrayList<>();
    MyCommentCardRecyclerViewAdapter commentsAdapter;
    RecyclerView recyclerView;

    ArrayList<String> userIdList;

    /**
     * The constructor for the fragment.
     *
     * @param scannedCode is the code that has been clicked on.
     * @param fbw         is the FirebaseWrapper to allow for queries of the Firebase DB.
     */
    public OnClickCodeFragment(ScannedCode scannedCode, FirebaseWrapper fbw) {
        this.scannedCode = scannedCode;
        this.fbw = fbw;
    }

    /**
     * onCreate is called to do initial creation of a fragment.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * onCreateView is called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Returns the view that was instantiated.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_on_click_code, container, false);
        recyclerView = view.findViewById(R.id.comments_recycler_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        Button playerAlsoCaptured = view.findViewById(R.id.player_also_captured_button);
        playerAlsoCaptured.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchUserFromFirebase(v);
            }
        });
        fetchCommentsFromFirebase(view);

        // Setting the fragment elements
        ConstraintLayout rootLayout = view.findViewById(R.id.fragment_on_item_click_code_constraint_layout_view);
        ((TextView) rootLayout.findViewById(R.id.code_name)).setText(scannedCode.getName());

        String imageFileName = scannedCode.getMonsterResourceName();
        ((ImageView) rootLayout.findViewById(R.id.code_image)).setImageResource(getResources().getIdentifier(imageFileName, "drawable", getActivity().getPackageName()));
        ((TextView) rootLayout.findViewById(R.id.code_location)).setText(scannedCode.getLocationString());
        ((TextView)  rootLayout.findViewById(R.id.code_date)).setText(scannedCode.getDateString());
        ((TextView) rootLayout.findViewById(R.id.code_hash_val)).setText(String.valueOf(scannedCode.getHashVal()));
        ((TextView) rootLayout.findViewById(R.id.code_points)).setText(String.valueOf(scannedCode.getPoints()));


        // recorded photo code (get max 1MB)
        fbw.getStorage().getReference(scannedCode.getScannedCodeDID() + ".jpg")
                .getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                        // Read the orientation metadata using ExifInterface
                        try {
                            ExifInterface exif = new ExifInterface(new ByteArrayInputStream(bytes));
                            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                            // Rotate the bitmap according to the orientation metadata
                            Matrix matrix = new Matrix();
                            switch (orientation) {
                                case ExifInterface.ORIENTATION_ROTATE_90:
                                    matrix.setRotate(90);
                                    break;
                                case ExifInterface.ORIENTATION_ROTATE_180:
                                    matrix.setRotate(180);
                                    break;
                                case ExifInterface.ORIENTATION_ROTATE_270:
                                    matrix.setRotate(270);
                                    break;
                                default:
                                    // Do nothing
                            }
                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ImageView imageView = rootLayout.findViewById(R.id.code_recorded_photo);
                        imageView.setImageBitmap(bitmap);
                    }
                });

        // Adding delete button listener
        ((ImageView) rootLayout.findViewById(R.id.delete_button)).setOnClickListener(new View.OnClickListener() {
            /**
             * onClick is called when an object is clicked on.
             *
             * @param v is the view we are concerned with after being clicked on.
             */
            @Override
            public void onClick(View v) {
                fbw.deleteCode(scannedCode.getScannedCodeDID());
                Toast.makeText(getContext(), "\"" + scannedCode.getName() + "\"" + " has been deleted!", Toast.LENGTH_SHORT).show();
                fbw.refreshScannedCodesForUser(scannedCode.getUserDID());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
                fragmentManager.beginTransaction().replace(R.id.fragment_container, new CodesFragment(fbw))
                        .commit();

            }
        });

        // Adding comment send button listener
        ((ImageView) rootLayout.findViewById(R.id.comment_send_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView commentTextView = ((TextView) rootLayout.findViewById(R.id.code_comment_textbox));
                String content = commentTextView.getText().toString();
                if (content.trim().length() == 0) {
                    commentTextView.setError("Comment cannot be empty");
                }
                if (content.length() >= 128) {
                    commentTextView.setError("Comment too big");
                } else {
                    SharedPreferences preferences = getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);
                    String userName = preferences.getString("userName", "");
                    Map<String, Object> doc = new HashMap<>();
                    doc.put("userName", userName);
                    doc.put("hash", scannedCode.getHash());
                    doc.put("content", content);
                    doc.put("date", (Date) new Date());
                    fbw.db.collection("Comments").add(doc).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            comments = new ArrayList<>();
                            fetchCommentsFromFirebase(view);
                            Toast.makeText(getContext(), "The comment has been published!", Toast.LENGTH_SHORT).show();
                            commentsAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

        return view;
    }

    private void fetchCommentsFromFirebase(View view) {
        // get comments
        fbw.db.collection("Comments").whereEqualTo("hash", scannedCode.getHash())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> documentSnapshotList = task.getResult().getDocuments();
                        for (DocumentSnapshot doc : documentSnapshotList) {
                            comments.add(new Comment(doc.get("userName").toString(),
                                    doc.get("hash").toString(),
                                    doc.get("content").toString(),
                                    doc.getTimestamp("date").toDate()));
                        }

                        commentsAdapter = new MyCommentCardRecyclerViewAdapter(comments);
                        commentsAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(commentsAdapter);
                    }
                });
    }

    private void fetchUserFromFirebase(View view) {
        SharedPreferences preferences = getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String userId = preferences.getString("userDID", "no user id found");
        String deviceID = preferences.getString("deviceID", "no user id found");
        userIdList = new ArrayList<>();
        fbw.db.collection("ScannedCodes").whereEqualTo("hash", scannedCode.getHash())
                .get().addOnCompleteListener(task -> {
                   List<DocumentSnapshot> documentSnapshotList = task.getResult().getDocuments();
                   for (DocumentSnapshot doc: documentSnapshotList) {
                       String userDID = Objects.requireNonNull(doc.get("userDID")).toString();
                       if (!userDID.equals(userId))
                        userIdList.add(userDID);
                   }
                   new SameQrDialogFragment(userIdList, fbw).show(getChildFragmentManager(), "SameQRDialogFragment");
                });
    }
}