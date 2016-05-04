package io.github.lumue.dexter.web.rest;

import io.github.lumue.dexter.DexterApp;
import io.github.lumue.dexter.domain.File;
import io.github.lumue.dexter.repository.FileRepository;
import io.github.lumue.dexter.service.FileService;
import io.github.lumue.dexter.repository.search.FileSearchRepository;
import io.github.lumue.dexter.web.rest.dto.FileDTO;
import io.github.lumue.dexter.web.rest.mapper.FileMapper;

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
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the FileResource REST controller.
 *
 * @see FileResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DexterApp.class)
@WebAppConfiguration
@IntegrationTest
public class FileResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));

    private static final String DEFAULT_PATH = "AAAAA";
    private static final String UPDATED_PATH = "BBBBB";
    private static final String DEFAULT_HASH = "AAAAA";
    private static final String UPDATED_HASH = "BBBBB";

    private static final ZonedDateTime DEFAULT_CREATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_CREATED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_CREATED_STR = dateTimeFormatter.format(DEFAULT_CREATED);

    private static final ZonedDateTime DEFAULT_LASTMODIFIED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_LASTMODIFIED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_LASTMODIFIED_STR = dateTimeFormatter.format(DEFAULT_LASTMODIFIED);

    private static final ZonedDateTime DEFAULT_LASTSEEN = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_LASTSEEN = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_LASTSEEN_STR = dateTimeFormatter.format(DEFAULT_LASTSEEN);

    private static final ZonedDateTime DEFAULT_FIRSTSEEN = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_FIRSTSEEN = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_FIRSTSEEN_STR = dateTimeFormatter.format(DEFAULT_FIRSTSEEN);

    @Inject
    private FileRepository fileRepository;

    @Inject
    private FileMapper fileMapper;

    @Inject
    private FileService fileService;

    @Inject
    private FileSearchRepository fileSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restFileMockMvc;

    private File file;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        FileResource fileResource = new FileResource();
        ReflectionTestUtils.setField(fileResource, "fileService", fileService);
        ReflectionTestUtils.setField(fileResource, "fileMapper", fileMapper);
        this.restFileMockMvc = MockMvcBuilders.standaloneSetup(fileResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        fileSearchRepository.deleteAll();
        file = new File();
        file.setPath(DEFAULT_PATH);
        file.setHash(DEFAULT_HASH);
        file.setCreated(DEFAULT_CREATED);
        file.setLastmodified(DEFAULT_LASTMODIFIED);
        file.setLastseen(DEFAULT_LASTSEEN);
        file.setFirstseen(DEFAULT_FIRSTSEEN);
    }

    @Test
    @Transactional
    public void createFile() throws Exception {
        int databaseSizeBeforeCreate = fileRepository.findAll().size();

        // Create the File
        FileDTO fileDTO = fileMapper.fileToFileDTO(file);

        restFileMockMvc.perform(post("/api/files")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(fileDTO)))
                .andExpect(status().isCreated());

        // Validate the File in the database
        List<File> files = fileRepository.findAll();
        assertThat(files).hasSize(databaseSizeBeforeCreate + 1);
        File testFile = files.get(files.size() - 1);
        assertThat(testFile.getPath()).isEqualTo(DEFAULT_PATH);
        assertThat(testFile.getHash()).isEqualTo(DEFAULT_HASH);
        assertThat(testFile.getCreated()).isEqualTo(DEFAULT_CREATED);
        assertThat(testFile.getLastmodified()).isEqualTo(DEFAULT_LASTMODIFIED);
        assertThat(testFile.getLastseen()).isEqualTo(DEFAULT_LASTSEEN);
        assertThat(testFile.getFirstseen()).isEqualTo(DEFAULT_FIRSTSEEN);

        // Validate the File in ElasticSearch
        File fileEs = fileSearchRepository.findOne(testFile.getId());
        assertThat(fileEs).isEqualToComparingFieldByField(testFile);
    }

    @Test
    @Transactional
    public void checkPathIsRequired() throws Exception {
        int databaseSizeBeforeTest = fileRepository.findAll().size();
        // set the field null
        file.setPath(null);

        // Create the File, which fails.
        FileDTO fileDTO = fileMapper.fileToFileDTO(file);

        restFileMockMvc.perform(post("/api/files")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(fileDTO)))
                .andExpect(status().isBadRequest());

        List<File> files = fileRepository.findAll();
        assertThat(files).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCreatedIsRequired() throws Exception {
        int databaseSizeBeforeTest = fileRepository.findAll().size();
        // set the field null
        file.setCreated(null);

        // Create the File, which fails.
        FileDTO fileDTO = fileMapper.fileToFileDTO(file);

        restFileMockMvc.perform(post("/api/files")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(fileDTO)))
                .andExpect(status().isBadRequest());

        List<File> files = fileRepository.findAll();
        assertThat(files).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedIsRequired() throws Exception {
        int databaseSizeBeforeTest = fileRepository.findAll().size();
        // set the field null
        file.setLastmodified(null);

        // Create the File, which fails.
        FileDTO fileDTO = fileMapper.fileToFileDTO(file);

        restFileMockMvc.perform(post("/api/files")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(fileDTO)))
                .andExpect(status().isBadRequest());

        List<File> files = fileRepository.findAll();
        assertThat(files).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastseenIsRequired() throws Exception {
        int databaseSizeBeforeTest = fileRepository.findAll().size();
        // set the field null
        file.setLastseen(null);

        // Create the File, which fails.
        FileDTO fileDTO = fileMapper.fileToFileDTO(file);

        restFileMockMvc.perform(post("/api/files")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(fileDTO)))
                .andExpect(status().isBadRequest());

        List<File> files = fileRepository.findAll();
        assertThat(files).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkFirstseenIsRequired() throws Exception {
        int databaseSizeBeforeTest = fileRepository.findAll().size();
        // set the field null
        file.setFirstseen(null);

        // Create the File, which fails.
        FileDTO fileDTO = fileMapper.fileToFileDTO(file);

        restFileMockMvc.perform(post("/api/files")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(fileDTO)))
                .andExpect(status().isBadRequest());

        List<File> files = fileRepository.findAll();
        assertThat(files).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllFiles() throws Exception {
        // Initialize the database
        fileRepository.saveAndFlush(file);

        // Get all the files
        restFileMockMvc.perform(get("/api/files?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(file.getId().intValue())))
                .andExpect(jsonPath("$.[*].path").value(hasItem(DEFAULT_PATH.toString())))
                .andExpect(jsonPath("$.[*].hash").value(hasItem(DEFAULT_HASH.toString())))
                .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED_STR)))
                .andExpect(jsonPath("$.[*].lastmodified").value(hasItem(DEFAULT_LASTMODIFIED_STR)))
                .andExpect(jsonPath("$.[*].lastseen").value(hasItem(DEFAULT_LASTSEEN_STR)))
                .andExpect(jsonPath("$.[*].firstseen").value(hasItem(DEFAULT_FIRSTSEEN_STR)));
    }

    @Test
    @Transactional
    public void getFile() throws Exception {
        // Initialize the database
        fileRepository.saveAndFlush(file);

        // Get the file
        restFileMockMvc.perform(get("/api/files/{id}", file.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(file.getId().intValue()))
            .andExpect(jsonPath("$.path").value(DEFAULT_PATH.toString()))
            .andExpect(jsonPath("$.hash").value(DEFAULT_HASH.toString()))
            .andExpect(jsonPath("$.created").value(DEFAULT_CREATED_STR))
            .andExpect(jsonPath("$.lastmodified").value(DEFAULT_LASTMODIFIED_STR))
            .andExpect(jsonPath("$.lastseen").value(DEFAULT_LASTSEEN_STR))
            .andExpect(jsonPath("$.firstseen").value(DEFAULT_FIRSTSEEN_STR));
    }

    @Test
    @Transactional
    public void getNonExistingFile() throws Exception {
        // Get the file
        restFileMockMvc.perform(get("/api/files/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFile() throws Exception {
        // Initialize the database
        fileRepository.saveAndFlush(file);
        fileSearchRepository.save(file);
        int databaseSizeBeforeUpdate = fileRepository.findAll().size();

        // Update the file
        File updatedFile = new File();
        updatedFile.setId(file.getId());
        updatedFile.setPath(UPDATED_PATH);
        updatedFile.setHash(UPDATED_HASH);
        updatedFile.setCreated(UPDATED_CREATED);
        updatedFile.setLastmodified(UPDATED_LASTMODIFIED);
        updatedFile.setLastseen(UPDATED_LASTSEEN);
        updatedFile.setFirstseen(UPDATED_FIRSTSEEN);
        FileDTO fileDTO = fileMapper.fileToFileDTO(updatedFile);

        restFileMockMvc.perform(put("/api/files")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(fileDTO)))
                .andExpect(status().isOk());

        // Validate the File in the database
        List<File> files = fileRepository.findAll();
        assertThat(files).hasSize(databaseSizeBeforeUpdate);
        File testFile = files.get(files.size() - 1);
        assertThat(testFile.getPath()).isEqualTo(UPDATED_PATH);
        assertThat(testFile.getHash()).isEqualTo(UPDATED_HASH);
        assertThat(testFile.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testFile.getLastmodified()).isEqualTo(UPDATED_LASTMODIFIED);
        assertThat(testFile.getLastseen()).isEqualTo(UPDATED_LASTSEEN);
        assertThat(testFile.getFirstseen()).isEqualTo(UPDATED_FIRSTSEEN);

        // Validate the File in ElasticSearch
        File fileEs = fileSearchRepository.findOne(testFile.getId());
        assertThat(fileEs).isEqualToComparingFieldByField(testFile);
    }

    @Test
    @Transactional
    public void deleteFile() throws Exception {
        // Initialize the database
        fileRepository.saveAndFlush(file);
        fileSearchRepository.save(file);
        int databaseSizeBeforeDelete = fileRepository.findAll().size();

        // Get the file
        restFileMockMvc.perform(delete("/api/files/{id}", file.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean fileExistsInEs = fileSearchRepository.exists(file.getId());
        assertThat(fileExistsInEs).isFalse();

        // Validate the database is empty
        List<File> files = fileRepository.findAll();
        assertThat(files).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchFile() throws Exception {
        // Initialize the database
        fileRepository.saveAndFlush(file);
        fileSearchRepository.save(file);

        // Search the file
        restFileMockMvc.perform(get("/api/_search/files?query=id:" + file.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(file.getId().intValue())))
            .andExpect(jsonPath("$.[*].path").value(hasItem(DEFAULT_PATH.toString())))
            .andExpect(jsonPath("$.[*].hash").value(hasItem(DEFAULT_HASH.toString())))
            .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED_STR)))
            .andExpect(jsonPath("$.[*].lastmodified").value(hasItem(DEFAULT_LASTMODIFIED_STR)))
            .andExpect(jsonPath("$.[*].lastseen").value(hasItem(DEFAULT_LASTSEEN_STR)))
            .andExpect(jsonPath("$.[*].firstseen").value(hasItem(DEFAULT_FIRSTSEEN_STR)));
    }
}
