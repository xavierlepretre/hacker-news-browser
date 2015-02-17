package com.ycombinator.news.service;

import android.test.AndroidTestCase;
import com.ycombinator.news.dto.CommentDTO;
import com.ycombinator.news.dto.ItemDTO;
import com.ycombinator.news.dto.ItemId;
import com.ycombinator.news.dto.JobDTO;
import com.ycombinator.news.dto.PollDTO;
import com.ycombinator.news.dto.PollOptDTO;
import com.ycombinator.news.dto.StoryDTO;
import com.ycombinator.news.dto.UpdateDTO;
import com.ycombinator.news.dto.UserDTO;
import com.ycombinator.news.dto.UserId;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class HackerNewsServiceTest extends AndroidTestCase
{
    HackerNewsService hackerNewsService;

    @Override protected void setUp() throws Exception
    {
        super.setUp();
        hackerNewsService = HackerNewsRestAdapter.createHackerNewsService();
    }

    public void testServiceCreated()
    {
        assertThat(hackerNewsService).isNotNull();
    }

    public void testCanFetchOneUser()
    {
        Iterator<UserDTO> userDTOIterator = hackerNewsService.getUser(new UserId("jl")).toBlocking().getIterator();

        assertThat(userDTOIterator.hasNext()).isTrue();
        UserDTO userDTO = userDTOIterator.next();
        assertThat(userDTO).isNotNull();
        assertThat(userDTO.getId()).isEqualTo(new UserId("jl"));
        assertThat(userDTO.getKarma()).isGreaterThanOrEqualTo(2937);

        assertThat(userDTOIterator.hasNext()).isFalse();
    }

    public void testCanFetchOneStory()
    {
        Iterator<ItemDTO> itemDTOIterator = hackerNewsService.getContent(new ItemId(8863)).toBlocking().getIterator();

        assertThat(itemDTOIterator.hasNext()).isTrue();
        ItemDTO itemDTO = itemDTOIterator.next();
        assertThat(itemDTO).isNotNull();
        assertThat(itemDTO).isExactlyInstanceOf(StoryDTO.class);
        assertThat(itemDTO.getId()).isEqualTo(new ItemId(8863));
        assertThat(itemDTO.getBy()).isEqualTo(new UserId("dhouston"));

        assertThat(itemDTOIterator.hasNext()).isFalse();
    }

    public void testCanFetchOneComment()
    {
        Iterator<ItemDTO> itemDTOIterator = hackerNewsService.getContent(new ItemId(2921983)).toBlocking().getIterator();

        assertThat(itemDTOIterator.hasNext()).isTrue();
        ItemDTO itemDTO = itemDTOIterator.next();
        assertThat(itemDTO).isNotNull();
        assertThat(itemDTO).isExactlyInstanceOf(CommentDTO.class);
        assertThat(itemDTO.getId()).isEqualTo(new ItemId(2921983));
        assertThat(itemDTO.getBy()).isEqualTo(new UserId("norvig"));

        assertThat(itemDTOIterator.hasNext()).isFalse();
    }

    public void testCanFetchOnePoll()
    {
        Iterator<ItemDTO> itemDTOIterator = hackerNewsService.getContent(new ItemId(126809)).toBlocking().getIterator();

        assertThat(itemDTOIterator.hasNext()).isTrue();
        ItemDTO itemDTO = itemDTOIterator.next();
        assertThat(itemDTO).isNotNull();
        assertThat(itemDTO).isExactlyInstanceOf(PollDTO.class);
        assertThat(itemDTO.getId()).isEqualTo(new ItemId(126809));
        assertThat(itemDTO.getBy()).isEqualTo(new UserId("pg"));

        assertThat(itemDTOIterator.hasNext()).isFalse();
    }

    public void testCanFetchOnePollOpt()
    {
        Iterator<ItemDTO> itemDTOIterator = hackerNewsService.getContent(new ItemId(160705)).toBlocking().getIterator();

        assertThat(itemDTOIterator.hasNext()).isTrue();
        ItemDTO itemDTO = itemDTOIterator.next();
        assertThat(itemDTO).isNotNull();
        assertThat(itemDTO).isExactlyInstanceOf(PollOptDTO.class);
        assertThat(itemDTO.getId()).isEqualTo(new ItemId(160705));
        assertThat(itemDTO.getBy()).isEqualTo(new UserId("pg"));

        assertThat(itemDTOIterator.hasNext()).isFalse();
    }

    public void testCanFetchOneJob()
    {
        Iterator<ItemDTO> itemDTOIterator = hackerNewsService.getContent(new ItemId(9052645)).toBlocking().getIterator();

        assertThat(itemDTOIterator.hasNext()).isTrue();
        ItemDTO itemDTO = itemDTOIterator.next();
        assertThat(itemDTO).isNotNull();
        assertThat(itemDTO).isExactlyInstanceOf(JobDTO.class);
        assertThat(itemDTO.getId()).isEqualTo(new ItemId(9052645));
        assertThat(itemDTO.getBy()).isEqualTo(new UserId("alivahab"));

        assertThat(itemDTOIterator.hasNext()).isFalse();
    }

    public void testCanFetchMoreItems()
    {
        Iterator<ItemDTO> itemDTOIterator = hackerNewsService.getContent(Arrays.asList(
                new ItemId(160705), new ItemId(2921983)), 2).toBlocking().getIterator();

        ItemDTO itemDTO;
        int count = 2;
        while (count-- > 0)
        {
            itemDTO = itemDTOIterator.next();

            switch (itemDTO.getId().id)
            {
                case 160705:
                    assertThat(itemDTO).isNotNull();
                    assertThat(itemDTO).isExactlyInstanceOf(PollOptDTO.class);
                    assertThat(itemDTO.getId()).isEqualTo(new ItemId(160705));
                    assertThat(itemDTO.getBy()).isEqualTo(new UserId("pg"));
                    break;

                case 2921983:
                    assertThat(itemDTO).isNotNull();
                    assertThat(itemDTO).isExactlyInstanceOf(CommentDTO.class);
                    assertThat(itemDTO.getId()).isEqualTo(new ItemId(2921983));
                    assertThat(itemDTO.getBy()).isEqualTo(new UserId("norvig"));
            }
        }

        assertThat(itemDTOIterator.hasNext()).isFalse();
    }

    public void testCanFetchTopStories()
    {
        Iterator<List<ItemId>> itemDTOListIterator = hackerNewsService.getTopStories().toBlocking().getIterator();

        assertThat(itemDTOListIterator.hasNext()).isTrue();
        List<ItemId> topStories = itemDTOListIterator.next();
        assertThat(topStories.size()).isLessThanOrEqualTo(100);

        assertThat(itemDTOListIterator.hasNext()).isFalse();
    }

    public void testCanFetchMaxItem()
    {
        Iterator<ItemId> itemIdIterator = hackerNewsService.getMaxItem().toBlocking().getIterator();

        assertThat(itemIdIterator.hasNext()).isTrue();
        ItemId maxItemId = itemIdIterator.next();
        assertThat(maxItemId.id).isGreaterThanOrEqualTo(8423374);

        assertThat(itemIdIterator.hasNext()).isFalse();
    }

    public void testCanFetchUpdates()
    {
        Iterator<UpdateDTO> udpateDTOIterator = hackerNewsService.getUpdates().toBlocking().getIterator();

        assertThat(udpateDTOIterator.hasNext()).isTrue();
        UpdateDTO updateDTO = udpateDTOIterator.next();
        assertThat(updateDTO.getItems().size()).isGreaterThan(5);
        assertThat(updateDTO.getProfiles().size()).isGreaterThan(1);

        assertThat(udpateDTOIterator.hasNext()).isFalse();
    }
}
