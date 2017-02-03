package com.luciferldy.zhihutoday_as.adapter;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import com.luciferldy.zhihutoday_as.R;
import com.luciferldy.zhihutoday_as.model.ThemeListGson;
import com.luciferldy.zhihutoday_as.utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucifer on 2017/1/20.
 */

public class DrawerRvAdapter extends RecyclerView.Adapter<DrawerRvAdapter.DrawerRvViewHolder> {

    private static final String LOG_TAG = DrawerRvAdapter.class.getSimpleName();

    public static final int TAG_USERINFO = 1;
    public static final int TAG_MAIN = 2;
    public static final int TAG_OTHERS = 3;
    @IntDef({TAG_USERINFO, TAG_MAIN, TAG_OTHERS})
    public @interface RvItemCategory {}

    private ArrayList<ItemCategory> mRvItems = new ArrayList<>();
    private OnClickListener mOnClickListener;

    private View mSelectedRoot;

    @Override
    public int getItemViewType(int position) {
        return mRvItems.get(position).category;
    }

    @Override
    public DrawerRvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DrawerRvViewHolder viewHolder;

        if (viewType == TAG_USERINFO) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_item_userinfo, parent, false);
            viewHolder = new UserInfoViewHolder(view);
        } else if (viewType == TAG_MAIN) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_item_main, parent, false);
            mSelectedRoot = view;
            mSelectedRoot.setBackgroundResource(R.color.drawerItemNormal);
            viewHolder = new MainViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_item_others, parent, false);
            viewHolder = new OthersViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DrawerRvViewHolder holder, int position) {
        holder.onBind(mRvItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mRvItems.size();
    }

    public void update(List<ThemeListGson.OthersBean> beans) {
        this.mRvItems.clear();
        ItemCategory userInfo = new ItemCategory();
        userInfo.category = TAG_USERINFO;
        mRvItems.add(userInfo);
        ItemCategory main = new ItemCategory();
        main.category = TAG_MAIN;
        main.name = "首页";
        mRvItems.add(main);
        for (ThemeListGson.OthersBean bean : beans) {
            ItemCategory themeItem = new ItemCategory();
            themeItem.category = TAG_OTHERS;
            themeItem.name = bean.getName();
            themeItem.id = bean.getId();
            mRvItems.add(themeItem);
            Logger.i(LOG_TAG, "drawer category item " + themeItem.name);
        }
        notifyDataSetChanged();
    }

    public void setOnClickListener(@NonNull OnClickListener listener) {
        this.mOnClickListener = listener;
    }

    class UserInfoViewHolder extends DrawerRvViewHolder {
        public UserInfoViewHolder(View itemView) {
            super(itemView);
        }
    }

    class MainViewHolder extends DrawerRvViewHolder {

        View root;

        public MainViewHolder(View itemView) {
            super(itemView);
            this.root = itemView;
        }

        @Override
        void onBind(final ItemCategory category) {
            super.onBind(category);
            this.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnClickListener != null)
                        mOnClickListener.onClick(TAG_MAIN, category.id, category.name);
                    /**
                     * 修改 item 选中状态的另一种做法是在 onBind 回调函数中根据 mSelection 的数值对 viwholder 的 contentView 着色
                     * 当某个 item 选中时，notifiDatasetChanged
                     */
                    if (root != mSelectedRoot) {
                        mSelectedRoot.setBackgroundResource(R.color.drawerItemNormal);
                        root.setBackgroundResource(R.color.drawerItemSelected);
                        mSelectedRoot = root;
                    }
                }
            });
        }

        @Override
        void onSelected() {
            this.root.setBackgroundResource(R.color.drawerItemSelected);
        }

        @Override
        void unSelected() {
            this.root.setBackgroundResource(R.color.drawerItemNormal);
        }
    }

    class OthersViewHolder extends DrawerRvViewHolder {
        View root;
        TextView title;
        ImageView iv;
        public OthersViewHolder(View itemView) {
            super(itemView);
            root = itemView;
            title = (TextView) root.findViewById(R.id.title);
            iv = (ImageView) root.findViewById(R.id.ic_follow);
        }

        @Override
        void onBind(final ItemCategory category) {
            super.onBind(category);
            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnClickListener != null)
                        mOnClickListener.onClick(TAG_OTHERS, category.id, category.name);
                    if (root != mSelectedRoot) {
                        mSelectedRoot.setBackgroundResource(R.color.drawerItemNormal);
                        root.setBackgroundResource(R.color.drawerItemSelected);
                        mSelectedRoot = root;
                    }
                }
            });
            title.setText(category.name);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v instanceof ImageView) {
                        ImageView iv = (ImageView) v;
                        iv.setImageResource(R.drawable.menu_arrow);
                    }
                }
            });

        }

        @Override
        void onSelected() {
            this.root.setBackgroundResource(R.color.drawerItemSelected);
        }

        @Override
        void unSelected() {
            this.root.setBackgroundResource(R.color.drawerItemNormal);
        }
    }


    class DrawerRvViewHolder extends RecyclerView.ViewHolder {
        public DrawerRvViewHolder(View itemView) {
            super(itemView);
        }

        void onBind(ItemCategory category){}

        void onSelected() {}

        void unSelected() {}
    }

    class ItemCategory {

        public int id;
        public @RvItemCategory int category;
        public String name;
    }

    public interface OnClickListener {
        void onClick(@RvItemCategory int type, int themeId, String title);
    }
}
