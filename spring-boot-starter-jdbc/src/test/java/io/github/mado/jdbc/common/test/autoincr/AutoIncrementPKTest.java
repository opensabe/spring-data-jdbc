package io.github.mado.jdbc.common.test.autoincr;

import io.github.mado.jdbc.common.test.BaseTest;
import io.github.mado.jdbc.common.test.autoincr.po.LongRole;
import io.github.mado.jdbc.common.test.autoincr.po.Role;
import io.github.mado.jdbc.common.test.autoincr.repository.LongRoleRepository;
import io.github.mado.jdbc.common.test.autoincr.repository.RoleRepository;
import io.github.mado.jdbc.core.EnableJdbcRepositories;
import io.github.mado.jdbc.core.lambda.Weekend;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author heng.ma
 */
@EnableJdbcRepositories(basePackageClasses = RoleRepository.class)
public class AutoIncrementPKTest extends BaseTest {


    @Autowired
    private RoleRepository repository;
    @Autowired
    private LongRoleRepository longRoleRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @BeforeEach
    void setup () {
        jdbcTemplate.update("truncate table t_role");
    }

    @Test
    public void insertSelective() {
        Role role = new Role();
        role.setName("role1");

        repository.insertSelective(role);

        assertThat(role.getId()).isNotNull().isEqualTo(1);

    }

    @Test
    public void updateByPrimaryKeySelective() {
        Role role = new Role();
        role.setName("role1");

        repository.insertSelective(role);

        role.setName("role2");

        repository.updateByIdSelective(role);

        Optional<Role> optional = repository.findById(1);

        assertThat(optional)
                .isPresent()
                .get()
                .extracting(Role::getName)
                .isEqualTo("role2");
    }

    @Test
    public void deleteById() {
        Role role = new Role();
        role.setName("role1");
        repository.insertSelective(role);

        assertThat(repository.existsById(1)).isTrue();

        repository.deleteById(1);

        assertThat(repository.existsById(1)).isFalse();
    }

    @Test
    public void deleteByEntity() {
        List<Role> list = new ArrayList<>(5);
        for (int i = 1; i < 6; i++) {
            Role role = new Role("role" + i);
            list.add(role);
        }
        repository.insertList(list);

        assertThat(repository.count()).isEqualTo(5);

        Example<Role> example = Example.of(new Role("role1"), ExampleMatcher.matching().withIgnoreNullValues());
        long l = repository.deleteAll(example);

        assertThat(l).isEqualTo(1);

        assertThat(repository.count()).isEqualTo(4);
    }

    @Test
    public void deleteByIds() {
        List<Role> list = new ArrayList<>(5);
        for (int i = 1; i < 6; i++) {
            Role role = new Role("role" + i);
            list.add(role);
        }
        repository.insertList(list);

        assertThat(repository.count()).isEqualTo(5);

        repository.deleteAllById(List.of(1,2));

        assertThat(repository.count()).isEqualTo(3);
    }

    @Test
    public void deleteByWeekend() {
        List<Role> list = new ArrayList<>(5);
        for (int i = 1; i < 6; i++) {
            Role role = new Role("role" + i);
            list.add(role);
        }
        repository.insertList(list);

        assertThat(repository.count()).isEqualTo(5);

        Weekend<Role> weekend = Weekend.of(Role.class);
        weekend.weekendCriteria()
                .andIn(Role::getName, "role1", "role3");

        long l = repository.deleteAll(weekend);

        assertThat(l).isEqualTo(2);

        assertThat(repository.count()).isEqualTo(3);

    }

    @Test
    public void updateSelectiveByWeekend() {
        List<Role> list = new ArrayList<>(5);
        for (int i = 1; i < 6; i++) {
            Role role = new Role("role" + i);
            list.add(role);
        }
        repository.insertList(list);

        Role updater = new Role("rolea");

        Weekend<Role> weekend = Weekend.of(Role.class);

        weekend.weekendCriteria()
                .andIn(Role::getName, "role1", "role2");

        long l = repository.updateSelective(updater, weekend);

        assertThat(l).isEqualTo(2);

        List<Role> all = repository.findAll();

        assertThat(all)
                .extracting(Role::getName)
                .contains("rolea","rolea", "role3", "role4", "role5");
    }

    @Test
    public void updateSelectiveByEntity() {
        List<Role> list = new ArrayList<>(5);
        for (int i = 1; i < 6; i++) {
            Role role = new Role("role" + i);
            list.add(role);
        }
        repository.insertList(list);

        Role updater = new Role("rolea");

        Role query = new Role("role3");

        long l = repository.updateSelective(updater, Example.of(query, ExampleMatcher.matching().withIgnoreNullValues()));

        assertThat(l).isEqualTo(1);

        List<Role> all = repository.findAll();

        assertThat(all)
                .extracting(Role::getName)
                .contains("role1","role2", "rolea", "role4", "role5");
    }

    @Test
    public void insertList() {
        List<Role> list = new ArrayList<>(5);
        for (int i = 1; i < 6; i++) {
            Role role = new Role("role" + i);
            list.add(role);
        }
        repository.insertList(list);

        assertThat(repository.count()).isEqualTo(5);

    }
    @Test
    public void insertLongIdList() {

        List<LongRole> longRoles = new ArrayList<>(5);
        for (int i = 1; i < 6; i++) {
            LongRole role = new LongRole("role" + i);
            longRoles.add(role);
        }

        longRoleRepository.insertList(longRoles);

        assertThat(longRoleRepository.count()).isEqualTo(5);
    }
}
