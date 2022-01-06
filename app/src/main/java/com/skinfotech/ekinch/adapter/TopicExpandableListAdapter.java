package com.skinfotech.ekinch.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skinfotech.ekinch.R;
import com.skinfotech.ekinch.Utility;
import com.skinfotech.ekinch.models.responses.Category;
import com.skinfotech.ekinch.models.responses.SubCategory;
import com.skinfotech.ekinch.models.responses.SubCategoryDatum;
import com.skinfotech.ekinch.views.fragments.LibraryFragment;
import com.skinfotech.ekinch.views.fragments.TopicFragment;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class TopicExpandableListAdapter extends BaseExpandableListAdapter {

    private final Activity mActivity;
    private final Fragment fragment;
    private Context context;
    private ArrayList<SubCategory> subCategory;

    public TopicExpandableListAdapter(Context context, ArrayList<SubCategory> subCategory, Activity mActivity, Fragment fragment) {
        this.context = context;
        this.subCategory = subCategory;
        this.mActivity = mActivity;
        this.fragment = fragment;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.subCategory.get(listPosition)
                .getSubCategoryData().get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final SubCategoryDatum expandedListText = subCategory.get(groupPosition).getSubCategoryData().get(childPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.topic_item, null);
        }
        TextView topicTextView = convertView.findViewById(R.id.textView);
        LinearLayout likeLL = convertView.findViewById(R.id.likeLL);
        TextView likeTV = convertView.findViewById(R.id.likeTV);
        ImageView topicImageView = convertView.findViewById(R.id.imageView2);

        View topicContainer = convertView.findViewById(R.id.topicContainer);


        if (!Utility.isEmpty(expandedListText.getCoursesImage())) {
            Picasso.get().load(expandedListText.getCoursesImage().replace("video", "course thumbnail")).placeholder(R.drawable.video_bg).into(topicImageView);
        }
        topicTextView.setText(expandedListText.getTitle());
        likeTV.setText(expandedListText.getTotalLikes() + "");

        topicImageView.setClipToOutline(true);

        topicContainer.setTag(expandedListText);
        topicContainer.setOnClickListener(v -> {
            SubCategoryDatum selectedCourse = (SubCategoryDatum) v.getTag();
            if (fragment instanceof TopicFragment)
                ((TopicFragment) fragment).fetchQuestionsServerCall(selectedCourse.getCoursesId(), selectedCourse.getTitle());
        });
        likeLL.setTag(expandedListText);

        likeLL.setOnClickListener(view -> {
            SubCategoryDatum selectedCourse = (SubCategoryDatum) view.getTag();

            if (fragment instanceof TopicFragment)
                ((TopicFragment) fragment).likeApi(selectedCourse.getCoursesId(), groupPosition, childPosition);
        });


        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        if (this.subCategory.get(listPosition).getSubCategoryData() != null)
            return this.subCategory.get(listPosition).getSubCategoryData().size();
        else
            return 0;
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.subCategory.get(listPosition).getSubCategoryName();
    }

    @Override
    public int getGroupCount() {
        return this.subCategory.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = subCategory.get(listPosition).getSubCategoryName();
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.listTitle);
        listTitleTextView.setText(listTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}