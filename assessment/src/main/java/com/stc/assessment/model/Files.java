package com.stc.assessment.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.BinaryJdbcType;

import java.util.Arrays;

@Entity
@Data
public class Files {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    @Lob
    @Column(name = "data")
    @JdbcType(BinaryJdbcType.class)
    private byte[] data;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Item.class, optional = false)
    private Item item;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Files files = (Files) o;

        if (id != null ? !id.equals(files.id) : files.id != null) return false;
        if (!Arrays.equals(data, files.data)) return false;
        if (item != null ? !item.equals(files.item) : files.item != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(data);
        result = 31 * result + (item != null ? item.hashCode() : 0);
        return result;
    }
}
