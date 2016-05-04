package io.github.lumue.dexter.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A File.
 */
@Entity
@Table(name = "file")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "file")
public class File implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "path", nullable = false)
    private String path;

    @Column(name = "hash")
    private String hash;

    @NotNull
    @Column(name = "created", nullable = false)
    private ZonedDateTime created;

    @NotNull
    @Column(name = "lastmodified", nullable = false)
    private ZonedDateTime lastmodified;

    @NotNull
    @Column(name = "lastseen", nullable = false)
    private ZonedDateTime lastseen;

    @NotNull
    @Column(name = "firstseen", nullable = false)
    private ZonedDateTime firstseen;

    @OneToMany(mappedBy = "file")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<NamedProperty> properties = new HashSet<>();

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

    public Set<NamedProperty> getProperties() {
        return properties;
    }

    public void setProperties(Set<NamedProperty> namedProperties) {
        this.properties = namedProperties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        File file = (File) o;
        if(file.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, file.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "File{" +
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
