package com.example.fbuteamproject.utils;

import com.example.fbuteamproject.utils.FlickrApi.Photo;
import java.util.List;

/**
 * An interface for an object that displays Glide Flickr samples
 * objects.
 */
public interface PhotoViewer {
    /**
     * Called whenever new Glide Flickr samples are loaded.
     *
     * @param photos The loaded photos.
     */
    void onPhotosUpdated(List<Photo> photos);
}

