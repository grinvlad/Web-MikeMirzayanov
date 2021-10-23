package ru.itmo.wp.servlet;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MessageServlet extends HttpServlet {

    private static class Message {
        private final String user;
        private final String text;

        private Message(String user, String text) {
            this.user = user;
            this.text = text;
        }
    }


    private static final List<Message> messages = new ArrayList<>();


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        String uri = request.getRequestURI();
        String user;
        String text;
        Object result = null;
        switch(uri) {
            case "/message/auth":
                user = request.getParameter("user");
                if (isValid(user)) {
                    session.setAttribute("user", user);
                    result = user;
                } else {
                    result = session.getAttribute("user");
                }
                break;
            case "/message/add":
                text = request.getParameter("text");
                user = (String) session.getAttribute("user");
                if (isValid(text)) {
                    messages.add(new Message(user, text));
                }
                break;
            case "/message/findAll":
                result = messages;
                break;
        }

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        String json = new Gson().toJson(result);
        PrintWriter printWriter = new PrintWriter(response.getOutputStream(), false, StandardCharsets.UTF_8);
        printWriter.print(json);
        printWriter.flush();
    }


    private static boolean isValid(Object str) {
        return (str != null && !str.toString().isBlank());
    }
}
