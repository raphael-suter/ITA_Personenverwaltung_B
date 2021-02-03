package ch.axa.ita.personenverwaltung.rest;

import ch.axa.ita.personenverwaltung.model.Message;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static ch.axa.ita.personenverwaltung.utility.Json.toJson;

@Component
public class Filter extends OncePerRequestFilter {
    private static final String URL_PATTERN = "/api/secured/*";
    private static final String AUTHORIZATION_HEADER = "authorization";
    private static final int MIN_LENGTH = 7;
    private static final String CONTENT_TYPE = "application/json";
    private static final String ERROR_MESSAGE = "Du bist nicht berechtigt, auf diese Ressource zuzugreifen.";

    private final API api;

    public Filter(API api) {
        this.api = api;
    }

    @Override
    public void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        if (isAuthorized(httpServletRequest)) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } else {
            denyAccess(httpServletResponse);
        }
    }

    @Bean
    public FilterRegistrationBean<Filter> registerFilter() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(this);
        registrationBean.addUrlPatterns(URL_PATTERN);

        return registrationBean;
    }

    private String getToken(HttpServletRequest httpServletRequest) {
        String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION_HEADER);

        if (authorizationHeader == null || authorizationHeader.isEmpty() || authorizationHeader.length() < MIN_LENGTH) {
            return null;
        }

        return authorizationHeader.substring(MIN_LENGTH);
    }

    private boolean isAuthorized(HttpServletRequest httpServletRequest) {
        return api
                .getUserByToken(getToken(httpServletRequest))
                .isPresent();
    }

    private void denyAccess(HttpServletResponse httpServletResponse) throws IOException {
        String json = toJson(new Message(ERROR_MESSAGE));

        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpServletResponse.setContentType(CONTENT_TYPE);

        httpServletResponse
                .getWriter()
                .write(json);

        httpServletResponse.flushBuffer();
    }
}
