package com.vincent.blogger.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vincent.blogger.interfaces.GetBlogCallback;
import com.vincent.blogger.R;
import com.vincent.blogger.interfaces.Redirect;
import com.vincent.blogger.services.LoadPostsServices;
import com.vincent.blogger.adapters.BlogRecyclerAdapter;
import com.vincent.blogger.models.BlogPost;

import java.util.ArrayList;
import java.util.List;

public class GymFragment extends Fragment implements Redirect {

    private List<BlogPost> blogPostList;


    private RecyclerView recyclerView;
    private BlogRecyclerAdapter adapter;
    private Context mContext;

    private Activity mActivity;

    private LoadPostsServices service;

    GymFragment(Activity activity){
        this.mActivity = activity;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gym, container, false);
        initViews(view);
        service = new LoadPostsServices();
        getBlogData();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        blogPostList.clear();
    }

    private void getBlogData() {
        if(recyclerView !=null){
            recyclerView.addOnScrollListener(
                    new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            boolean bottomReached = !recyclerView.canScrollVertically(1);

                            if(bottomReached){
                                service.loadMorePosts("Gym", getActivity(), new GetBlogCallback() {
                                    @Override
                                    public void onCallBack(BlogPost blogPost) {
                                        if(blogPost !=null){
                                            blogPostList.add(blogPost);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }
                        }
                    }
            );

            service.loadFirstPosts("Gym", getActivity(), new GetBlogCallback() {
                @Override
                public void onCallBack(BlogPost blogPost) {
                    if(blogPost !=null){
                        blogPostList.add(blogPost);
                        adapter.notifyDataSetChanged();
                    }
                }
            });

        }

    }

    public void initViews(View mView){
        blogPostList = new ArrayList<>();
        recyclerView = mView.findViewById(R.id.gym_recycler);
        adapter = new BlogRecyclerAdapter(getContext(), blogPostList, this);
        LinearLayoutManager lm= new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(lm);
        recyclerView.setAdapter(adapter);

    }


    @Override
    public void redToExploreBlog(String desc, String coverImage, ArrayList<String> images, String date, String userId) {

    }
}