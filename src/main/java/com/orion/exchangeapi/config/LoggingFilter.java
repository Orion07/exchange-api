package com.orion.exchangeapi.config;

import com.orion.exchangeapi.constants.ProjectConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.GenericFilter;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.UUID;

@Component
public class LoggingFilter extends GenericFilter {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        String traceId = UUID.randomUUID().toString();
        MDC.put(ProjectConstants.MDC_TRACE_KEY, traceId);

        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            LOG.error("::doFilter traceId:{}", traceId, e);
        } finally {
            MDC.clear();
        }
    }
}
