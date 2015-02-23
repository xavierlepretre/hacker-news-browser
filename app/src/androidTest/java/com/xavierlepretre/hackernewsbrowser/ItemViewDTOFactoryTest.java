package com.xavierlepretre.hackernewsbrowser;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import com.ycombinator.news.dto.CollapsedState;
import com.ycombinator.news.dto.ItemDTO;
import com.ycombinator.news.dto.ItemId;
import com.ycombinator.news.dto.OpenCommentDTO;
import com.ycombinator.news.dto.OpenItemDTO;
import com.ycombinator.news.dto.OpenJobDTO;
import com.ycombinator.news.dto.OpenStoryDTO;
import com.ycombinator.news.dto.UserId;
import com.ycombinator.news.service.OpenLoadingItemFinishedDTO;
import com.ycombinator.news.service.OpenLoadingItemStartedDTO;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class ItemViewDTOFactoryTest extends AndroidTestCase
{
    @SmallTest
    public void testCreatesLoadingViewDto()
    {
        ItemViewDTO itemViewDTO = ItemViewDTOFactory.create(
                getContext(),
                new OpenLoadingItemStartedDTO(
                        new ItemId(1)));
        assertThat(itemViewDTO).isExactlyInstanceOf(LoadingItemView.DTO.class);
        assertThat(((LoadingItemView.DTO) itemViewDTO).loading).isTrue();
    }

    @SmallTest
    public void testCreatesStory()
    {
        assertThat(
                ItemViewDTOFactory.create(
                        getContext(),
                        new OpenStoryDTO(
                                new ItemId(1),
                                new UserId("a"),
                                new Date(),
                                false,
                                "title",
                                "http://urlshouldbe.ok/here",
                                32,
                                null))).isExactlyInstanceOf(StoryView.DTO.class);
    }

    @SmallTest
    public void testCreatesJob()
    {
        assertThat(
                ItemViewDTOFactory.create(
                        getContext(),
                        new OpenLoadingItemFinishedDTO(
                                new OpenJobDTO(
                                        new ItemId(1),
                                        new UserId("a"),
                                        new Date(),
                                        false,
                                        "title",
                                        "url",
                                        32,
                                        "text")))).isExactlyInstanceOf(JobView.DTO.class);
    }

    @SmallTest
    public void testCreatesComment()
    {
        assertThat(
                ItemViewDTOFactory.create(
                        getContext(),
                        new OpenLoadingItemFinishedDTO(
                                new OpenCommentDTO(
                                        new ItemId(2),
                                        new UserId("a"),
                                        new Date(),
                                        false,
                                        new ItemId(1),
                                        "text",
                                        null)))).isExactlyInstanceOf(CommentView.DTO.class);
    }

    @SmallTest
    public void testCreatesCommentWithCollapsibleTrue()
    {
        ItemViewDTO viewDTO = ItemViewDTOFactory.create(
                getContext(),
                new OpenLoadingItemFinishedDTO(
                        new OpenCommentDTO(
                                new ItemId(2),
                                new UserId("a"),
                                new Date(),
                                false,
                                new ItemId(1),
                                "text",
                                null)),
                new CollapsedState(true));
        assertThat(viewDTO).isExactlyInstanceOf(CommentView.DTO.class);
        CommentView.DTO commentViewDTO = (CommentView.DTO) viewDTO;
        assertThat(commentViewDTO.isCollapsed()).isTrue();
    }

    @SmallTest
    public void testCreatesCommentWithCollapsibleFalse()
    {
        ItemViewDTO viewDTO = ItemViewDTOFactory.create(
                getContext(),
                new OpenLoadingItemFinishedDTO(
                        new OpenCommentDTO(
                                new ItemId(2),
                                new UserId("a"),
                                new Date(),
                                false,
                                new ItemId(1),
                                "text",
                                null)),
                new CollapsedState(false));
        assertThat(viewDTO).isExactlyInstanceOf(CommentView.DTO.class);
        CommentView.DTO commentViewDTO = (CommentView.DTO) viewDTO;
        assertThat(commentViewDTO.isCollapsed()).isFalse();
    }

    @SmallTest
    public void testCreatesCommentWithCollapsibleDepthIsCollapsed()
    {
        ItemViewDTO viewDTO = ItemViewDTOFactory.create(
                getContext(),
                new OpenCommentDTO(
                        new ItemId(2),
                        new UserId("a"),
                        new Date(),
                        false,
                        new ItemId(1),
                        "text",
                        null),
                new CollapsedState(1));
        assertThat(viewDTO).isExactlyInstanceOf(CommentView.DTO.class);
        CommentView.DTO commentViewDTO = (CommentView.DTO) viewDTO;
        assertThat(commentViewDTO.isCollapsed()).isTrue();
    }

    @SmallTest
    public void testCreatesCommentWithDepth()
    {
        ItemViewDTO viewDTO = ItemViewDTOFactory.create(
                getContext(),
                new OpenCommentDTO(
                        new ItemId(2),
                        new UserId("a"),
                        new Date(),
                        false,
                        new ItemId(1),
                        "text",
                        null),
                3,
                true);
        assertThat(viewDTO).isExactlyInstanceOf(CommentView.DTO.class);
        CommentView.DTO commentViewDTO = (CommentView.DTO) viewDTO;
        assertThat(commentViewDTO.getZeroBasedDepth()).isEqualTo(3);
        assertThat(commentViewDTO.isCollapsed()).isTrue();
    }

    @SmallTest
    public void testCreatesDefault()
    {
        assertThat(
                ItemViewDTOFactory.create(
                        getContext(),
                        new OpenLoadingItemFinishedDTO(
                                new OpenItemDTO(
                                        new ItemId(1),
                                        new UserId("a"),
                                        new Date(),
                                        false)))).isExactlyInstanceOf(ItemView.DTO.class);
    }

    @SmallTest
    public void testCreatesList()
    {
        List<ItemDTO> list = Arrays.<ItemDTO>asList(
                new OpenJobDTO(
                        new ItemId(1),
                        new UserId("a"),
                        new Date(),
                        false,
                        "title",
                        "url",
                        32,
                        "text"),
                new OpenStoryDTO(
                        new ItemId(2),
                        new UserId("a"),
                        new Date(),
                        false,
                        "title",
                        "http://urlshouldbe.ok/here",
                        32,
                        null));
        List<ItemViewDTO> created = ItemViewDTOFactory.create(getContext(), list);
        assertThat(created.size()).isEqualTo(2);
        assertThat(created.get(0)).isExactlyInstanceOf(JobView.DTO.class);
        assertThat(created.get(0).getItemId()).isEqualTo(new ItemId(1));
        assertThat(created.get(1)).isExactlyInstanceOf(StoryView.DTO.class);
        assertThat(created.get(1).getItemId()).isEqualTo(new ItemId(2));
    }
}
