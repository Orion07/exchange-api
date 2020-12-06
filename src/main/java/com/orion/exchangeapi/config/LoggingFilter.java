package com.orion.exchangeapi.config;

import com.orion.exchangeapi.constants.ProjectConstants;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;
import java.util.UUID;

@Component
public class LoggingFilter extends GenericFilter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String traceId = UUID.randomUUID().toString();
        MDC.put(ProjectConstants.MDC_TRACE_KEY, traceId);

        try {
            chain.doFilter(request, response);
        } catch (Exception e) {

        } finally {
            MDC.clear();
        }
    }
}
