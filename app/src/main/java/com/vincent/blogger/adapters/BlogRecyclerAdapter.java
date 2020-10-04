package com.vincent.blogger.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.vincent.blogger.interfaces.BlogPostInterface;
import com.vincent.blogger.interfaces.SetBlogLikesInterface;
import com.vincent.blogger.services.PostsLikesService;
import com.vincent.blogger.R;
import com.vincent.blogger.interfaces.Redirect;
import com.vincent.blogger.models.BlogPost;
import com.vincent.blogger.services.SetLikesService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    public List<BlogPost> blogPostList;

    private Context mContext;

    String current_user;
    private String userName;
    private String userImage;
    private TextView topicView;
    private ImageView blogImageView;
    private CircleImageView profPicView;
    private TextView categoryView;
    private TextView userNameView;
    private TextView blogDate;
    private MaterialCardView blogCard;
    private Redirect redirect;
    private PostsLikesService postsLikesService;
    private SetLikesService setLikesService;
    private String date;

    public BlogRecyclerAdapter(Context context, List<BlogPost> blogPostList, Redirect redirect){
        this.blogPostList = blogPostList;
        this.redirect = redirect;
        this.mContext = context;
    }

    @NonNull
    @Override
    public BlogRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.blog_recycler, parent, false);
        postsLikesService = new PostsLikesService(mContext);
        setLikesService = new SetLikesService(mContext);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BlogRecyclerAdapter.ViewHolder holder, int position) {
        initFirebase();
        holder.setIsRecyclable(false);
        final String topic = blogPostList.get(position).getDescription();
        String category = blogPostList.get(position).getCategory();
        String image = blogPostList.get(position).getCover_image();
        final String blogPostId = blogPostList.get(position).BlogPostId;

        if(blogPostList.get(position).getTimeStamp() != null){
            long milliseconds = blogPostList.get(position).getTimeStamp().getTime();
            date = DateFormat.format("MM/dd/yyyy",  new Date(milliseconds)).toString();
        }else{
            date = "";
        }

        final String user_id = blogPostList.get(position).getUser_id();
        ArrayList<String> images = blogPostList.get(position).getImage();


        postsLikesService.getLikesData(topic, user_id, blogPostId,
                new BlogPostInterface() {
                    @Override
                    public void getUserData(Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            userName = task.getResult().getString("user_name");
                            userImage = task.getResult().getString("image");

                            holder.setProfileUser(userName, userImage);

                        }else{
                            Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void getLikes(DocumentSnapshot doc_snap) {
                        if(doc_snap.exists()){
                            holder.favoriteImgView.setImageResource(R.drawable.like_fav_icon);
                        }else{
                            holder.favoriteImgView.setImageResource(R.drawable.unlike_fav_icon);
                        }
                    }

                    @Override
                    public void getLikesCount(QuerySnapshot querySnapshot) {
                        if(!querySnapshot.isEmpty()){
                            int count = querySnapshot.size();
                            holder.updateLikesCount(count);
                        }else{
                            holder.updateLikesCount(0);
                        }
                    }

                }
        );


        holder.favoriteImgView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setLikesService.setLikesCollection(topic, blogPostId, new SetBlogLikesInterface() {
                            @Override
                            public void setLikes(Void aVoid) {
                                holder.favoriteImgView.setImageResource(R.drawable.like_fav_icon);
                            }

                            @Override
                            public void deleteLike(Void aVoid) {
                                holder.favoriteImgView.setImageResource(R.drawable.unlike_fav_icon);
                            }
                        });
                    }
                }
        );

        holder.setTopic(topic);
        holder.setCategory(category);
        holder.setBlogImage(image);
        holder.setDateView(date);
        holder.cardClicked(topic, image,  images, date, user_id);
    }

    @Override
    public int getItemCount() {
        return blogPostList.size();
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
        private ImageView favoriteImgView;
        private TextView likesCountView;

        public ViewHolder(View view){
            super(view);
            mView = view;
            initViews(view);



        }

        private void updateLikesCount(int count){
            likesCountView.setText(count + " likes");
        }

        public void cardClicked(final String desc, final String coverImage, final ArrayList<String> images, final String date, final String userId){
            blogCard.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            redirect.redToExploreBlog(desc, coverImage, images, date, userId);
                        }
                    }
            );
        }

        public void setTopic(String topic){
            topicView.setText(topic);
        }

        public void setCategory(String category){
            categoryView.setText(category);
        }

        public void setDateView(String date){
            blogDate.setText(date);
        }

        public void setBlogImage(String image){
            RequestOptions reqOpt = new RequestOptions();
            reqOpt = reqOpt.placeholder(mContext.getDrawable(R.drawable.add_image_icon));
            Glide.with(mContext).setDefaultRequestOptions(reqOpt).load(image).into(blogImageView);
        }

        public void setProfileUser(String userName, String image){
            profPicView = mView.findViewById(R.id.user_profile_img);
            userNameView = mView.findViewById(R.id.blog_username);
            userNameView.setText(userName);
            RequestOptions reqOpt = new RequestOptions();
            reqOpt = reqOpt.placeholder(mContext.getDrawable(R.drawable.add_image_icon));
            Glide.with(mContext).setDefaultRequestOptions(reqOpt).load(image).into(profPicView);

        }

        public void initViews(View view){
            topicView = view.findViewById(R.id.blog_topic);
            categoryView = view.findViewById(R.id.blog_category);
            blogImageView = view.findViewById(R.id.blog_image);
            favoriteImgView = view.findViewById(R.id.favorite);
            blogDate = view.findViewById(R.id.blog_date);
            blogCard  = view.findViewById(R.id.blog_card);
            likesCountView = view.findViewById(R.id.likes_view);
        }

    }
    public void initFirebase(){
        current_user = FirebaseAuth.getInstance().getUid();
    }
}
