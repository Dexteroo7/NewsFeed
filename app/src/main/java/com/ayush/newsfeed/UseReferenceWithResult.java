package com.ayush.newsfeed;

/**
 * Created by dexter on 07/01/16.
 */
public interface UseReferenceWithResult<Param, Result> {
    Result useReference(Param param);
}
