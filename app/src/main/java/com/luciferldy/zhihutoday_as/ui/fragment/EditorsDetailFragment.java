package com.luciferldy.zhihutoday_as.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luciferldy.zhihutoday_as.R;
import com.luciferldy.zhihutoday_as.utils.FragmentUtils;

/**
 * Created by Lucifer on 2017/1/29.
 */

public class EditorsDetailFragment extends Fragment implements BaseFragment {

    RecyclerView mRv;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_editors_detail, container, false);
        mRv = (RecyclerView) root.findViewById(R.id.rv_editors);
        return root;
    }

    @Override
    public void onBackPressed() {
        FragmentUtils.popBackStack(getFragmentManager());
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        public SimpleViewHolder(View itemView) {
            super(itemView);
        }
    }

    class SimpleAdapter extends RecyclerView.Adapter<SimpleViewHolder> {

        @Override
        public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(SimpleViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }
}
