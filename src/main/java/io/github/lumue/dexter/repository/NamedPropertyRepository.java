package io.github.lumue.dexter.repository;

import io.github.lumue.dexter.domain.NamedProperty;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the NamedProperty entity.
 */
public interface NamedPropertyRepository extends JpaRepository<NamedProperty,Long> {

}
