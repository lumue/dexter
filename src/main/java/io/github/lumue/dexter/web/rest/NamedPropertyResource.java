package io.github.lumue.dexter.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.lumue.dexter.domain.NamedProperty;
import io.github.lumue.dexter.repository.NamedPropertyRepository;
import io.github.lumue.dexter.repository.search.NamedPropertySearchRepository;
import io.github.lumue.dexter.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing NamedProperty.
 */
@RestController
@RequestMapping("/api")
public class NamedPropertyResource {

    private final Logger log = LoggerFactory.getLogger(NamedPropertyResource.class);
        
    @Inject
    private NamedPropertyRepository namedPropertyRepository;
    
    @Inject
    private NamedPropertySearchRepository namedPropertySearchRepository;
    
    /**
     * POST  /named-properties : Create a new namedProperty.
     *
     * @param namedProperty the namedProperty to create
     * @return the ResponseEntity with status 201 (Created) and with body the new namedProperty, or with status 400 (Bad Request) if the namedProperty has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/named-properties",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<NamedProperty> createNamedProperty(@Valid @RequestBody NamedProperty namedProperty) throws URISyntaxException {
        log.debug("REST request to save NamedProperty : {}", namedProperty);
        if (namedProperty.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("namedProperty", "idexists", "A new namedProperty cannot already have an ID")).body(null);
        }
        NamedProperty result = namedPropertyRepository.save(namedProperty);
        namedPropertySearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/named-properties/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("namedProperty", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /named-properties : Updates an existing namedProperty.
     *
     * @param namedProperty the namedProperty to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated namedProperty,
     * or with status 400 (Bad Request) if the namedProperty is not valid,
     * or with status 500 (Internal Server Error) if the namedProperty couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/named-properties",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<NamedProperty> updateNamedProperty(@Valid @RequestBody NamedProperty namedProperty) throws URISyntaxException {
        log.debug("REST request to update NamedProperty : {}", namedProperty);
        if (namedProperty.getId() == null) {
            return createNamedProperty(namedProperty);
        }
        NamedProperty result = namedPropertyRepository.save(namedProperty);
        namedPropertySearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("namedProperty", namedProperty.getId().toString()))
            .body(result);
    }

    /**
     * GET  /named-properties : get all the namedProperties.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of namedProperties in body
     */
    @RequestMapping(value = "/named-properties",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<NamedProperty> getAllNamedProperties() {
        log.debug("REST request to get all NamedProperties");
        List<NamedProperty> namedProperties = namedPropertyRepository.findAll();
        return namedProperties;
    }

    /**
     * GET  /named-properties/:id : get the "id" namedProperty.
     *
     * @param id the id of the namedProperty to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the namedProperty, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/named-properties/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<NamedProperty> getNamedProperty(@PathVariable Long id) {
        log.debug("REST request to get NamedProperty : {}", id);
        NamedProperty namedProperty = namedPropertyRepository.findOne(id);
        return Optional.ofNullable(namedProperty)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /named-properties/:id : delete the "id" namedProperty.
     *
     * @param id the id of the namedProperty to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/named-properties/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteNamedProperty(@PathVariable Long id) {
        log.debug("REST request to delete NamedProperty : {}", id);
        namedPropertyRepository.delete(id);
        namedPropertySearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("namedProperty", id.toString())).build();
    }

    /**
     * SEARCH  /_search/named-properties?query=:query : search for the namedProperty corresponding
     * to the query.
     *
     * @param query the query of the namedProperty search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/named-properties",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<NamedProperty> searchNamedProperties(@RequestParam String query) {
        log.debug("REST request to search NamedProperties for query {}", query);
        return StreamSupport
            .stream(namedPropertySearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
