package com.xavierlepretre.hackernewsbrowser;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewAnimator;
import butterknife.InjectView;
import butterknife.OnClick;
import com.ycombinator.news.dto.CommentDTO;
import com.ycombinator.news.dto.ItemId;
import java.text.NumberFormat;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class CommentView extends ItemView
    implements KidItemView
{
    private static final int COLLAPSED_VIEW_CHILD_NO_COMMENT = 0;
    private static final int COLLAPSED_VIEW_CHILD_COLLAPSED = 1;
    private static final int COLLAPSED_VIEW_CHILD_EXPANDED = 2;

    @InjectView(android.R.id.content) TextView text;
    @InjectView(android.R.id.custom) TextView commentCount;
    @InjectView(R.id.collapsed) ViewAnimator collapsed;

    @NonNull private final BehaviorSubject<ItemId> backToParentSubject;

    public CommentView(Context context)
    {
        super(context);
        backToParentSubject = BehaviorSubject.create();
    }

    public CommentView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        backToParentSubject = BehaviorSubject.create();
    }

    public CommentView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        backToParentSubject = BehaviorSubject.create();
    }

    @NonNull @Override public Observable<ItemId> getBackToParentObservable()
    {
        return backToParentSubject.asObservable();
    }

    public void displayComment(@NonNull DTO commentViewDTO)
    {
        super.displayItem(commentViewDTO);
        if (text != null)
        {
            text.setText(commentViewDTO.text);
        }

        if (commentCount != null)
        {
            commentCount.setText(commentViewDTO.commentCount);
        }

        setPadding(getPaddingRight() + commentViewDTO.paddingLeft, getPaddingTop(), getPaddingRight(), getPaddingBottom());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            setAlpha(commentViewDTO.itemDTO.isDeleted() ? 0.5f : 1);
        }

        collapsed.setDisplayedChild(commentViewDTO.collapsedChildId);
    }

    @SuppressWarnings("UnusedDeclaration, UnusedParameters")
    @OnClick(android.R.id.button2)
    void backToParentClicked(View view)
    {
        ItemView.DTO viewDTO = getItem();
        if (viewDTO != null)
        {
            backToParentSubject.onNext(((CommentDTO) viewDTO.itemDTO).getParent());
        }
    }

    public static class DTO extends ItemView.DTO
        implements Collapsible
    {
        @NonNull final Spanned text;
        final int count;
        @NonNull final String commentCount;
        final int paddingLeft;
        final int zeroBasedDepth;
        private boolean collapsed;
        private int collapsedChildId;

        public DTO(@NonNull Context context, @NonNull CommentDTO commentDTO, int zeroBasedDepth, boolean collapsed)
        {
            super(context, commentDTO);
            this.text = Html.fromHtml(trim(commentDTO.getText()));
            this.count = commentDTO.getKids().size();
            this.commentCount = NumberFormat.getInstance().format(count);
            this.zeroBasedDepth = zeroBasedDepth;
            this.paddingLeft = Math.min(
                    ItemViewDTOUtil.getDisplayWidthPixel(context),
                    context.getResources().getDimensionPixelSize(R.dimen.comment_indentation)
                            * Math.min(
                            context.getResources().getInteger(R.integer.comment_indentation_max),
                            zeroBasedDepth));
            this.setCollapsed(collapsed);
        }

        int getZeroBasedDepth()
        {
            return zeroBasedDepth;
        }

        @Override public boolean isCollapsed()
        {
            return collapsed;
        }

        @Override public void toggleCollapsed()
        {
            setCollapsed(!collapsed);
        }

        @Override public void setCollapsed(boolean collapsed)
        {
            this.collapsed = collapsed;
            this.collapsedChildId = getCollapsedChildId(count, collapsed);
        }

        @NonNull static String trim(@NonNull String padded)
        {
            String trimmed = padded;
            int length = padded.length();
            if (length > 2)
            {
                do
                {
                    trimmed = trimmed.trim();
                    length = trimmed.length();

                    if (trimmed.charAt(length - 1) == '\n'
                            || trimmed.charAt(length - 1) == '\t'
                            || trimmed.charAt(length - 1) == '\r')
                    {
                        trimmed = trimmed.substring(0, trimmed.length() - 2);
                    }

                    if (trimmed.charAt(0) == '\n'
                            || trimmed.charAt(0) == '\t'
                            || trimmed.charAt(0) == '\r')
                    {
                        trimmed = trimmed.substring(1, trimmed.length() - 1);
                    }

                    trimmed = trimmed.trim();
                }
                while (trimmed.length() < length && trimmed.length() > 2);
            }
            return trimmed;
        }

        static int getCollapsedChildId(int count, boolean collapsed)
        {
            return count == 0
                    ? COLLAPSED_VIEW_CHILD_NO_COMMENT
                    : collapsed
                            ? COLLAPSED_VIEW_CHILD_COLLAPSED
                            : COLLAPSED_VIEW_CHILD_EXPANDED;
        }
    }
}
