package io.github.mado.jdbc.common.test.vo;

import io.github.mado.jdbc.converter.Converter;
import io.github.mado.jdbc.converter.extension.JsonPropertyValueConverter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;
import java.util.Map;

/**
 * @author heng.ma
 */

@Table("t_activity")
public class Activity {

    @Id
    private String id;

    @Converter(JsonPropertyValueConverter.class)
    private Config config;

    @Converter(JsonPropertyValueConverter.class)
    private List<String> platforms;

    @Converter(JsonPropertyValueConverter.class)
    private Map<String, Boolean> times;

    private Boolean online;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public static class Config {
        private String key;
        private String value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Config(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Config() {
        }
    }
}
