package io.github.lumue.dexter.repository;

import io.github.lumue.dexter.domain.Document;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Document entity.
 */
public interface DocumentRepository extends JpaRepository<Document,Long> {

}
