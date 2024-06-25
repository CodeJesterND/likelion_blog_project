package com.techit.domains.user.interceptor;

import com.techit.standard.util.ConstValues;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler
    ) throws Exception {

        String requestURI = request.getRequestURI();

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute(ConstValues.SESSION_LOGIN_USER) == null) {
            response.sendRedirect("/login?redirectURL=" + requestURI);
            log.info("요청된 requestURI는? " + requestURI);
            return false;
        }

        return true;
    }
}