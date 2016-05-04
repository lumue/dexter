package io.github.lumue.dexter.web.rest.dto;

import java.time.ZonedDateTime;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


/**
 * A DTO for the File entity.
 */
public class FileDTO implements Serializable {

    private Long id;

    @NotNull
    private String path;


    private String hash;


    @NotNull
    private ZonedDateTime created;


    @NotNull
    private ZonedDateTime lastmodified;


    @NotNull
    private ZonedDateTime lastseen;


    @NotNull
    private ZonedDateTime firstseen;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
    public ZonedDateTime getCreated() {
        return created;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }
    public ZonedDateTime getLastmodified() {
        return lastmodified;
    }

    public void setLastmodified(ZonedDateTime lastmodified) {
        this.lastmodified = lastmodified;
    }
    public ZonedDateTime getLastseen() {
        return lastseen;
    }

    public void setLastseen(ZonedDateTime lastseen) {
        this.lastseen = lastseen;
    }
    public ZonedDateTime getFirstseen() {
        return firstseen;
    }

    public void setFirstseen(ZonedDateTime firstseen) {
        this.firstseen = firstseen;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FileDTO fileDTO = (FileDTO) o;

        if ( ! Objects.equals(id, fileDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "FileDTO{" +
            "id=" + id +
            ", path='" + path + "'" +
            ", hash='" + hash + "'" +
            ", created='" + created + "'" +
            ", lastmodified='" + lastmodified + "'" +
            ", lastseen='" + lastseen + "'" +
            ", firstseen='" + firstseen + "'" +
            '}';
    }
}
