package ch.axa.ita.personenverwaltung.rest;

import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogFilter extends OncePerRequestFilter {
    @Override
    public void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("LogFilter");
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
