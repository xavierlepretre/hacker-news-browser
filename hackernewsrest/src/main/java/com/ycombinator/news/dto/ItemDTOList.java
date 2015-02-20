package com.ycombinator.news.dto;

import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.Collection;

/**
* Necessary to properly serialise lists of ItemDTOs with the proper type.
*/
public class ItemDTOList extends ArrayList<ItemDTO>
{
    @SuppressWarnings("UnusedDeclaration") // Necessary for deserialisation
    public ItemDTOList()
    {
    }

    @SuppressWarnings("UnusedDeclaration")
    public ItemDTOList(@NonNull Collection<? extends ItemDTO> collection)
    {
        super(collection);
    }
}
