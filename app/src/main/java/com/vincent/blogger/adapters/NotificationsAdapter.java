package com.vincent.blogger.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.vincent.blogger.R;
import com.vincent.blogger.interfaces.UsersNotificationInterface;
import com.vincent.blogger.models.Notification;
import com.vincent.blogger.models.User;
import com.vincent.blogger.services.UsersNotificationService;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    public List<Notification> notificationList;
    private Context context;

    private TextView notificationTitleView;
    private CircleImageView userProfileView;
    private TextView dateLikedView;
    private TextView userNameView;

    private UsersNotificationService notificationService;
    private  Context mContext;

    public NotificationsAdapter(Context context, List<Notification> notificationList){
        this.mContext = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.notifications_recycler, parent, false);
        notificationService = new UsersNotificationService(mContext);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotificationsAdapter.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        final String description = notificationList.get(position).getDescription_of_post();
        long milliseconds = notificationList.get(position).getTimeStamp().getTime();
        final String date = DateFormat.format("MM/dd/yyyy",  new Date(milliseconds)).toString();
        String userLikesPostId = notificationList.get(position).getLiked_user_id();
        holder.setNotificationInfo(description, date);

        notificationService.getUserData(userLikesPostId, new UsersNotificationInterface() {
            @Override
            public void getUser(User user) {
                if(user !=null){
                    String userName = user.getUser_name();
                    String userImage = user.getImage();
                    holder.setUserInfo(userImage, userName);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private ViewHolder(View view){
            super(view);
            mView = view;
            initViews(view);
        }

        public void setUserInfo(String image, String name){
            userNameView.setText(name + " liked your post");
            RequestOptions requestOptions = new RequestOptions();
            Glide.with(mContext).setDefaultRequestOptions(requestOptions).load(image).into(userProfileView);
        }

        public void setNotificationInfo(String desc, String date){
            notificationTitleView.setText(desc);
            dateLikedView.setText(date);


        }

        private void initViews(View view){
            notificationTitleView = view.findViewById(R.id.notification_title);
            userProfileView = view.findViewById(R.id.user_liked_profile_img);
            userNameView = view.findViewById(R.id.user_liked_username);
            dateLikedView = view.findViewById(R.id.date_liked);
        }

    }
}
