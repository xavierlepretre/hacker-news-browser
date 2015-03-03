package com.ycombinator.news.service;

import android.support.annotation.NonNull;
import com.firebase.client.DataSnapshot;
import rx.functions.Func1;

public class DataSnapshotMapper<T> implements Func1<DataSnapshot, T>
{
    @NonNull private final Class<? extends T> aClass;

    public DataSnapshotMapper(@NonNull Class<? extends T> aClass)
    {
        this.aClass = aClass;
    }

    @Override public T call(DataSnapshot dataSnapshot)
    {
        return dataSnapshot.getValue(aClass);
    }
}
