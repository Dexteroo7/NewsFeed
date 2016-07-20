package com.ayush.newsfeed;

import com.google.common.base.Optional;

import java.lang.ref.WeakReference;

/**
 * Created by dexter on 18/07/2016.
 */
public class MiscUtils {

    public static <Param> void useReference(final WeakReference<Param> reference,
                                            final UseReference<Param> task) {

        final Param param;
        if (reference == null || (param = reference.get()) == null)
            return;
        task.useReference(param);
    }

    public static <Param, Result> Optional<Result> useReference(final WeakReference<Param> reference,
                                                                final UseReferenceWithResult<Param, Result> task) {

        final Param param;
        if (reference == null || (param = reference.get()) == null)
            return Optional.absent();
        return Optional.of(task.useReference(param));
    }
}
