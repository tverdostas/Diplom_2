package site.stellarburgers.user;

import site.stellarburgers.data.User;

import static site.stellarburgers.utils.Util.randomString;

public class UserGenerator {
    public static User getRandomUser() {

        return new User()
                .setEmail(randomString(9) + "@gmail.com")
                .setPassword(randomString(9))
                .setName(randomString(9));

    }
}
