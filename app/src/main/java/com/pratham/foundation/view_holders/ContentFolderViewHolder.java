package com.pratham.foundation.view_holders;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.ui.app_home.FragmentItemClicked;
import com.pratham.foundation.ui.app_home.display_content.ContentClicked;

import java.io.File;
import java.util.Objects;

import static com.pratham.foundation.ApplicationClass.App_Thumbs_Path;
import static com.pratham.foundation.utility.FC_Constants.SINGLE_RES_DOWNLOAD;
import static com.pratham.foundation.utility.FC_Utility.getRandomCardColor;

public class ContentFolderViewHolder extends RecyclerView.ViewHolder {

    @Nullable
    TextView tvTitle;
    @Nullable
    TextView tv_progress;
    @Nullable
    SimpleDraweeView itemImage;
    @Nullable
    RelativeLayout rl_root;
    @Nullable
    ImageView iv_downld;
    @Nullable
    ImageView ib_update_btn;
    @Nullable
    MaterialCardView card_main;
    
    private ContentClicked contentClicked;
    private FragmentItemClicked itemClicked;
    String animType;

    public ContentFolderViewHolder(View itemView, final ContentClicked contentClicked) {
        super(itemView);

        tvTitle = itemView.findViewById(R.id.tvTitle);
        tv_progress = itemView.findViewById(R.id.tv_progress);
        itemImage = itemView.findViewById(R.id.item_Image);
        rl_root = itemView.findViewById(R.id.rl_root);
        card_main = itemView.findViewById(R.id.card_main);
        iv_downld = itemView.findViewById(R.id.iv_downld);
        ib_update_btn = itemView.findViewById(R.id.ib_update_btn);
        this.contentClicked = contentClicked;
    }

    public ContentFolderViewHolder(View itemView, final FragmentItemClicked itemClicked) {
        super(itemView);

        tvTitle = itemView.findViewById(R.id.tvTitle);
        tv_progress = itemView.findViewById(R.id.tv_progress);
        itemImage = itemView.findViewById(R.id.item_Image);
        rl_root = itemView.findViewById(R.id.rl_root);
        card_main = itemView.findViewById(R.id.card_main);
        iv_downld = itemView.findViewById(R.id.iv_downld);
        ib_update_btn = itemView.findViewById(R.id.ib_update_btn);
        this.itemClicked = itemClicked;
    }

