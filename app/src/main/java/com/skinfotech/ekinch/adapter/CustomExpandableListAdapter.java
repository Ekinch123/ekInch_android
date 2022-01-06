package com.skinfotech.ekinch.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skinfotech.ekinch.R;
import com.skinfotech.ekinch.models.responses.Category;

import java.util.ArrayList;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private final Activity mActivity;
    private final Fragment fragment;
    private Context context;
    private ArrayList<Category> category;

    public CustomExpandableListAdapter(Context context, ArrayList<Category> expandableListTitle, Activity mActivity, Fragment fragment) {
        this.context = context;
        this.category = expandableListTitle;
        this.mActivity = mActivity;
        this.fragment = fragment;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.category.get(listPosition)
                .getSubCategories().get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String expandedListText = category.get(groupPosition).getSubCategories().get(childPosition).getSubCategoryName();
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item_child_catagory_liberary, null);
        }
        TextView expandedListTextView = (TextView) convertView.findViewById(R.id.expandedListItem);
        expandedListTextView.setText(expandedListText);

        RecyclerView listItemsRV = (RecyclerView) convertView.findViewById(R.id.ListItemsRV);
        if (category.get(groupPosition).getSubCategories().get(childPosition).getSubCategoryData() != null
                && category.get(groupPosition).getSubCategories().get(childPosition).getSubCategoryData().size() > 0) {
            listItemsRV.setVisibility(View.VISIBLE);

            CoursesAdapter mLibraryAdapter = new CoursesAdapter(context, category.get(groupPosition).getSubCategories().get(childPosition).getSubCategoryData(), fragment,groupPosition,childPosition);
            listItemsRV.setLayoutManager(new LinearLayoutManager(context) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            });
            listItemsRV.setAdapter(mLibraryAdapter);
        } else {
            listItemsRV.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.category.get(listPosition).getSubCategories().size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.category.get(listPosition).getCategoryName();
    }

    @Override
    public int getGroupCount() {
        return this.category.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = category.get(listPosition).getCategoryName();
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group_lib, null);
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