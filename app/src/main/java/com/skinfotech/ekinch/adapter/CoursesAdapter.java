package com.skinfotech.ekinch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.skinfotech.ekinch.R;
import com.skinfotech.ekinch.Utility;
import com.skinfotech.ekinch.models.responses.SubCategoryDatum;
import com.skinfotech.ekinch.views.fragments.BaseFragment;
import com.skinfotech.ekinch.views.fragments.LibraryFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.TopicViewHolder> {

    private final Context context;
    private final Fragment fragment;
    private final int groupPosition;
    private final int childPosition;
    private ArrayList<SubCategoryDatum> coursesList = new ArrayList<>();

    public CoursesAdapter(Context context, ArrayList<SubCategoryDatum> coursesList, Fragment fragment, int groupPosition, int childPosition) {
        this.coursesList = coursesList;
        this.context = context;
        this.fragment = fragment;
        this.groupPosition = groupPosition;
        this.childPosition = childPosition;
    }

    @NonNull
    @Override
    public CoursesAdapter.TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.topic_item, parent, false);
        return new CoursesAdapter.TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CoursesAdapter.TopicViewHolder holder, int position) {
        SubCategoryDatum item = coursesList.get(position);
        if (!Utility.isEmpty(item.getCoursesImage())) {
            Picasso.get().load(item.getCoursesImage().replace("video", "course thumbnail")).placeholder(R.drawable.video_bg).into(holder.topicImageView);
        }
        holder.topicTextView.setText(item.getTitle());
        holder.likeTV.setText(item.getTotalLikes() + "");

        holder.topicImageView.setClipToOutline(true);
    }

    @Override
    public int getItemCount() {
        return coursesList.size();
    }

    public class TopicViewHolder extends RecyclerView.ViewHolder {

        private final ImageView topicImageView;
        private final TextView topicTextView, likeTV;
        private LinearLayout likeLL;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            topicImageView = itemView.findViewById(R.id.imageView2);
            topicTextView = itemView.findViewById(R.id.textView);
            likeLL = itemView.findViewById(R.id.likeLL);
            likeTV = itemView.findViewById(R.id.likeTV);

            View topicContainer = itemView.findViewById(R.id.topicContainer);
            topicContainer.setOnClickListener(v -> {
                SubCategoryDatum selectedCourse = coursesList.get(getAdapterPosition());
                if (fragment instanceof LibraryFragment)
                    ((LibraryFragment) fragment).fetchQuestionsServerCall(selectedCourse.getCoursesId(), selectedCourse.getTitle());
            });

            likeLL.setOnClickListener(view -> {
                SubCategoryDatum selectedCourse = coursesList.get(getAdapterPosition());

                if (fragment instanceof LibraryFragment)
                    ((LibraryFragment) fragment).likeApi(selectedCourse.getCoursesId(), getAdapterPosition(), groupPosition, childPosition);
            });
        }
    }


}
