package io.github.lumue.dexter.web.rest;

import io.github.lumue.dexter.DexterApp;
import io.github.lumue.dexter.domain.Document;
import io.github.lumue.dexter.repository.DocumentRepository;
import io.github.lumue.dexter.service.DocumentService;
import io.github.lumue.dexter.repository.search.DocumentSearchRepository;
import io.github.lumue.dexter.web.rest.dto.DocumentDTO;
import io.github.lumue.dexter.web.rest.mapper.DocumentMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.github.lumue.dexter.domain.enumeration.MediaType;

/**
 * Test class for the DocumentResource REST controller.
 *
 * @see DocumentResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DexterApp.class)
@WebAppConfiguration
@IntegrationTest
public class DocumentResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    private static final MediaType DEFAULT_TYPE = MediaType.TEXT;
    private static final MediaType UPDATED_TYPE = MediaType.AUDIO;

    @Inject
    private DocumentRepository documentRepository;

    @Inject
    private DocumentMapper documentMapper;

    @Inject
    private DocumentService documentService;

    @Inject
    private DocumentSearchRepository documentSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restDocumentMockMvc;

    private Document document;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        DocumentResource documentResource = new DocumentResource();
        ReflectionTestUtils.setField(documentResource, "documentService", documentService);
        ReflectionTestUtils.setField(documentResource, "documentMapper", documentMapper);
        this.restDocumentMockMvc = MockMvcBuilders.standaloneSetup(documentResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        documentSearchRepository.deleteAll();
        document = new Document();
        document.setName(DEFAULT_NAME);
        document.setType(DEFAULT_TYPE);
    }

    @Test
    @Transactional
    public void createDocument() throws Exception {
        int databaseSizeBeforeCreate = documentRepository.findAll().size();

        // Create the Document
        DocumentDTO documentDTO = documentMapper.documentToDocumentDTO(document);

        restDocumentMockMvc.perform(post("/api/documents")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(documentDTO)))
                .andExpect(status().isCreated());

        // Validate the Document in the database
        List<Document> documents = documentRepository.findAll();
        assertThat(documents).hasSize(databaseSizeBeforeCreate + 1);
        Document testDocument = documents.get(documents.size() - 1);
        assertThat(testDocument.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDocument.getType()).isEqualTo(DEFAULT_TYPE);

        // Validate the Document in ElasticSearch
        Document documentEs = documentSearchRepository.findOne(testDocument.getId());
        assertThat(documentEs).isEqualToComparingFieldByField(testDocument);
    }

    @Test
    @Transactional
    public void getAllDocuments() throws Exception {
        // Initialize the database
        documentRepository.saveAndFlush(document);

        // Get all the documents
        restDocumentMockMvc.perform(get("/api/documents?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(document.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }

    @Test
    @Transactional
    public void getDocument() throws Exception {
        // Initialize the database
        documentRepository.saveAndFlush(document);

        // Get the document
        restDocumentMockMvc.perform(get("/api/documents/{id}", document.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(document.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingDocument() throws Exception {
        // Get the document
        restDocumentMockMvc.perform(get("/api/documents/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDocument() throws Exception {
        // Initialize the database
        documentRepository.saveAndFlush(document);
        documentSearchRepository.save(document);
        int databaseSizeBeforeUpdate = documentRepository.findAll().size();

        // Update the document
        Document updatedDocument = new Document();
        updatedDocument.setId(document.getId());
        updatedDocument.setName(UPDATED_NAME);
        updatedDocument.setType(UPDATED_TYPE);
        DocumentDTO documentDTO = documentMapper.documentToDocumentDTO(updatedDocument);

        restDocumentMockMvc.perform(put("/api/documents")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(documentDTO)))
                .andExpect(status().isOk());

        // Validate the Document in the database
        List<Document> documents = documentRepository.findAll();
        assertThat(documents).hasSize(databaseSizeBeforeUpdate);
        Document testDocument = documents.get(documents.size() - 1);
        assertThat(testDocument.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDocument.getType()).isEqualTo(UPDATED_TYPE);

        // Validate the Document in ElasticSearch
        Document documentEs = documentSearchRepository.findOne(testDocument.getId());
        assertThat(documentEs).isEqualToComparingFieldByField(testDocument);
    }

    @Test
    @Transactional
    public void deleteDocument() throws Exception {
        // Initialize the database
        documentRepository.saveAndFlush(document);
        documentSearchRepository.save(document);
        int databaseSizeBeforeDelete = documentRepository.findAll().size();

        // Get the document
        restDocumentMockMvc.perform(delete("/api/documents/{id}", document.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean documentExistsInEs = documentSearchRepository.exists(document.getId());
        assertThat(documentExistsInEs).isFalse();

        // Validate the database is empty
        List<Document> documents = documentRepository.findAll();
        assertThat(documents).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchDocument() throws Exception {
        // Initialize the database
        documentRepository.saveAndFlush(document);
        documentSearchRepository.save(document);

        // Search the document
        restDocumentMockMvc.perform(get("/api/_search/documents?query=id:" + document.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(document.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }
}
