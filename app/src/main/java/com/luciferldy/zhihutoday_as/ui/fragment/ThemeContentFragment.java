package com.luciferldy.zhihutoday_as.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.luciferldy.zhihutoday_as.R;
import com.luciferldy.zhihutoday_as.adapter.ThemeContentRvAdapter;
import com.luciferldy.zhihutoday_as.model.ThemeContentListGson;
import com.luciferldy.zhihutoday_as.presenter.ThemeContentFragPresenter;
import com.luciferldy.zhihutoday_as.ui.view.BaseView;
import com.luciferldy.zhihutoday_as.utils.CommonUtils;
import com.luciferldy.zhihutoday_as.utils.FragmentUtils;

import java.util.List;

/**
 * Created by Lucifer on 2017/1/24.
 */

public class ThemeContentFragment extends Fragment implements BaseView, BaseFragment {

    private static final String LOG_TAG = ThemeContentFragment.class.getSimpleName();
    public static final String THEME_ID = "themeId";
    public int themeId;
    private ThemeContentFragPresenter mPresenter;

    private SimpleDraweeView mBackground;
    private TextView mDescription;
    private LinearLayout mEditorGroup;
    private RecyclerView mRv;
    private ThemeContentRvAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_theme_content, container, false);
        mBackground = (SimpleDraweeView) root.findViewById(R.id.theme_img);
        mDescription = (TextView) root.findViewById(R.id.theme_des);
        mEditorGroup = (LinearLayout) root.findViewById(R.id.editor_group);
        mRv = (RecyclerView) root.findViewById(R.id.rv_theme_content);
        mRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ThemeContentRvAdapter();
        mAdapter.addCallback(new ThemeContentRvAdapter.Callback() {
            @Override
            public void onClickNormal(int contentId) {

            }

            @Override
            public void onClickEditor(List<ThemeContentListGson.EditorsBean> editors) {

            }
        });
        mRv.setAdapter(mAdapter);
        initPresenter();
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        themeId = bundle.getInt(THEME_ID, -1);
        mPresenter.getThemeContent(themeId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unsubscribe();
    }

    @Override
    public void onBackPressed() {
        FragmentUtils.popBackStack(getFragmentManager());
    }

    @Override
    public void initPresenter() {
        mPresenter = new ThemeContentFragPresenter(getActivity(), this);
    }

    @Deprecated
    public void updateDescription(String imgUrl, String description) {
        mBackground.setImageURI(imgUrl);
        mDescription.setText(description);
    }

    @Deprecated
    public void updateEditors(List<ThemeContentListGson.EditorsBean> editors) {
        for (ThemeContentListGson.EditorsBean editor: editors) {
            SimpleDraweeView avatar = new SimpleDraweeView(getContext());
            mEditorGroup.addView(avatar);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) avatar.getLayoutParams();
            params.width = LinearLayout.LayoutParams.MATCH_PARENT;
            params.height = LinearLayout.LayoutParams.MATCH_PARENT;
            params.rightMargin = CommonUtils.dip2px(getContext(), 12);
            avatar.setLayoutParams(params);
            avatar.setImageURI(editor.getAvatar());
        }
    }

    @Deprecated
    public void updateStories(List<ThemeContentListGson.StoriesBean> stories) {
        if (stories != null && stories.size() > 0)
            mAdapter.update(stories);
    }

    public void updateData(ThemeContentListGson themeContent) {
        mAdapter.update(themeContent);
    }

    public void getData() {
        if (mPresenter.isLoading())
            mPresenter.unsubscribe();
        mPresenter.getThemeContent(themeId);
    }
}
