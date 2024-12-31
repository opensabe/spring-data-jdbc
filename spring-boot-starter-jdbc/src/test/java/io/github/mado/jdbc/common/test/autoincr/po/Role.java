package io.github.mado.jdbc.common.test.autoincr.po;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author heng.ma
 */

@Table(name = "t_role")
public class Role {

    @Id
    @ReadOnlyProperty
    private Integer id;

    private String name;

    public Role(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Role() {
    }
}
