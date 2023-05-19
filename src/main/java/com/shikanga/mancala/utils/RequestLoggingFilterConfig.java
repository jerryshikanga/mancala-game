package com.shikanga.mancala.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class RequestLoggingFilterConfig {

    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(false);
        filter.setIncludePayload(false);
        filter.setMaxPayloadLength(10000);
        filter.setAfterMessagePrefix("REQUEST DATA: ");
        filter.setIncludeClientInfo(true);
        filter.setIncludeHeaders(true);
        return filter;
    }
}