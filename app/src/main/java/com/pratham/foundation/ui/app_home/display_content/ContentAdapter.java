package com.pratham.foundation.ui.app_home.display_content;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pratham.foundation.R;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.view_holders.ContentFileViewHolder;
import com.pratham.foundation.view_holders.ContentFolderViewHolder;

import java.util.List;

import static com.pratham.foundation.utility.FC_Constants.TYPE_ITEM;


public class ContentAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<ContentTable> contentViewList;
    ContentClicked contentClicked;

    public ContentAdapter(Context mContext, List<ContentTable> contentViewList, ContentClicked contentClicked) {
        this.mContext = mContext;
        this.contentViewList = contentViewList;
        this.contentClicked = contentClicked;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewtype) {
        View view;
        switch (viewtype) {
            case 1:
                LayoutInflater file = LayoutInflater.from(viewGroup.getContext());
                view = file.inflate(R.layout.content_card, viewGroup, false);
                return new ContentFileViewHolder(view, contentClicked);
            case 2:
                LayoutInflater folder = LayoutInflater.from(viewGroup.getContext());
                view = folder.inflate(R.layout.content_adaprer_item_folder_card, viewGroup, false);
                return new ContentFolderViewHolder(view, contentClicked);
            default:
                return null;
        }
    }

