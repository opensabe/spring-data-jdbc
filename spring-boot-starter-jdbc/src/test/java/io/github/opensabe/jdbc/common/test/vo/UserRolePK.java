package io.github.opensabe.jdbc.common.test.vo;

/**
 * @author heng.ma
 */

public class UserRolePK {
    private String userId;

    private String roleId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public UserRolePK(String userId, String roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }

    public UserRolePK() {
    }
}
