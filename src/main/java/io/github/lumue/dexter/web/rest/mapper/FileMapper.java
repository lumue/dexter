package io.github.lumue.dexter.web.rest.mapper;

import io.github.lumue.dexter.domain.*;
import io.github.lumue.dexter.web.rest.dto.FileDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity File and its DTO FileDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface FileMapper {

    FileDTO fileToFileDTO(File file);

    List<FileDTO> filesToFileDTOs(List<File> files);

    @Mapping(target = "properties", ignore = true)
    File fileDTOToFile(FileDTO fileDTO);

    List<File> fileDTOsToFiles(List<FileDTO> fileDTOs);
}
