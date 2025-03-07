package io.github.opensabe.jdbc.common.test.vo;

import org.springframework.data.relational.core.mapping.Table;

/**
 * @author heng.ma
 */
@Table(name = "t_user_role")
public class UserRole  extends UserRolePK {

    private String userName;

    private String roleName;

    public UserRole(String userId, String roleId, String userName, String roleName) {
        super(userId, roleId);
        this.userName = userName;
        this.roleName = roleName;
    }

    public UserRole() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
