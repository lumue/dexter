package io.github.lumue.dexter.repository.search;

import io.github.lumue.dexter.domain.Document;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Document entity.
 */
public interface DocumentSearchRepository extends ElasticsearchRepository<Document, Long> {
}