//    public class FileHolder extends RecyclerView.ViewHolder {
//
//        public TextView title;
//        public ImageView ib_action_btn, ib_update_btn;
//        public MaterialCardView content_card_view;
//        SimpleDraweeView thumbnail;
//
//        public FileHolder(View view) {
//            super(view);
//            title = view.findViewById(R.id.content_title);
//            thumbnail = view.findViewById(R.id.content_image);
//            content_card_view = view.findViewById(R.id.content_card_view);
//            ib_action_btn = view.findViewById(R.id.ib_action_btn);
//            ib_update_btn = view.findViewById(R.id.ib_update_btn);
//        }
//    }
//
//    public class FolderHolder extends RecyclerView.ViewHolder {
//
//        TextView tvTitle;
//        TextView tv_progress;
//        SimpleDraweeView itemImage;
//        protected RelativeLayout rl_root;
//        ImageView iv_downld, ib_update_btn;
//        MaterialCardView card_main;
//
//        public FolderHolder(View view) {
//            super(view);
//            tvTitle = view.findViewById(R.id.tvTitle);
//            tv_progress = view.findViewById(R.id.tv_progress);
//            itemImage = view.findViewById(R.id.item_Image);
//            rl_root = view.findViewById(R.id.rl_root);
//            card_main = view.findViewById(R.id.card_main);
//            iv_downld = view.findViewById(R.id.iv_downld);
//            ib_update_btn = view.findViewById(R.id.ib_update_btn);
//        }
//    }

    @Override
    public int getItemViewType(int position) {
        if (contentViewList.get(position).getNodeType() != null) {
            switch (contentViewList.get(position).getNodeType()) {
                case TYPE_ITEM:
                    return 1;
                default:
                    return 2;
            }
        } else
            return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {

        final ContentTable contentList = contentViewList.get(position);
        switch (viewHolder.getItemViewType()) {
            case 1:
                //file
                ContentFileViewHolder contentFileViewHolder = (ContentFileViewHolder) viewHolder;
                contentFileViewHolder.setFileItem(contentList,position);
//                ContentAdapter.FileHolder holder = (ContentAdapter.FileHolder) viewHolder;
//                holder.content_card_view.setBackground(mContext.getResources().getDrawable(getRandomCardColor()));
//                holder.title.setText(contentList.getNodeTitle());
//                holder.title.setSelected(true);
//                RequestOptions requestOptions = new RequestOptions();
//                requestOptions.placeholder(R.drawable.ic_download_2);
//                requestOptions.error(R.drawable.warning);
//
//                holder.ib_action_btn.setVisibility(View.GONE);
//                if (contentList.getIsDownloaded().equalsIgnoreCase("false")) {
//                    holder.ib_action_btn.setVisibility(View.VISIBLE);
//                    holder.ib_action_btn.setImageResource(R.drawable.ic_download_2);//setVisibility(View.VISIBLE);
//                    holder.ib_action_btn.setClickable(false);
//                } else if (contentList.getIsDownloaded().equalsIgnoreCase("true")) {
//                    holder.ib_action_btn.setVisibility(View.VISIBLE);
//                    holder.ib_action_btn.setImageResource(R.drawable.ic_joystick);
//                    holder.ib_action_btn.setClickable(true);
//                    if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.VIDEO))
//                        holder.ib_action_btn.setImageResource(R.drawable.ic_video);
//                    else if (contentList.getResourceType().toLowerCase().contains(FC_Constants.GAME))
//                        holder.ib_action_btn.setImageResource(R.drawable.ic_joystick);
//                    else
//                        holder.ib_action_btn.setImageResource(R.drawable.ic_android_act);
//                }
//
//                File f;
//                if (contentList.getIsDownloaded().equalsIgnoreCase("1") ||
//                        contentList.getIsDownloaded().equalsIgnoreCase("true")) {
//                    if (contentList.isOnSDCard())
//                        f = new File(ApplicationClass.contentSDPath +
//                                "" + App_Thumbs_Path + contentList.getNodeImage());
//                    else
//                        f = new File(ApplicationClass.foundationPath +
//                                "" + App_Thumbs_Path + contentList.getNodeImage());
//                    if (f.exists())
//                        holder.thumbnail.setImageURI(Uri.fromFile(f));
//                } else {
//                    ImageRequest imageRequest = ImageRequestBuilder
//                            .newBuilderWithSource(Uri.parse(contentList.getNodeServerImage()))
//                            .setResizeOptions(new ResizeOptions(250, 170))
//                            .setLocalThumbnailPreviewsEnabled(true)
//                            .build();
//                    DraweeController controller = Fresco.newDraweeControllerBuilder()
//                            .setImageRequest(imageRequest)
//                            .setOldController(holder.thumbnail.getController())
//                            .build();
//                    holder.thumbnail.setController(controller);
//
//                }
//
//                holder.content_card_view.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (contentList.getNodeType() != null) {
//                            if (contentList.getIsDownloaded().equalsIgnoreCase("true")) {
//                                contentClicked.onContentOpenClicked(position, contentList.getNodeId());
//                            } else if (contentList.getIsDownloaded().equalsIgnoreCase("false"))
//                                contentClicked.onContentDownloadClicked(position, contentList.nodeId);
//                        }
//                    }
//                });
//
//                if (contentList.isNodeUpdate())
//                    holder.ib_update_btn.setVisibility(View.VISIBLE);
//                else
//                    holder.ib_update_btn.setVisibility(View.GONE);
//
//                holder.ib_update_btn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        contentClicked.onContentDownloadClicked(position, contentList.nodeId);
//                    }
//                });
////                holder.content_card_view.setVisibility(View.GONE);
//                setAnimations(holder.content_card_view, position);
                break;
            case 2:
                //folder
                ContentFolderViewHolder contentFolderViewHolder = (ContentFolderViewHolder) viewHolder;
                contentFolderViewHolder.setFolderItem(contentList,position);
//                ContentAdapter.FolderHolder folderHolder = (ContentAdapter.FolderHolder) viewHolder;
//                folderHolder.card_main.setBackground(mContext.getResources().getDrawable(getRandomCardColor()));
//                folderHolder.tvTitle.setText(contentList.getNodeTitle());
//                RequestOptions requestOptions2 = new RequestOptions();
//                requestOptions2.placeholder(R.drawable.ic_download_2);
//                requestOptions2.error(R.drawable.warning);
//
//                File file;
//                if (contentList.getIsDownloaded().equalsIgnoreCase("1") ||
//                        contentList.getIsDownloaded().equalsIgnoreCase("true")) {
//                    folderHolder.iv_downld.setVisibility(View.GONE);
//                    if (contentList.isOnSDCard())
//                        file = new File(ApplicationClass.contentSDPath +
//                                "" + App_Thumbs_Path + contentList.getNodeImage());
//                    else
//                        file = new File(ApplicationClass.foundationPath +
//                                "" + App_Thumbs_Path + contentList.getNodeImage());
//                    if (file.exists())
//                        folderHolder.itemImage.setImageURI(Uri.fromFile(file));
//                } else {
//                    ImageRequest imageRequest = ImageRequestBuilder
//                            .newBuilderWithSource(Uri.parse(contentList.getNodeServerImage()))
//                            .setResizeOptions(new ResizeOptions(300, 300))
//                            .setLocalThumbnailPreviewsEnabled(true)
//                            .build();
//                    DraweeController controller = Fresco.newDraweeControllerBuilder()
//                            .setImageRequest(imageRequest)
//                            .setOldController(folderHolder.itemImage.getController())
//                            .build();
//                    folderHolder.itemImage.setController(controller);
//                }
//                if (contentList.getNodeType().equalsIgnoreCase("PreResource")) {
//                    if (contentList.getIsDownloaded().equalsIgnoreCase("true")) {
//                        folderHolder.iv_downld.setVisibility(View.GONE);
//                    } else if (contentList.getIsDownloaded().equalsIgnoreCase("false"))
//                        folderHolder.iv_downld.setVisibility(View.VISIBLE);
//
//                    if (contentList.isNodeUpdate())
//                        folderHolder.ib_update_btn.setVisibility(View.VISIBLE);
//                    else
//                        folderHolder.ib_update_btn.setVisibility(View.GONE);
//
//                    folderHolder.ib_update_btn.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            contentClicked.onContentDownloadClicked(position, contentList.nodeId);
//                        }
//                    });
//
//                } else
//                    folderHolder.iv_downld.setVisibility(View.GONE);
//
//                folderHolder.card_main.setOnClickListener(v -> {
//                    if (contentList.getNodeType() != null) {
//                        if (contentList.getNodeType().equalsIgnoreCase("PreResource")) {
//                            if (contentList.getIsDownloaded().equalsIgnoreCase("true")) {
//                                contentClicked.onPreResOpenClicked(position, contentList.getNodeId(), contentList.getNodeTitle(), contentList.isOnSDCard());
//                            } else if (contentList.getIsDownloaded().equalsIgnoreCase("false"))
//                                contentClicked.onContentDownloadClicked(position, contentList.nodeId);
//                        } else
//                            contentClicked.onContentClicked(position, contentList.nodeId);
//                    }
//                });
//                setAnimations(folderHolder.card_main, position);
                break;
        }
    }

//    private void setAnimations(final View content_card_view, final int position) {
//        final Animation animation;
//        animation = AnimationUtils.loadAnimation(mContext, R.anim.item_fall_down);
//        animation.setDuration(500);
//        content_card_view.setVisibility(View.VISIBLE);
//        content_card_view.setAnimation(animation);
//    }

    @Override
    public int getItemCount() {
        return contentViewList.size();
    }
}