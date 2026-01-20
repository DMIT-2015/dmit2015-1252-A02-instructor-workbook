package dmit2015.faces;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.validation.constraints.NotBlank;
import net.datafaker.Faker;
import org.omnifaces.util.Messages;

@Named("helloRequest")
@RequestScoped
public class HelloBean {

    @NotBlank(message = "Username is required.")
    private String userInput;

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    public String getMessage() {
        return "Hello, " + userInput;
    }

    public String submit() {

        var faker = new Faker();
        Messages.addGlobalInfo("Your favorite pokemon is {0}",
                faker.pokemon().name());
        return null; // or navigation outcome
    }
}