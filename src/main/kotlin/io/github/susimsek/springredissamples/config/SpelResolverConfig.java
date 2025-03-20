package io.github.susimsek.springredissamples.config;

import io.github.susimsek.springredissamples.spelresolver.DefaultSpelResolver;
import io.github.susimsek.springredissamples.spelresolver.SpelResolver;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.expression.spel.standard.SpelExpressionParser;

@Configuration
public class SpelResolverConfig {
    @Bean
    public SpelResolver spelResolver(SpelExpressionParser spelExpressionParser,
                                     ParameterNameDiscoverer parameterNameDiscoverer,
                                     BeanFactory beanFactory) {
        return new DefaultSpelResolver(spelExpressionParser, parameterNameDiscoverer, beanFactory);
    }

    @Bean
    public SpelExpressionParser spelExpressionParser() {
        return new SpelExpressionParser();
    }

    @Bean
    public ParameterNameDiscoverer parameterNameDiscoverer() {
        return new StandardReflectionParameterNameDiscoverer();
    }
}
