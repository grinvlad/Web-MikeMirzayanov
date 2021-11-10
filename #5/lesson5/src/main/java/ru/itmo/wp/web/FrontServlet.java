package ru.itmo.wp.web;

import freemarker.template.*;
import ru.itmo.wp.web.exception.NotFoundException;
import ru.itmo.wp.web.exception.RedirectException;
import ru.itmo.wp.web.page.IndexPage;
import ru.itmo.wp.web.page.NotFoundPage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class FrontServlet extends HttpServlet {
    private static final List<Class<?>> METHODS_TO_MATCH = Arrays.asList(Map.class, HttpServletRequest.class);
    private static final String BASE_PACKAGE = FrontServlet.class.getPackage().getName() + ".page";
    private static final String DEFAULT_ACTION = "action";
    private Configuration sourceConfiguration;
    private Configuration targetConfiguration;

    private Configuration newFreemarkerConfiguration(String templateDirName, boolean debug)
            throws ServletException {
        File templateDir = new File(templateDirName);
        if (!templateDir.isDirectory()) {
            return null;
        }

        Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
        try {
            configuration.setDirectoryForTemplateLoading(templateDir);
        } catch (IOException e) {
            throw new ServletException("Can't create freemarker configuration [templateDir="
                    + templateDir + "]");
        }
        configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
        configuration.setTemplateExceptionHandler(debug ? TemplateExceptionHandler.HTML_DEBUG_HANDLER :
                TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setLogTemplateExceptions(false);
        configuration.setWrapUncheckedExceptions(true);

        return configuration;
    }

    @Override
    public void init() throws ServletException {
        sourceConfiguration = newFreemarkerConfiguration(
                getServletContext().getRealPath("/") + "../../src/main/webapp/WEB-INF/templates", true);
        targetConfiguration = newFreemarkerConfiguration(
                getServletContext().getRealPath("WEB-INF/templates"), false);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        process(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        process(request, response);
    }

    private void process(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Route route = Route.newRoute(request);
        try {
            process(route, request, response);
        } catch (NotFoundException e) {
            try {
                process(Route.newNotFoundRoute(), request, response);
            } catch (NotFoundException notFoundException) {
                throw new ServletException(notFoundException);
            }
        }
    }

    private void process(Route route, HttpServletRequest request, HttpServletResponse response)
            throws NotFoundException, ServletException, IOException {
        Class<?> pageClass;

        try {
            pageClass = Class.forName(route.getClassName());
        } catch (ClassNotFoundException e) {
            throw new NotFoundException();
        }

        Method method = findMethod(pageClass, route.getAction());
        method.setAccessible(true);

        Object page;
        try {
            page = pageClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ServletException("Can't create page [pageClass="
                    + pageClass + "]");
        }

        Map<String, Object> view = new HashMap<>();
        try {
            Class<?>[] parameterTypes = method.getParameterTypes();
            Object[] parameters = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                if (parameterTypes[i] == HttpServletRequest.class) {
                    parameters[i] = request;
                }
                if (parameterTypes[i] == Map.class) {
                    parameters[i] = view;
                }
            }
            method.invoke(page, parameters);
        } catch (IllegalAccessException e) {
            throw new ServletException("Can't invoke action method [pageClass="
                    + pageClass + ", method=" + method + "]");
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RedirectException) {
                RedirectException redirectException = (RedirectException) cause;
                response.sendRedirect(redirectException.getTarget());
                return;
            } else {
                throw new ServletException("Can't invoke action method [pageClass="
                        + pageClass + ", method=" + method + "]", cause);
            }
        }

        setLocaleInSession(request);

        HttpSession session = request.getSession();
        Template template;
        if (session.getAttribute("language") == null) {
            template = newTemplate(pageClass.getSimpleName() + ".ftlh");
        } else {
            try {
                String lang = (String) session.getAttribute("language");
                template = newTemplate(pageClass.getSimpleName() + "_" + lang + ".ftlh");
            } catch (ServletException e) {
                template = newTemplate(pageClass.getSimpleName() + ".ftlh");
            }
        }

        response.setContentType("text/html");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try {
            template.process(view, response.getWriter());
        } catch (TemplateException e) {
            if (sourceConfiguration == null) {
                throw new ServletException("Can't render template [pageClass="
                        + pageClass + ", action=" + method + "]", e);
            }
        }
    }

    private void setLocaleInSession(HttpServletRequest request) {
        String langPattern = "[a-z]{2}";
        if (request.getParameterMap().containsKey("lang")) {
            String value = request.getParameter("lang");
            if (value.matches(langPattern)) {
                request.getSession().setAttribute("language", value);
            }
        }
    }

    private Method findMethod(Class<?> pageClass, String action) throws NotFoundException {
        ArrayList<Method> candidates = new ArrayList<>();
        for (Class<?> clazz = pageClass; candidates.isEmpty() && clazz != null; clazz = clazz.getSuperclass()) {
            for (Method curMethod : clazz.getDeclaredMethods()) {
                if (isAppropriateMethod(curMethod, action)) {
                    candidates.add(curMethod);
                }
            }
        }
        Method method = null;
        int maxLength = -1;
        for (Method candidate : candidates) {
            if (candidate.getParameterTypes().length > maxLength) {
                maxLength = candidate.getParameterTypes().length;
                method = candidate;
            }
        }
        if (method == null) {
            throw new NotFoundException();
        }
        return method;
    }

    private boolean isAppropriateMethod(Method method, String action) {
        return method.getName().equals(action) &&
               method.getReturnType() == void.class &&
               METHODS_TO_MATCH.containsAll(Arrays.asList(method.getParameterTypes()));
    }

    private Template newTemplate(String templateName) throws ServletException {
        Template template = null;

        if (sourceConfiguration != null) {
            try {
                template = sourceConfiguration.getTemplate(templateName);
            } catch (TemplateNotFoundException ignored) {
                // No operations.
            } catch (IOException e) {
                throw new ServletException("Can't load template [templateName=" + templateName + "]", e);
            }
        }

        if (template == null && targetConfiguration != null) {
            try {
                template = targetConfiguration.getTemplate(templateName);
            } catch (TemplateNotFoundException ignored) {
                // No operations.
            } catch (IOException e) {
                throw new ServletException("Can't load template [templateName=" + templateName + "]", e);
            }
        }

        if (template == null) {
            throw new ServletException("Can't find template [templateName=" + templateName + "]");
        }

        return template;
    }

    private static class Route {
        private final String className;
        private final String action;

        private Route(String className, String action) {
            this.className = className;
            this.action = action;
        }

        private String getClassName() {
            return className;
        }

        private String getAction() {
            return action;
        }

        private static Route newNotFoundRoute() {
            return new Route(NotFoundPage.class.getName(), DEFAULT_ACTION);
        }

        private static Route newIndexRoute() {
            return new Route(IndexPage.class.getName(), DEFAULT_ACTION);
        }

        // /misc/help -> ru.itmo.wp.web.page.misc.HelpPage
        private static Route newRoute(HttpServletRequest request) {
            String uri = request.getRequestURI();

            List<String> uriParts = Arrays.stream(uri.split("/"))
                    .filter(part -> !part.isEmpty())
                    .collect(Collectors.toList());

            if (uriParts.isEmpty()) {
                return newIndexRoute();
            }

            StringBuilder simpleClassName = new StringBuilder(uriParts.get(uriParts.size() - 1));
            int lastDotIndex = simpleClassName.lastIndexOf(".");
            simpleClassName.setCharAt(lastDotIndex + 1,
                    Character.toUpperCase(simpleClassName.charAt(lastDotIndex + 1)));
            uriParts.set(uriParts.size() - 1, simpleClassName.toString());

            String className = BASE_PACKAGE + "." + String.join(".", uriParts) + "Page";

            String action = request.getParameter("action");
            if (action == null || action.isEmpty()) {
                action = DEFAULT_ACTION;
            }

            return new Route(className, action);
        }
    }
}
