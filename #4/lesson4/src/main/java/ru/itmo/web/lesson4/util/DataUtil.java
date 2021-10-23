package ru.itmo.web.lesson4.util;

import ru.itmo.web.lesson4.model.Post;
import ru.itmo.web.lesson4.model.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DataUtil {
    private static final List<User> USERS = Arrays.asList(
            new User(1, "MikeMirzayanov", "Mike Mirzayanov"),
            new User(6, "pashka", "Pavel Mavrin"),
            new User(9, "geranazarov555", "Georgiy Nazarov"),
            new User(11, "tourist", "Gennady Korotkevich")
    );

    private static final List<Post> POSTS = Arrays.asList(
            new Post(5, "What a great day!",
                    "alskfjdsalfjksafl;j;aslfjlsadjfljjыфваыфваыфаывфваalskfjdsalfjksafl;j;aslfjlsadjfljjыфваыфваыфаывфваalskfjdsalfjksafl;j;aslfjlsadjfljjыфваыфваыфаывфваalskfjdsalfjksafl;j;aslfjlsadjfljjыфваыфваыфаывфваalskfjdsalfjksafl;j;aslfjlsadjfljjыфваыфваыфаывфваalskfjdsalfjksafl;j;aslfjlsadjfljjыфваыфваыфаывфваalskfjdsalfjksafl;j;aslfjlsadjfljjыфваыфваыфаывфваalskfjdsalfjksafl;j;aslfjlsadjfljjыфваыфваыфаывфваalskfjdsalfjksafl;j;aslfjlsadjfljjыфваыфваыфаывфва"
                    , 1),
            new Post(7, "Random", "ojdgpnsjboejrbnpeoibnieqrnbn", 11),
            new Post(43, "123", "lkvxckjlxzvjkzxvljkvxlzxvjl", 6),
            new Post(12, "pop Post", "1232133123123123", 9),
            new Post(6, "mmm", "sdfadsfsadfsdfsadf", 6)
    );


    public static void addData(HttpServletRequest request, Map<String, Object> data) {
        data.put("users", USERS);
        data.put("posts", POSTS);
        for (User user : USERS) {
            if (Long.toString(user.getId()).equals(request.getParameter("logged_user_id"))) {
                data.put("user", user);
            }
        }
    }
}
