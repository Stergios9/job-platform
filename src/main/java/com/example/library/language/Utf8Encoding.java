package com.example.library.language;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;

import jakarta.servlet.Filter;

@Configuration
public class Utf8Encoding {

    @Bean
    public Filter encodingFilter() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);

        return (Filter) filter;
    }
}