package io.github.lumue.dexter.repository.search;

import io.github.lumue.dexter.domain.NamedProperty;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the NamedProperty entity.
 */
public interface NamedPropertySearchRepository extends ElasticsearchRepository<NamedProperty, Long> {
}
