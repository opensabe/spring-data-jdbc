package io.github.opensabe.jdbc.common.test.json;

import io.github.opensabe.jdbc.common.test.BaseTest;
import io.github.opensabe.jdbc.common.test.json.repository.ActivityRepository;
import io.github.opensabe.jdbc.common.test.json.service.ActivityService;
import io.github.opensabe.jdbc.common.test.vo.Activity;
import io.github.opensabe.jdbc.core.EnableJdbcRepositories;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author heng.ma
 */

@Import(ActivityService.class)
@EnableJdbcRepositories(basePackageClasses = ActivityRepository.class)
public class JsonPropertyTest extends BaseTest {



    @Autowired
    private ActivityRepository repository;

    @Autowired
    private ActivityService service;

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
    void testCustomerSQL () {
        Activity activity = new Activity();
        activity.setId("1");
        activity.setOnline(true);
        activity.setConfig(new Activity.Config("k1", "v1"));
        activity.setPlatforms(List.of("p1", "p2"));
        activity.setTimes(Map.of("m1", true, "m2", false));


        repository.insertSelective(activity);
        List<ActivityRepository.ActivityRecord> activityRecords = repository.selectActivities();

        Assertions.assertThat(activityRecords)
                .hasSize(1)
                .first()
                .extracting(ActivityRepository.ActivityRecord::getPlatforms,
                        a -> a.getConfig().getKey(),
                        a -> a.getConfig().getValue(),
                        a -> a.getTimes().get("m1"),
                        a -> a.getTimes().get("m2")
                ).contains(List.of("p1", "p2"), "k1", "v1", true, false);
    }
}
