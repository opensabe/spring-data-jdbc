package io.github.mado.jdbc.common.test;

import io.github.mado.jdbc.common.test.repository.activity.ActivityRepository;
import io.github.mado.jdbc.common.test.vo.Activity;
import io.github.mado.jdbc.core.EnableJdbcRepositories;
import io.github.mado.jdbc.core.lambda.Weekend;
import io.github.opensabe.common.testcontainers.CustomizedMySQLContainer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.core.convert.QueryMapper;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author heng.ma
 */
@Testcontainers
@SpringBootTest(
        properties = {
                "spring.datasource.username=root",
                "spring.datasource.password=123456",
                "spring.datasource.url=jdbc:mysql://localhost:3306/sys",
        },
        classes = JsonPropertyTest.App.class)
public class JsonPropertyTest {

    @Container
    final static CustomizedMySQLContainer mysql = new CustomizedMySQLContainer() {

        public CustomizedMySQLContainer fixedExposedPort(int hostPort, int containerPort) {
            super.addFixedExposedPort(hostPort, containerPort);
            return this;
        }
    }.fixedExposedPort(3306, 3306);

    @EnableJdbcRepositories("io.github.mado.jdbc.common.test.repository.activity")
    @SpringBootApplication(scanBasePackages = "io.github.mado.jdbc.autoconfigure")
    public static class App {

    }

    @Autowired
    private ActivityRepository repository;

    @Autowired
    private Dialect dialect;
    @Autowired
    private JdbcConverter jdbcConverter;

    @Autowired
    private MappingContext mappingContext;

    @BeforeEach
    void clear () {
        repository.deleteAll();
    }

    @Test
    void testSelect () {
        Activity activity = new Activity();
        activity.setId("1");
        activity.setOnline(true);
        activity.setConfig(new Activity.Config("k1", "v1"));
        activity.setPlatforms(List.of("p1", "p2"));
        activity.setTimes(Map.of("m1", true, "m2", false));


        repository.insertSelective(activity);

        Optional<Activity> byId = repository.findById("1");

        Assertions.assertThat(byId)
                .isPresent()
                .get()
                .extracting(Activity::getPlatforms,
                        Activity::getOnline,
                        a -> a.getConfig().getKey(),
                        a -> a.getConfig().getValue(),
                        a -> a.getTimes().get("m1"),
                        a -> a.getTimes().get("m2")
                ).contains(List.of("p1", "p2"), true,"k1", "v1", true, false);
    }
    @Test
    void testUpdate () {
        Activity activity = new Activity();
        activity.setId("1");
        activity.setOnline(false);
        activity.setConfig(new Activity.Config("k1", "v1"));
        activity.setPlatforms(List.of("p1", "p2"));
        activity.setTimes(Map.of("m1", true, "m2", false));


        repository.insertSelective(activity);

        Optional<Activity> byId = repository.findById("1");


        Assertions.assertThat(byId)
                .isPresent()
                .get()
                .extracting(Activity::getPlatforms,
                        Activity::getOnline,
                        a -> a.getConfig().getKey(),
                        a -> a.getConfig().getValue(),
                        a -> a.getTimes().get("m1"),
                        a -> a.getTimes().get("m2")
                ).contains(List.of("p1", "p2"), false, "k1", "v1", true, false);

        Activity update = new Activity();
        update.setId("1");
        update.setOnline(true);
        update.setConfig(new Activity.Config("k2", "v2"));
        repository.updateByIdSelective(update);

        Optional<Activity> optional = repository.findById("1");


        Assertions.assertThat(optional)
                .isPresent()
                .get()
                .extracting(Activity::getPlatforms,
                        Activity::getOnline,
                        a -> a.getConfig().getKey(),
                        a -> a.getConfig().getValue(),
                        a -> a.getTimes().get("m1"),
                        a -> a.getTimes().get("m2")
                ).contains(List.of("p1", "p2"), true, "k2", "v2", true, false);

    }

    @Test
    void testInsertList () {
        List<Activity> list = new ArrayList<>(10);
        for (int i = 1; i < 11; i++) {
            Activity activity = new Activity();
            activity.setId("1"+i);
            activity.setOnline(true);
            activity.setConfig(new Activity.Config("k1"+i, "v1"+i));
            activity.setPlatforms(List.of("p1"+i, "p2"+i));
            activity.setTimes(Map.of("m1"+1, true, "m2"+i, false));
            list.add(activity);
        }

        repository.insertList(list);

        Assertions.assertThat(repository.count()).isEqualTo(10);

    }

    @Test
    void testQueryMapper () {
        QueryMapper queryMapper = new QueryMapper(dialect, jdbcConverter);
        Weekend<Activity> weekend = Weekend.of(Activity.class);
        weekend.weekendCriteria()
                .andEqualTo(Activity::getId, "1")
                .andIn(Activity::getOnline, false, true);
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        RelationalPersistentEntity entity = (RelationalPersistentEntity) mappingContext.getRequiredPersistentEntity(Activity.class);
        System.out.println(weekend.toQuery().getCriteria().get());
        System.out.println(parameterSource.getValues());
    }
}
