package io.github.lumue.dexter.web.rest;

import io.github.lumue.dexter.DexterApp;
import io.github.lumue.dexter.domain.NamedProperty;
import io.github.lumue.dexter.repository.NamedPropertyRepository;
import io.github.lumue.dexter.repository.search.NamedPropertySearchRepository;

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


/**
 * Test class for the NamedPropertyResource REST controller.
 *
 * @see NamedPropertyResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DexterApp.class)
@WebAppConfiguration
@IntegrationTest
public class NamedPropertyResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";
    private static final String DEFAULT_VALUE = "AAAAA";
    private static final String UPDATED_VALUE = "BBBBB";

    @Inject
    private NamedPropertyRepository namedPropertyRepository;

    @Inject
    private NamedPropertySearchRepository namedPropertySearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restNamedPropertyMockMvc;

    private NamedProperty namedProperty;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        NamedPropertyResource namedPropertyResource = new NamedPropertyResource();
        ReflectionTestUtils.setField(namedPropertyResource, "namedPropertySearchRepository", namedPropertySearchRepository);
        ReflectionTestUtils.setField(namedPropertyResource, "namedPropertyRepository", namedPropertyRepository);
        this.restNamedPropertyMockMvc = MockMvcBuilders.standaloneSetup(namedPropertyResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        namedPropertySearchRepository.deleteAll();
        namedProperty = new NamedProperty();
        namedProperty.setName(DEFAULT_NAME);
        namedProperty.setValue(DEFAULT_VALUE);
    }

    @Test
    @Transactional
    public void createNamedProperty() throws Exception {
        int databaseSizeBeforeCreate = namedPropertyRepository.findAll().size();

        // Create the NamedProperty

        restNamedPropertyMockMvc.perform(post("/api/named-properties")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(namedProperty)))
                .andExpect(status().isCreated());

        // Validate the NamedProperty in the database
        List<NamedProperty> namedProperties = namedPropertyRepository.findAll();
        assertThat(namedProperties).hasSize(databaseSizeBeforeCreate + 1);
        NamedProperty testNamedProperty = namedProperties.get(namedProperties.size() - 1);
        assertThat(testNamedProperty.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testNamedProperty.getValue()).isEqualTo(DEFAULT_VALUE);

        // Validate the NamedProperty in ElasticSearch
        NamedProperty namedPropertyEs = namedPropertySearchRepository.findOne(testNamedProperty.getId());
        assertThat(namedPropertyEs).isEqualToComparingFieldByField(testNamedProperty);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = namedPropertyRepository.findAll().size();
        // set the field null
        namedProperty.setName(null);

        // Create the NamedProperty, which fails.

        restNamedPropertyMockMvc.perform(post("/api/named-properties")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(namedProperty)))
                .andExpect(status().isBadRequest());

        List<NamedProperty> namedProperties = namedPropertyRepository.findAll();
        assertThat(namedProperties).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = namedPropertyRepository.findAll().size();
        // set the field null
        namedProperty.setValue(null);

        // Create the NamedProperty, which fails.

        restNamedPropertyMockMvc.perform(post("/api/named-properties")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(namedProperty)))
                .andExpect(status().isBadRequest());

        List<NamedProperty> namedProperties = namedPropertyRepository.findAll();
        assertThat(namedProperties).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllNamedProperties() throws Exception {
        // Initialize the database
        namedPropertyRepository.saveAndFlush(namedProperty);

        // Get all the namedProperties
        restNamedPropertyMockMvc.perform(get("/api/named-properties?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(namedProperty.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.toString())));
    }

    @Test
    @Transactional
    public void getNamedProperty() throws Exception {
        // Initialize the database
        namedPropertyRepository.saveAndFlush(namedProperty);

        // Get the namedProperty
        restNamedPropertyMockMvc.perform(get("/api/named-properties/{id}", namedProperty.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(namedProperty.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingNamedProperty() throws Exception {
        // Get the namedProperty
        restNamedPropertyMockMvc.perform(get("/api/named-properties/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateNamedProperty() throws Exception {
        // Initialize the database
        namedPropertyRepository.saveAndFlush(namedProperty);
        namedPropertySearchRepository.save(namedProperty);
        int databaseSizeBeforeUpdate = namedPropertyRepository.findAll().size();

        // Update the namedProperty
        NamedProperty updatedNamedProperty = new NamedProperty();
        updatedNamedProperty.setId(namedProperty.getId());
        updatedNamedProperty.setName(UPDATED_NAME);
        updatedNamedProperty.setValue(UPDATED_VALUE);

        restNamedPropertyMockMvc.perform(put("/api/named-properties")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedNamedProperty)))
                .andExpect(status().isOk());

        // Validate the NamedProperty in the database
        List<NamedProperty> namedProperties = namedPropertyRepository.findAll();
        assertThat(namedProperties).hasSize(databaseSizeBeforeUpdate);
        NamedProperty testNamedProperty = namedProperties.get(namedProperties.size() - 1);
        assertThat(testNamedProperty.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testNamedProperty.getValue()).isEqualTo(UPDATED_VALUE);

        // Validate the NamedProperty in ElasticSearch
        NamedProperty namedPropertyEs = namedPropertySearchRepository.findOne(testNamedProperty.getId());
        assertThat(namedPropertyEs).isEqualToComparingFieldByField(testNamedProperty);
    }

    @Test
    @Transactional
    public void deleteNamedProperty() throws Exception {
        // Initialize the database
        namedPropertyRepository.saveAndFlush(namedProperty);
        namedPropertySearchRepository.save(namedProperty);
        int databaseSizeBeforeDelete = namedPropertyRepository.findAll().size();

        // Get the namedProperty
        restNamedPropertyMockMvc.perform(delete("/api/named-properties/{id}", namedProperty.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean namedPropertyExistsInEs = namedPropertySearchRepository.exists(namedProperty.getId());
        assertThat(namedPropertyExistsInEs).isFalse();

        // Validate the database is empty
        List<NamedProperty> namedProperties = namedPropertyRepository.findAll();
        assertThat(namedProperties).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchNamedProperty() throws Exception {
        // Initialize the database
        namedPropertyRepository.saveAndFlush(namedProperty);
        namedPropertySearchRepository.save(namedProperty);

        // Search the namedProperty
        restNamedPropertyMockMvc.perform(get("/api/_search/named-properties?query=id:" + namedProperty.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(namedProperty.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.toString())));
    }
}
