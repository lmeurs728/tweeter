package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.UserResponse;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.UserDAODynamo;
import org.junit.Assert;
import org.junit.Test;

public class UserDAODynamoTest {
    private final UserDAO userDAO = new UserDAODynamo();
    static String[] handles = {"@BarackObama", "@justinbieber", "@katyperry", "@rihanna", "@cristiano", "@taylorswift13",
            "@realDonalTrump", "@ArianaGrande", "@ladygaga", "@TheEllenShow", "@elonmusk",
            "@narendramodi", "@YouTube", "@KimKardashian", "@selenagomez", "@jtimberlake", "@cnnbrk",
            "@Twitter", "@BillGates", "@cnn", "@neymarjr", "@britneyspears", "@ddlovato", "@nasa"};
    static String[] names = {"Barack Obama", "Justin Bieber", "Katy Perry", "Rihanna", "Cristiano Ronaldo", "Taylor Swift",
            "Donald Trump", "Ariana Grande", "Lady Gaga", "Ellen DeGeneres", "Elon Musk",
            "Narendra Modi", "YouTube", "Kim Kardashian", "Selena Gomez", "Justin Timberlake", "CNN Breaking News",
            "Twitter", "Bill Gates", "CNN", "Neymar", "Britney Spears", "Demi Lovato", "Nasa"};

    @Test
    public void registerEveryone() {
        for (int i = 0; i < handles.length; i++) {
            User user = userDAO.register(new RegisterRequest(names[i].split(" ")[0],
                            names[i].split(" ").length >= 2 ? names[i].split(" ")[1] : "",
                            handles[i], "password", null),
                    "https://lances-tweeter-images.s3.us-west-1.amazonaws.com/moose.png");
            System.out.println(handles[i]);
        }

    }

    @Test
    public void register() {
        User lance = userDAO.register(new RegisterRequest("Lance", "Meurs", "@lmeurs", "password", null),
                "https://lances-tweeter-images.s3.us-west-1.amazonaws.com/%40lmeurs.png");
        System.out.println(lance);
        Assert.assertNotNull(lance);
    }

    @Test
    public void login() {
        try {
            User lance = userDAO.login(new LoginRequest("@lmeurs", "password"));
            Assert.assertNotNull(lance);
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
            Assert.fail();
        }

        try {
            userDAO.login(new LoginRequest("@lmeurs", "password69"));
            Assert.fail();
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void getUser() {
        UserResponse resp = userDAO.getUser(new GetUserRequest("@lmeurs"));
        Assert.assertTrue(resp.isSuccess());
    }
}
