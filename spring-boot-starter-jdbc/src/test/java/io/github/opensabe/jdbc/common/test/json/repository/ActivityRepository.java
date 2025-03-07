package io.github.opensabe.jdbc.common.test.json.repository;


import io.github.opensabe.jdbc.common.test.vo.Activity;
import io.github.opensabe.jdbc.converter.Converter;
import io.github.opensabe.jdbc.converter.extension.JsonPropertyValueConverter;
import io.github.opensabe.jdbc.core.repository.BaseRepository;
import org.springframework.data.jdbc.repository.query.Query;

import java.util.List;
import java.util.Map;

public interface ActivityRepository extends BaseRepository<Activity, String> {


    @Query("select id activity_id, config, platforms, times from t_activity")
    List<ActivityRecord> selectActivities ();

    class ActivityRecord {
        private String activityId;
        private @Converter(JsonPropertyValueConverter.class) Config config;
        private @Converter(JsonPropertyValueConverter.class) List<String> platforms;
        private @Converter(JsonPropertyValueConverter.class) Map<String, Boolean> times;

        public String getActivityId() {
            return activityId;
        }

        public void setActivityId(String activityId) {
            this.activityId = activityId;
        }

        public Config getConfig() {
            return config;
        }

        public void setConfig(Config config) {
            this.config = config;
        }

        public List<String> getPlatforms() {
            return platforms;
        }

        public void setPlatforms(List<String> platforms) {
            this.platforms = platforms;
        }

        public Map<String, Boolean> getTimes() {
            return times;
        }

        public void setTimes(Map<String, Boolean> times) {
            this.times = times;
        }
    }

    class Config  {
        private String key;
        private String value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

}
