package com.vincent.blogger.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vincent.blogger.R;
import com.vincent.blogger.SpacesItemDecorator;
import com.vincent.blogger.adapters.AccountInterestsAdapter;
import com.vincent.blogger.adapters.ExploreImagesAdapter;
import com.vincent.blogger.adapters.InterestsAdapter;
import com.vincent.blogger.models.Interests;
import com.vincent.blogger.models.User;
import com.vincent.blogger.ui.Setup;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

public class AccountFragment extends Fragment {

    private TextView userNameView;
    private TextView userBioView;
    private TextView dateView;
    private CircleImageView userImgView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Button editProfBtn;

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private String currentUser;

    private Context mContext;
    private List<String> interestsList;
    private AccountInterestsAdapter adapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        interestsList = new ArrayList<>();
        initFirebase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        initViews(view);
        initRecyclerAndAdapter(view);
        setViews();
        editBtnClicked();
        return view;
    }

    private void editBtnClicked(){
        editProfBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editProfBtn.setBackgroundColor(getResources().getColor(R.color.orange1));
                        redToSetup();
                    }
                }
        );
    }

    private void setViews(){
        progressBar.setVisibility(View.VISIBLE);
        firestore
                .collection("Users")
                .document(currentUser)
                .get()
                .addOnSuccessListener(
                        new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot !=null){
                                    User user = documentSnapshot.toObject(User.class);
                                    if(user !=null){

                                        userNameView.setText(user.getUser_name());
                                        userBioView.setText(user.getBio());

                                        long  milliseconds = user.getTimeStamp().getTime();
                                        String date =  DateFormat.format("EEE, MMM d, ''yy",  new Date(milliseconds)).toString();
                                        dateView.setText("Joined " + date);

                                        String image = user.getImage();

                                        RequestOptions reqOpt = new RequestOptions();
                                        reqOpt = reqOpt.placeholder(mContext.getDrawable(R.drawable.add_image_icon));
                                        Glide.with(mContext).setDefaultRequestOptions(reqOpt).load(image).into(userImgView);

                                        for(Map.Entry<String, String> val: user.getChosen_interests().entrySet()){
                                            interestsList.add(val.getKey() + ":" +  val.getValue());
                                        }
                                        adapter.notifyDataSetChanged();

                                    }else{
                                        Log.d(TAG, "User object null");
                                    }
                                }else{
                                    Log.d(TAG, "Docuemnt snapsot null");
                                }
                            }
                        }
                ).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        progressBar.setVisibility(View.INVISIBLE);
    }

    private void initRecyclerAndAdapter(View view){
        recyclerView = view.findViewById(R.id.prof_recycler);
        adapter  = new AccountInterestsAdapter(mContext, interestsList);
        GridLayoutManager gm = new GridLayoutManager(mContext, 3);
        recyclerView.setLayoutManager(gm);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new SpacesItemDecorator(10));

    }

    private void redToSetup(){
        Intent intent = new Intent(getContext(), Setup.class);
        intent.putExtra(Setup.REASON_VISITING_PAGE, "edit");
        startActivity(intent);
    }

    private void initFirebase(){
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getUid();
    }

    private void initViews(View view){
        userNameView = view.findViewById(R.id.prof_user_name);
        userBioView = view.findViewById(R.id.prof_bio);
        dateView = view.findViewById(R.id.prof_date_joined);
        userImgView = view.findViewById(R.id.prof_img);
        progressBar = view.findViewById(R.id.prof_progress_bar);
        editProfBtn = view.findViewById(R.id.edit_prof_btn);
    }
}