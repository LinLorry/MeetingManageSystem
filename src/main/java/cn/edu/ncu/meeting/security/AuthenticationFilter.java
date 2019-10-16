package cn.edu.ncu.meeting.security;

import cn.edu.ncu.meeting.until.TokenUntil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The Authentication Filter
 * set authentication by token.
 * @author lorry
 * @author lin864464995@163.com
 */
@Component
public class AuthenticationFilter extends OncePerRequestFilter {
    @Value("${Manage.AuthenticationName}")
    private String AuthenticationName;

    private final TokenUntil tokenUntil;

    public AuthenticationFilter(TokenUntil tokenUntil) {
        this.tokenUntil = tokenUntil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String requestTokenHeader = httpServletRequest.getHeader("Authorization");

        if (requestTokenHeader != null && requestTokenHeader.startsWith(AuthenticationName + " ")) {
            String token = requestTokenHeader.substring(AuthenticationName.length() + 1);
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    SecurityContextHolder.getContext().setAuthentication(
                            tokenUntil.getAuthenticationFromToken(token)
                    );
                } catch (ExpiredJwtException e) {
                    logger.warn("JWT Token has expired");
                } catch (SignatureException e) {
                    logger.error("Signature Exception");
                } catch (Exception e) {
                    logger.error(e);
                }
            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
