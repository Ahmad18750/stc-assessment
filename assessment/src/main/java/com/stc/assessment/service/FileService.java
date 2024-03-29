package com.stc.assessment.service;

import com.stc.assessment.dao.FilesRepository;
import com.stc.assessment.dao.ItemRepository;
import com.stc.assessment.dto.ItemDTO;
import com.stc.assessment.enums.ItemTypeEnum;
import com.stc.assessment.enums.PermissionLevelEnum;
import com.stc.assessment.model.Files;
import com.stc.assessment.model.Item;
import com.stc.assessment.model.PermissionGroups;
import com.stc.assessment.model.Permissions;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Paths;

import static com.stc.assessment.service.PermissionGroupService.ADMIN_GROUP;
import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class FileService {
    private final ItemRepository itemRepo;
    private final FilesRepository filesRepo;
    private final PermissionGroupService permissionGroupService;
    private final PermissionService permissionService;

    private final String SPACE_NAME = "stc-assessment";
    private final String FOLDER_NAME = "backend";
    private final String FILE_NAME = "assessment.pdf";

    private final String FILE_PATH = "src/main/resources/files/assessment.pdf";


    @Transactional
    public void createSpace(String spaceName) {
        validateItemName(spaceName, SPACE_NAME);
        PermissionGroups admin = permissionGroupService.findByGroupName(ADMIN_GROUP);
        if (itemRepo.existsByName(spaceName))
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "space with the same name already exists!");

        createItem(spaceName, ItemTypeEnum.SPACE, null, admin);
    }

    public void createFolder(String userEmail, String spaceName, String folderName) {
        validateItemName(spaceName, SPACE_NAME);
        validateItemName(folderName, FOLDER_NAME);
        validateUserAuthorized(userEmail, spaceName, "space");
        validateItemExists(folderName, ItemTypeEnum.FOLDER, spaceName);

        Item parent = itemRepo.findByName(spaceName).get();
        createItem(folderName, ItemTypeEnum.FOLDER, parent, parent.getPermissionGroup());
    }

    @Transactional
    public void createFile(String userEmail, String spaceName, String folderName, String fileName, MultipartFile physicalFile) {
        validateItemName(spaceName, SPACE_NAME);
        validateItemName(folderName, FOLDER_NAME);
        validateItemName(fileName, FILE_NAME);

        validateUserAuthorized(userEmail, folderName, "folder");
        validateItemExists(fileName, ItemTypeEnum.FILE, folderName);

        byte[] content = parseFile(physicalFile);
        Item parent = itemRepo.findByName(folderName).get();
        Item fileItem = createItem(fileName, ItemTypeEnum.FILE, parent, parent.getPermissionGroup());
        createFile(fileItem, content);
    }

    private void validateItemName(String itemName, String defaultItemName) {
        if (itemName == null || itemName.isEmpty())
            itemName = defaultItemName;
    }

    private void createFile(Item fileItem, byte[] content) {
        Files fileEntity = new Files();
        fileEntity.setItem(fileItem);
        fileEntity.setData(content);
        filesRepo.save(fileEntity);
    }

    private Item createItem(String itemName, ItemTypeEnum itemType, Item parentItem, PermissionGroups pg) {
        Item item = new Item();
        item.setName(itemName);
        item.setType(itemType);
        item.setPermissionGroup(pg);
        item.setParent(parentItem);
        return itemRepo.save(item);
    }
    private byte[] parseFile(MultipartFile file) {
        byte[] byteArray;
        try {
            if (file == null) {
                byteArray = java.nio.file.Files.readAllBytes(Paths.get(FILE_PATH));
            } else {
                byteArray = file.getBytes();
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "error while parsing file");
        }
        return byteArray;
    }

    public byte[] getFile(String userEmail, String fileName) {
        validateFileExists(fileName);
        Files file = filesRepo.getFilesByItemName(fileName, userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "user doesn't have the access to view the file!"));
        return file.getData();
    }

    public ItemDTO getItemMetadata(String fileName, String userEmail) {
        validateFileExists(fileName);
        return itemRepo.getItemMetadataByItemNameAndUserEmail(fileName, userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "user doesn't have the access to view file metadata!"));
    }

    private void validateUserAuthorized(String userEmail, String parentName, String parentType) {
        Item parent = itemRepo.findByName(parentName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        format("parent %s doesn't exist!", parentType)));
        Permissions p = permissionService.findByUserEmailAndPermissionLevel(userEmail, PermissionLevelEnum.EDIT);
        if (!p.getPermissionGroup().equals(parent.getPermissionGroup())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    format("user doesn't have the permission to edit this %s!", parentType));
        }
    }

    private void validateItemExists(String itemName, ItemTypeEnum itemType, String parentName) {
        if (itemRepo.existsByNameAndTypeAndParent_Name(itemName, itemType, parentName))
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "item with the same name already exists!");
    }

    private void validateFileExists(String fileName) {
        if (!filesRepo.existsByItem_Name(fileName))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "file doesn't exist!");
    }
}
