package com.firebase.client.rx;

import android.support.annotation.NonNull;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

public class FirebaseObservable
{
    @NonNull public static Observable<DataSnapshot> read(@NonNull final Query query)
    {
        return Observable.create(new Observable.OnSubscribe<DataSnapshot>()
        {
            @Override public void call(final Subscriber<? super DataSnapshot> subscriber)
            {
                final ValueEventListener listener = new ValueEventListener()
                {
                    @Override public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        subscriber.onNext(dataSnapshot);
                    }

                    @Override public void onCancelled(FirebaseError firebaseError)
                    {
                        subscriber.onError(new FirebaseErrorException(firebaseError));
                    }
                };
                query.addValueEventListener(listener);
                subscriber.add(Subscriptions.create(new Action0()
                {
                    @Override public void call()
                    {
                        query.removeEventListener(listener);
                    }
                }));
            }
        });
    }
}
