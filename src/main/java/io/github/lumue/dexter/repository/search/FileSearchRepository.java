package io.github.lumue.dexter.repository.search;

import io.github.lumue.dexter.domain.File;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the File entity.
 */
public interface FileSearchRepository extends ElasticsearchRepository<File, Long> {
}
