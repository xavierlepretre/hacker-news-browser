package com.firebase.client.rx;

import android.support.annotation.NonNull;
import com.firebase.client.FirebaseError;

public class FirebaseErrorException extends RuntimeException
{
    @NonNull private final FirebaseError error;

    public FirebaseErrorException(@NonNull FirebaseError error)
    {
        this.error = error;
    }

    public FirebaseErrorException(String detailMessage, @NonNull FirebaseError error)
    {
        super(detailMessage);
        this.error = error;
    }

    @NonNull public FirebaseError getError()
    {
        return error;
    }
}
