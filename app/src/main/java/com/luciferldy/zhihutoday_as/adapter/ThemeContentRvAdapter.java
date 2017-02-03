package com.luciferldy.zhihutoday_as.adapter;

import android.net.Uri;
import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.luciferldy.zhihutoday_as.R;
import com.luciferldy.zhihutoday_as.model.ThemeContentListGson;
import com.luciferldy.zhihutoday_as.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucifer on 2017/1/25.
 * 主题日报内容的 Adapter
 */

public class ThemeContentRvAdapter extends RecyclerView.Adapter<ThemeContentRvAdapter.SimpleViewHolder> {

    private ArrayList<ThemeContentListGson.EditorsBean> mEditors;
    private ArrayList<ThemeStoryWrapper> mStories;
    private String mUrl;
    private String mDes;
    public static final int TAG_TITLE = 1;
    public static final int TAG_EDITOR = 2;
    public static final int TAG_NORMAL = 3;
    @IntDef({TAG_TITLE, TAG_EDITOR, TAG_NORMAL})
    public @interface ThemeContentTag {}
    private Callback mCallback;

    public ThemeContentRvAdapter() {
        super();
        mEditors = new ArrayList<>();
        mStories = new ArrayList<>();
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, @ThemeContentTag int viewType) {
        View view;
        SimpleViewHolder holder;
        if (viewType == TAG_TITLE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.theme_item_title, parent, false);
            holder = new TitleViewHolder(view);
        } else if (viewType == TAG_EDITOR) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.theme_item_editors, parent, false);
            holder = new EditorViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.theme_item_normal, parent, false);
            holder = new NormalViewHolder(view);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        holder.onBind(mStories.get(position));
    }

    @Override
    public int getItemCount() {
        return mStories.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mStories.get(position).tag;
    }

    @Deprecated
    public void update(List<ThemeContentListGson.StoriesBean> stories) {
        mStories.clear();
//        mStories.addAll(stories);
        notifyDataSetChanged();
    }

    public void update(ThemeContentListGson themeContent) {
        mUrl = themeContent.getBackground();
        mDes = themeContent.getDescription();
        mEditors.clear();
        mStories.clear();
        mEditors.addAll(themeContent.getEditors());
        ThemeStoryWrapper title = new ThemeStoryWrapper();
        title.tag = TAG_TITLE;
        mStories.add(title);
        ThemeStoryWrapper editor = new ThemeStoryWrapper();
        editor.tag = TAG_EDITOR;
        mStories.add(editor);
        for (ThemeContentListGson.StoriesBean story: themeContent.getStories()) {
            ThemeStoryWrapper wrapper = new ThemeStoryWrapper();
            wrapper.id = story.getId();
            wrapper.type = story.getType();
            wrapper.tag = TAG_NORMAL;
            wrapper.title = story.getTitle();
            wrapper.images = story.getImages();
            mStories.add(wrapper);
        }
        notifyDataSetChanged();
    }

    public void addCallback(Callback callback) {
        this.mCallback = callback;
    }

    class TitleViewHolder extends SimpleViewHolder {

        SimpleDraweeView themeBg;
        TextView themeDes;

        public TitleViewHolder(View itemView) {
            super(itemView);
            themeBg = (SimpleDraweeView) itemView.findViewById(R.id.theme_img);
            themeDes = (TextView) itemView.findViewById(R.id.theme_des);
        }

        @Override
        public void onBind(ThemeStoryWrapper story) {
            super.onBind(story);
            themeBg.setImageURI(mUrl);
            themeDes.setText(mDes);
        }
    }

    class EditorViewHolder extends SimpleViewHolder {

        LinearLayout root;

        public EditorViewHolder(View itemView) {
            super(itemView);
            root = (LinearLayout) itemView;
        }

        @Override
        public void onBind(ThemeStoryWrapper story) {
            super.onBind(story);
            RecyclerView.LayoutParams rootParams = (RecyclerView.LayoutParams) root.getLayoutParams();
            int height = rootParams.height;
            rootParams = null;
            for (final ThemeContentListGson.EditorsBean editor: mEditors) {
                SimpleDraweeView avatar = new SimpleDraweeView(root.getContext());
                root.addView(avatar);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) avatar.getLayoutParams();
                params.height = height;
                params.width = height;
                params.rightMargin = CommonUtils.dip2px(root.getContext(), 12);
                avatar.setLayoutParams(params);
                RoundingParams roundingParams = new RoundingParams();
                roundingParams.setRoundAsCircle(true);
                avatar.getHierarchy().setRoundingParams(roundingParams);
                avatar.setImageURI(Uri.parse(editor.getAvatar()));
            }

            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        mCallback.onClickEditor(mEditors);
                    }
                }
            });

        }
    }

    class NormalViewHolder extends SimpleViewHolder {

        View root;
        SimpleDraweeView img;
        TextView title;

        public NormalViewHolder(View itemView) {
            super(itemView);
            root = itemView;
            img = (SimpleDraweeView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
        }

        @Override
        public void onBind(final ThemeStoryWrapper story) {
            super.onBind(story);
            if (story.images != null && story.images.size() > 0) {
                img.setImageURI(story.images.get(0));
            }
            title.setText(story.title);
            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        mCallback.onClickNormal(story.id);
                    }
                }
            });
        }
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {
        public SimpleViewHolder(View itemView) {
            super(itemView);
        }

        public void onBind(ThemeStoryWrapper story) {

        }
    }

    private class ThemeStoryWrapper {
        int id;
        int type;
        int tag;
        String title;
        List<String> images;
    }

    public interface Callback {
        void onClickNormal(int contentId);
        void onClickEditor(List<ThemeContentListGson.EditorsBean> editors);
    }
}
