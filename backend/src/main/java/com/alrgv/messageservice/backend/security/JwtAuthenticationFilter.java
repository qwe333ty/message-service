package com.alrgv.messageservice.backend.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    @Qualifier("userDetailsServiceImpl")
    private UserDetailsService userDataService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().contains("/token/") || request.getRequestURI().endsWith("/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader(JwtTokenUtil.HEADER_STRING);

        String login = null;
        String authToken = null;
        if(header != null && header.startsWith(JwtTokenUtil.TOKEN_PREFIX)) {
            authToken = header.replace(JwtTokenUtil.TOKEN_PREFIX, "");
            try {
                login = jwtTokenUtil.getUsernameFromToken(authToken);
            }catch (IllegalArgumentException e) {
                logger.error("An error occured during getting login from token", e);
            }catch (ExpiredJwtException e) {
                logger.error("The token is expired and not valid anymore", e);
            }catch (SignatureException e){
                logger.error("Authentication Failed. Login or Password not valid");
            }
        }else {
            logger.warn("Couldn't find bearer string, will ignore the header");
        }
        if(login != null && SecurityContextHolder.getContext().getAuthentication()
                == null) {
            UserDetails userDetails = userDataService.loadUserByUsername(login);
            if(jwtTokenUtil.validateToken(authToken, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken = new
                        UsernamePasswordAuthenticationToken(userDetails, null,
                        userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource()
                        .buildDetails(request));
                logger.info("authenticated user " + login + ",setting security context");
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
