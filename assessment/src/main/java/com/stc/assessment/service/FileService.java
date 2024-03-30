package com.stc.assessment.service;

import com.stc.assessment.dao.FilesRepository;
import com.stc.assessment.model.Files;
import com.stc.assessment.model.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
@Service
@RequiredArgsConstructor
public class FileService {
    private final FilesRepository filesRepo;

    public void createFile(Item fileItem, byte[] content) {
        Files fileEntity = new Files();
        fileEntity.setItem(fileItem);
        fileEntity.setData(content);
        filesRepo.save(fileEntity);
    }

    public byte[] getFile(String userEmail, String fileName) {
        validateFileExists(fileName);
        Files file = filesRepo.getFilesByItemName(fileName, userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "user doesn't have the access to view the file!"));
        return file.getData();
    }

    public void validateFileExists(String fileName) {
        if (!filesRepo.existsByItem_Name(fileName))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "file doesn't exist!");
    }
}
