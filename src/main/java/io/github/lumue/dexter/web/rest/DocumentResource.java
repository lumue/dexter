package io.github.lumue.dexter.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.lumue.dexter.domain.Document;
import io.github.lumue.dexter.service.DocumentService;
import io.github.lumue.dexter.web.rest.util.HeaderUtil;
import io.github.lumue.dexter.web.rest.util.PaginationUtil;
import io.github.lumue.dexter.web.rest.dto.DocumentDTO;
import io.github.lumue.dexter.web.rest.mapper.DocumentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Document.
 */
@RestController
@RequestMapping("/api")
public class DocumentResource {

    private final Logger log = LoggerFactory.getLogger(DocumentResource.class);
        
    @Inject
    private DocumentService documentService;
    
    @Inject
    private DocumentMapper documentMapper;
    
    /**
     * POST  /documents : Create a new document.
     *
     * @param documentDTO the documentDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new documentDTO, or with status 400 (Bad Request) if the document has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/documents",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<DocumentDTO> createDocument(@RequestBody DocumentDTO documentDTO) throws URISyntaxException {
        log.debug("REST request to save Document : {}", documentDTO);
        if (documentDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("document", "idexists", "A new document cannot already have an ID")).body(null);
        }
        DocumentDTO result = documentService.save(documentDTO);
        return ResponseEntity.created(new URI("/api/documents/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("document", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /documents : Updates an existing document.
     *
     * @param documentDTO the documentDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated documentDTO,
     * or with status 400 (Bad Request) if the documentDTO is not valid,
     * or with status 500 (Internal Server Error) if the documentDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/documents",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<DocumentDTO> updateDocument(@RequestBody DocumentDTO documentDTO) throws URISyntaxException {
        log.debug("REST request to update Document : {}", documentDTO);
        if (documentDTO.getId() == null) {
            return createDocument(documentDTO);
        }
        DocumentDTO result = documentService.save(documentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("document", documentDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /documents : get all the documents.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of documents in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/documents",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<DocumentDTO>> getAllDocuments(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Documents");
        Page<Document> page = documentService.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/documents");
        return new ResponseEntity<>(documentMapper.documentsToDocumentDTOs(page.getContent()), headers, HttpStatus.OK);
    }

    /**
     * GET  /documents/:id : get the "id" document.
     *
     * @param id the id of the documentDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the documentDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/documents/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<DocumentDTO> getDocument(@PathVariable Long id) {
        log.debug("REST request to get Document : {}", id);
        DocumentDTO documentDTO = documentService.findOne(id);
        return Optional.ofNullable(documentDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /documents/:id : delete the "id" document.
     *
     * @param id the id of the documentDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/documents/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        log.debug("REST request to delete Document : {}", id);
        documentService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("document", id.toString())).build();
    }

    /**
     * SEARCH  /_search/documents?query=:query : search for the document corresponding
     * to the query.
     *
     * @param query the query of the document search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/documents",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<DocumentDTO>> searchDocuments(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Documents for query {}", query);
        Page<Document> page = documentService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/documents");
        return new ResponseEntity<>(documentMapper.documentsToDocumentDTOs(page.getContent()), headers, HttpStatus.OK);
    }

}
