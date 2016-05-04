package io.github.lumue.dexter.repository;

import io.github.lumue.dexter.domain.File;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the File entity.
 */
public interface FileRepository extends JpaRepository<File,Long> {

}
