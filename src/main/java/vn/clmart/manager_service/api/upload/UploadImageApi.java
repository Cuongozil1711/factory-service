package vn.clmart.manager_service.api.upload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.clmart.manager_service.service.CloudinaryService;

import java.io.IOException;


@RestController
@RequestMapping("/upload")
public class UploadImageApi {
    @Autowired
    CloudinaryService cloudinaryService;

    @PostMapping("/image")
    public ResponseEntity<String> uploadGif(
            @RequestParam("type") String type,
            @RequestParam(value = "gifFile", required = false) MultipartFile gifFile,
            @RequestParam("rootId") Long rootId,
            @RequestHeader Long cid
    ) throws IOException {
        String url = cloudinaryService.upload(gifFile, type, rootId, cid);
        return new ResponseEntity<>(url, HttpStatus.OK);
    }

}
