package ru.itmo.wp.web.page;

import ru.itmo.wp.web.exception.RedirectException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class IndexPage {
    private void action(Map<String, Object> view) {
        view.put("name", "geranazavr555");
    }

    private void butt(Map<String, Object> view) {
//        view.put("name", "Butt");
        throw new RedirectException("/ticTacToe/action=onMove&cell_12=+");
    }


    private void action(HttpServletRequest request, Map<String, Object> view) {
        view.put("name", "geranazavr555");
    }
}