    @SuppressLint("CheckResult")
    public void setFolderItem(ContentTable contentList, int position) {
        Objects.requireNonNull(card_main).setBackground(ApplicationClass.getInstance().getResources().getDrawable(getRandomCardColor()));
        Objects.requireNonNull(tvTitle).setText(contentList.getNodeTitle());
        RequestOptions requestOptions2 = new RequestOptions();
        requestOptions2.placeholder(R.drawable.ic_download_2);
        requestOptions2.error(R.drawable.warning);

        File file;
        if (contentList.getIsDownloaded().equalsIgnoreCase("1") ||
                contentList.getIsDownloaded().equalsIgnoreCase("true")) {
            Objects.requireNonNull(iv_downld).setVisibility(View.GONE);
            if (contentList.isOnSDCard())
                file = new File(ApplicationClass.contentSDPath +
                        "" + App_Thumbs_Path + contentList.getNodeImage());
            else
                file = new File(ApplicationClass.foundationPath +
                        "" + App_Thumbs_Path + contentList.getNodeImage());
            if (file.exists())
                Objects.requireNonNull(itemImage).setImageURI(Uri.fromFile(file));
        } else {
            ImageRequest imageRequest = ImageRequestBuilder
                    .newBuilderWithSource(Uri.parse(contentList.getNodeServerImage()))
                    .setResizeOptions(new ResizeOptions(300, 300))
                    .setLocalThumbnailPreviewsEnabled(true)
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(imageRequest)
                    .setOldController(Objects.requireNonNull(itemImage).getController())
                    .build();
            itemImage.setController(controller);
        }
        if (contentList.getNodeType().equalsIgnoreCase("PreResource")) {
            if (contentList.getIsDownloaded().equalsIgnoreCase("true")) {
                Objects.requireNonNull(iv_downld).setVisibility(View.GONE);
            } else if (contentList.getIsDownloaded().equalsIgnoreCase("false"))
                Objects.requireNonNull(iv_downld).setVisibility(View.VISIBLE);

            if (contentList.isNodeUpdate())
                Objects.requireNonNull(ib_update_btn).setVisibility(View.VISIBLE);
            else
                Objects.requireNonNull(ib_update_btn).setVisibility(View.GONE);

            ib_update_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    contentClicked.onContentDownloadClicked(position, contentList.nodeId);
                }
            });

        } else
            Objects.requireNonNull(iv_downld).setVisibility(View.GONE);

        Objects.requireNonNull(card_main).setOnClickListener(v -> {
            if (contentList.getNodeType() != null) {
                if (contentList.getNodeType().equalsIgnoreCase("PreResource")) {
                    if (contentList.getIsDownloaded().equalsIgnoreCase("true")) {
                        contentClicked.onPreResOpenClicked(position, contentList.getNodeId(), contentList.getNodeTitle(), contentList.isOnSDCard());
                    } else if (contentList.getIsDownloaded().equalsIgnoreCase("false"))
                        contentClicked.onContentDownloadClicked(position, contentList.nodeId);
                } else
                    contentClicked.onContentClicked(position, contentList.nodeId);
            }
        });
        setAnimations(card_main);
    }

    private void setAnimations(final View content_card_view) {
        final Animation animation;
        animation = AnimationUtils.loadAnimation(ApplicationClass.getInstance(), R.anim.item_fall_down);
        animation.setDuration(300);
        content_card_view.setVisibility(View.VISIBLE);
        content_card_view.setAnimation(animation);
    }

    private void setSlideAnimations(final View content_card_view) {
        final Animation animation;
        animation = AnimationUtils.loadAnimation(ApplicationClass.getInstance(), R.anim.slide_list);
        animation.setDuration(300);
        content_card_view.setVisibility(View.VISIBLE);
        content_card_view.setAnimation(animation);
    }

    public void setFragmentFolderItem(ContentTable contentTable, int posi, String parentName,int parentPos) {
        card_main.setBackground(ApplicationClass.getInstance().getResources().getDrawable(getRandomCardColor()));
        tvTitle.setText(contentTable.getNodeTitle());
        tv_progress.setText(contentTable.getNodePercentage()+"%");
//                progressLayout.setCurProgress(Integer.parseInt(contentTable.getNodePercentage()));
        File f;
        if (contentTable.getIsDownloaded().equalsIgnoreCase("1") ||
                contentTable.getIsDownloaded().equalsIgnoreCase("true")) {
            if (contentTable.isOnSDCard())
                f = new File(ApplicationClass.contentSDPath +
                        "" + App_Thumbs_Path + contentTable.getNodeImage());
            else
                f = new File(ApplicationClass.foundationPath +
                        "" + App_Thumbs_Path + contentTable.getNodeImage());
            if (f.exists())
                itemImage.setImageURI(Uri.fromFile(f));
        } else {
//                    String myUrl = "http://devpos.prathamopenschool.org/CourseContent/images/posLogo.png";
//                    String myUrl2 = ""+myUrl.lastIndexOf('/');

            ImageRequest imageRequest = ImageRequestBuilder
                    .newBuilderWithSource(Uri.parse(contentTable.getNodeServerImage()))
//                    ImageRequest imageRequest = ImageRequestBuilder
//                            .newBuilderWithSource(Uri.parse(myUrl))
                    .setResizeOptions(new ResizeOptions(300, 300))
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(imageRequest)
                    .setOldController(itemImage.getController())
                    .build();
            itemImage.setController(controller);
        }

        if (contentTable.getNodeType().equalsIgnoreCase("PreResource")) {
            if (contentTable.isNodeUpdate())
                ib_update_btn.setVisibility(View.VISIBLE);
            else
                ib_update_btn.setVisibility(View.GONE);

            ib_update_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClicked.onContentDownloadClicked(contentTable,
                            parentPos,posi,""+ SINGLE_RES_DOWNLOAD);                        }
            });
            if (contentTable.getIsDownloaded().equalsIgnoreCase("true")) {
                iv_downld.setVisibility(View.GONE);
            } else if (contentTable.getIsDownloaded().equalsIgnoreCase("false"))
                iv_downld.setVisibility(View.VISIBLE);
        } else
            iv_downld.setVisibility(View.GONE);

        card_main.setOnClickListener(v -> {
            if (contentTable.getNodeType() != null) {
                if (contentTable.getNodeType().equalsIgnoreCase("PreResource")) {
                    if (contentTable.getIsDownloaded().equalsIgnoreCase("true")) {
                        itemClicked.onPreResOpenClicked(posi, contentTable.getNodeId(), contentTable.getNodeTitle(),contentTable.isOnSDCard());
                    } else if (contentTable.getIsDownloaded().equalsIgnoreCase("false"))
                        itemClicked.onContentDownloadClicked(contentTable,parentPos,posi, SINGLE_RES_DOWNLOAD);
                } else
                    itemClicked.onContentClicked(contentTable, parentName);
            }
        });
        setSlideAnimations(card_main);
    }
}