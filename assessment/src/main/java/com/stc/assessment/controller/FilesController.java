package com.stc.assessment.controller;

import com.stc.assessment.dto.ItemDTO;
import com.stc.assessment.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("files")
public class FilesController {

    private final FileService fileService;

    @PostMapping(value = "/{space}")
    public void createSpace(@PathVariable(value = "space", required = false) String space) {
        fileService.createSpace(space);
    }

    @PostMapping(value = "/{space}/{folder}")
    public void createFolder(@RequestHeader(name = "user_email") String userEmail,
                             @PathVariable(value = "space", required = false) String space,
                             @PathVariable(value = "folder", required = false) String folder) {
        fileService.createFolder(userEmail, space, folder);
    }

    @PostMapping(value = "/{space}/{folder}/{file}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void createFile(@RequestHeader(name = "user_email") String userEmail,
                           @PathVariable(value = "space", required = false) String space,
                           @PathVariable(value = "folder", required = false) String folder,
                           @PathVariable(value = "file", required = false) String fileName,
                           @RequestBody(required = false) MultipartFile file) {
        fileService.createFile(userEmail, space, folder, fileName, file);
    }

    @GetMapping(value = "/{file_name}")
    public byte[] getFile(@RequestHeader(name = "user_email") String userEmail,
                          @PathVariable (name = "file_name") String fileName) {
        return fileService.getFile(userEmail, fileName);
    }

    @QueryMapping
    public ItemDTO getItemMetadata(@Argument String email, @Argument String name) {
        return fileService.getItemMetadata(name, email);
    }
}
