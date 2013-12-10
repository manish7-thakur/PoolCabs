/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.security;

import com.poolcabs.ui.control.UserLoginController;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Manish
 */
@WebFilter("/booking/Create.jsf")
public class LoginFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        UserLoginController loginController = (UserLoginController) req.getSession().getAttribute("userLoginController");
        if (loginController != null && loginController.isLoggedIn()) {
            chain.doFilter(request, response);
        } else {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.sendRedirect(req.getContextPath() + "/login/signin.jsf");
        }
    }

    @Override
    public void destroy() {
    }
}
