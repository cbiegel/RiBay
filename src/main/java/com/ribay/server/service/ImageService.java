package com.ribay.server.service;

import com.ribay.server.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.activation.MimetypesFileTypeMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by CD on 02.05.2016.
 */
@RestController
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @RequestMapping(path = "/image/{imageId}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getImage(@PathVariable(value = "imageId") String imageId) throws Exception {
        ImageRepository.ImageData data = imageRepository.loadImage(imageId);

        MediaType mediaType = MediaType.parseMediaType(data.mimeType);
        CacheControl cacheControl = CacheControl.empty().noTransform().cachePublic().sMaxAge(30, TimeUnit.DAYS);

        return ResponseEntity.ok().contentType(mediaType).cacheControl(cacheControl).body(data.data);
    }

}
