package com.vincent.blogger.ui.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vincent.blogger.adapters.NotificationsAdapter;
import com.vincent.blogger.R;
import com.vincent.blogger.interfaces.NotificationsInterface;
import com.vincent.blogger.interfaces.UsersNotificationInterface;
import com.vincent.blogger.models.Notification;
import com.vincent.blogger.models.User;
import com.vincent.blogger.services.NotificationsService;
import com.vincent.blogger.services.UsersNotificationService;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {
    public List<Notification> notificationlist;

    private RecyclerView notificationsRecycler;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private String currentUserId;
    private ProgressBar progressBar;

    private NotificationsAdapter adapter;
    private NotificationsService notificationService;

    private Context mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationService = new NotificationsService(mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        notificationlist = new ArrayList<>();
        //instantiate firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        currentUserId = auth.getUid();
        progressBar = view.findViewById(R.id.notification_progressBar);

        initRecyclerAndAdapter(view);

        getUserData();

        // Inflate the layout for this fragment
        return view;
    }


    private void getUserData(){
        progressBar.setVisibility(View.VISIBLE);

        notificationService.getNotificationsInfo(new NotificationsInterface() {
            @Override
            public void getNotifications(Notification notification) {
                notificationlist.add(notification);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void getCountOfLikes(int count) {

            }
        });

        progressBar.setVisibility(View.INVISIBLE);

    }


    private void initRecyclerAndAdapter(View view){
        notificationsRecycler =  view.findViewById(R.id.notification_recycler);
        adapter = new NotificationsAdapter(mContext ,notificationlist);
        LinearLayoutManager lm = new LinearLayoutManager(mContext);
        notificationsRecycler.setLayoutManager(lm);
        notificationsRecycler.setAdapter(adapter);
    }
}