package io.github.lumue.dexter.web.rest.mapper;

import io.github.lumue.dexter.domain.*;
import io.github.lumue.dexter.web.rest.dto.DocumentDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Document and its DTO DocumentDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface DocumentMapper {

    @Mapping(source = "file.id", target = "fileId")
    DocumentDTO documentToDocumentDTO(Document document);

    List<DocumentDTO> documentsToDocumentDTOs(List<Document> documents);

    @Mapping(source = "fileId", target = "file")
    @Mapping(target = "properties", ignore = true)
    Document documentDTOToDocument(DocumentDTO documentDTO);

    List<Document> documentDTOsToDocuments(List<DocumentDTO> documentDTOs);

    default File fileFromId(Long id) {
        if (id == null) {
            return null;
        }
        File file = new File();
        file.setId(id);
        return file;
    }
}
