package io.github.lumue.dexter.service;

import io.github.lumue.dexter.domain.Document;
import io.github.lumue.dexter.repository.DocumentRepository;
import io.github.lumue.dexter.repository.search.DocumentSearchRepository;
import io.github.lumue.dexter.web.rest.dto.DocumentDTO;
import io.github.lumue.dexter.web.rest.mapper.DocumentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Document.
 */
@Service
@Transactional
public class DocumentService {

    private final Logger log = LoggerFactory.getLogger(DocumentService.class);
    
    @Inject
    private DocumentRepository documentRepository;
    
    @Inject
    private DocumentMapper documentMapper;
    
    @Inject
    private DocumentSearchRepository documentSearchRepository;
    
    /**
     * Save a document.
     * 
     * @param documentDTO the entity to save
     * @return the persisted entity
     */
    public DocumentDTO save(DocumentDTO documentDTO) {
        log.debug("Request to save Document : {}", documentDTO);
        Document document = documentMapper.documentDTOToDocument(documentDTO);
        document = documentRepository.save(document);
        DocumentDTO result = documentMapper.documentToDocumentDTO(document);
        documentSearchRepository.save(document);
        return result;
    }

    /**
     *  Get all the documents.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Document> findAll(Pageable pageable) {
        log.debug("Request to get all Documents");
        Page<Document> result = documentRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get one document by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public DocumentDTO findOne(Long id) {
        log.debug("Request to get Document : {}", id);
        Document document = documentRepository.findOne(id);
        DocumentDTO documentDTO = documentMapper.documentToDocumentDTO(document);
        return documentDTO;
    }

    /**
     *  Delete the  document by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Document : {}", id);
        documentRepository.delete(id);
        documentSearchRepository.delete(id);
    }

    /**
     * Search for the document corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Document> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Documents for query {}", query);
        return documentSearchRepository.search(queryStringQuery(query), pageable);
    }
}
