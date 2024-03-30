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

import static com.stc.assessment.enums.ItemTypeEnum.*;
import static com.stc.assessment.service.PermissionGroupService.ADMIN_GROUP;
import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepo;
    private final PermissionGroupService permissionGroupService;
    private final PermissionService permissionService;
    private final FileService fileService;

    private final String SPACE_NAME = "stc-assessment";
    private final String FOLDER_NAME = "backend";
    private final String FILE_NAME = "assessment.pdf";
    private final String FILE_PATH = "src/main/resources/files/assessment.pdf";

    public void createSpace(String spaceName) {
        validateItemName(spaceName, SPACE_NAME);
        if (itemRepo.existsByNameAndType(spaceName, SPACE)) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "space with the same name already exists!");
        }
        PermissionGroups admin = permissionGroupService.findByGroupName(ADMIN_GROUP);
        createItem(spaceName, ItemTypeEnum.SPACE, null, admin);
    }

    public void createFolder(String userEmail, String spaceName, String folderName) {
        validateItemName(spaceName, SPACE_NAME);
        validateItemName(folderName, FOLDER_NAME);

        Item parent = getParentItem(spaceName, SPACE);
        Permissions permission = permissionService.findByUserEmailAndPermissionLevel(userEmail, PermissionLevelEnum.EDIT);

        validateUserAuthorized(parent, permission, SPACE);
        validateItemExists(folderName, FOLDER, spaceName);

        createItem(folderName, FOLDER, parent, parent.getPermissionGroup());
    }

    @Transactional
    public void createFile(String userEmail, String spaceName, String folderName, String fileName, MultipartFile physicalFile) {
        validateItemName(spaceName, SPACE_NAME);
        validateItemName(folderName, FOLDER_NAME);
        validateItemName(fileName, FILE_NAME);

        Item parent = getParentItem(folderName, FOLDER);
        Permissions permission = permissionService.findByUserEmailAndPermissionLevel(userEmail, PermissionLevelEnum.EDIT);

        validateUserAuthorized(parent, permission, FOLDER);
        validateItemExists(fileName, FILE, folderName);

        byte[] content = parseFile(physicalFile);
        Item fileItem = createItem(fileName, FILE, parent, parent.getPermissionGroup());
        fileService.createFile(fileItem, content);
    }

    private void validateItemName(String itemName, String defaultItemName) {
        if (itemName == null || itemName.isEmpty())
            itemName = defaultItemName;
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

    public ItemDTO getItemMetadata(String fileName, String userEmail) {
        fileService.validateFileExists(fileName);
        return itemRepo.getItemMetadataByItemNameAndUserEmail(fileName, userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "user doesn't have the access to view file metadata!"));
    }

    private void validateUserAuthorized(Item parent, Permissions permission, ItemTypeEnum parentType) {
        if (!permission.getPermissionGroup().equals(parent.getPermissionGroup())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    format("user doesn't have the permission to edit this %s!", parentType.name().toLowerCase()));
        }
    }

    private Item getParentItem(String parentName, ItemTypeEnum parentType) {
        return itemRepo.findByName(parentName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        format("parent %s doesn't exist!", parentType.name().toLowerCase())));
    }

    private void validateItemExists(String itemName, ItemTypeEnum itemType, String parentName) {
        if (itemRepo.existsByNameAndTypeAndParent_Name(itemName, itemType, parentName))
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "item with the same name already exists!");
    }
}
