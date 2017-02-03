package com.luciferldy.zhihutoday_as.ui.fragment;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.luciferldy.zhihutoday_as.R;
import com.luciferldy.zhihutoday_as.model.ThemeContentListGson;
import com.luciferldy.zhihutoday_as.utils.CommonUtils;
import com.luciferldy.zhihutoday_as.utils.FragmentUtils;
import com.luciferldy.zhihutoday_as.utils.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucifer on 2017/1/29.
 */

public class EditorsDetailFragment extends Fragment implements BaseFragment {

    private static final String LOG_TAG = EditorsDetailFragment.class.getSimpleName();

    private RecyclerView mRv;
    private Toolbar mToolbar;
    private List<ThemeContentListGson.EditorsBean> mEditors = new ArrayList<>();
    private SimpleAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_editors_detail, container, false);
        mRv = (RecyclerView) root.findViewById(R.id.rv_editors);
        mRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new SimpleAdapter();
        mRv.setAdapter(mAdapter);
        root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View statusBar = root.findViewById(R.id.virtual_status_bar);
            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) (statusBar.getLayoutParams());
            params.height = CommonUtils.getStatusBarHeight(getContext());
            statusBar.setLayoutParams(params);
            statusBar.setVisibility(View.VISIBLE);
        }
        mToolbar = (Toolbar) root.findViewById(R.id.toolbar);
        mToolbar.setTitle("主编");
        if (mToolbar.getChildAt(0) instanceof TextView) {
            ((AppCompatTextView) mToolbar.getChildAt(0)).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        }
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        try {
            Class<? extends Toolbar> cls = mToolbar.getClass();
            Field privateTvFiled = cls.getDeclaredField("mTitleTextView");
            if (privateTvFiled != null) {
                // 使用 private 类型的变量，就需要抑制 java 对权限的检查
                privateTvFiled.setAccessible(true);
                TextView tv = (TextView) privateTvFiled.get(mToolbar);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                Logger.i(LOG_TAG, "use reflection to change the title size.");
            }
        } catch (ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.i(LOG_TAG, "use reflection occur exception.");
        }
    }

    @Override
    public void onBackPressed() {
        FragmentUtils.popBackStack(getFragmentManager());
    }

    public void setEditors(List<ThemeContentListGson.EditorsBean> editors) {
        this.mEditors.clear();
        this.mEditors.addAll(editors);
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView avatar;
        TextView name;
        TextView des;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            avatar = (SimpleDraweeView) itemView.findViewById(R.id.avatar);
            name = (TextView) itemView.findViewById(R.id.name);
            des = (TextView) itemView.findViewById(R.id.bio);
        }
    }

    class SimpleAdapter extends RecyclerView.Adapter<SimpleViewHolder> {

        @Override
        public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.editor_item, parent, false);
            SimpleViewHolder holder = new SimpleViewHolder(root);
            return holder;
        }

        @Override
        public void onBindViewHolder(SimpleViewHolder holder, final int position) {
            holder.avatar.setImageURI(Uri.parse(mEditors.get(position).getAvatar()));
            holder.name.setText(mEditors.get(position).getName());
            holder.des.setText(mEditors.get(position).getBio());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Logger.i(LOG_TAG, "onClick id = " + mEditors.get(position).getId() + ", url = " + mEditors.get(position).getUrl());
                }
            });
        }

        @Override
        public int getItemCount() {
            return mEditors.size();
        }
    }
}
