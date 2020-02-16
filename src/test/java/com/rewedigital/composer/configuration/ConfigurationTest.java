package com.rewedigital.composer.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.rewedigital.composer.configuration.DefaultConfiguration;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ConfigurationTest {
    
    @Test
    public void configurationIsComplete() {
        final Config config = ConfigFactory.load("composer.conf");
        config.checkValid(DefaultConfiguration.defaultConfiguration(), "composer");
        assertThat(config.isEmpty()).isFalse();
    }

}
