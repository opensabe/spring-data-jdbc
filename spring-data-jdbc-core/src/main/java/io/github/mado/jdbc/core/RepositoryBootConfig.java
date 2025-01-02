package io.github.mado.jdbc.core;

import org.springframework.data.repository.config.RepositoryConfigurationSource;

public interface RepositoryBootConfig {

    RepositoryConfigurationSource getConfig ();
}
