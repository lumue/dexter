package io.github.lumue.dexter.service;

import io.github.lumue.dexter.domain.File;
import io.github.lumue.dexter.repository.FileRepository;
import io.github.lumue.dexter.repository.search.FileSearchRepository;
import io.github.lumue.dexter.web.rest.dto.FileDTO;
import io.github.lumue.dexter.web.rest.mapper.FileMapper;
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
 * Service Implementation for managing File.
 */
@Service
@Transactional
public class FileService {

    private final Logger log = LoggerFactory.getLogger(FileService.class);
    
    @Inject
    private FileRepository fileRepository;
    
    @Inject
    private FileMapper fileMapper;
    
    @Inject
    private FileSearchRepository fileSearchRepository;
    
    /**
     * Save a file.
     * 
     * @param fileDTO the entity to save
     * @return the persisted entity
     */
    public FileDTO save(FileDTO fileDTO) {
        log.debug("Request to save File : {}", fileDTO);
        File file = fileMapper.fileDTOToFile(fileDTO);
        file = fileRepository.save(file);
        FileDTO result = fileMapper.fileToFileDTO(file);
        fileSearchRepository.save(file);
        return result;
    }

    /**
     *  Get all the files.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<File> findAll(Pageable pageable) {
        log.debug("Request to get all Files");
        Page<File> result = fileRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get one file by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public FileDTO findOne(Long id) {
        log.debug("Request to get File : {}", id);
        File file = fileRepository.findOne(id);
        FileDTO fileDTO = fileMapper.fileToFileDTO(file);
        return fileDTO;
    }

    /**
     *  Delete the  file by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete File : {}", id);
        fileRepository.delete(id);
        fileSearchRepository.delete(id);
    }

    /**
     * Search for the file corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<File> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Files for query {}", query);
        return fileSearchRepository.search(queryStringQuery(query), pageable);
    }
}
