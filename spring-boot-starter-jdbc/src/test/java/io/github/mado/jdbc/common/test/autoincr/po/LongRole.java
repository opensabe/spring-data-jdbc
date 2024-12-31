package io.github.mado.jdbc.common.test.autoincr.po;


import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author heng.ma
 */

@Table(name = "t_role")
public class LongRole {

    @Id
    @ReadOnlyProperty
    private Long id;

    private String name;

    public LongRole(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
