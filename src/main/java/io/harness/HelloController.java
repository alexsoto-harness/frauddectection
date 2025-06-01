package io.harness;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String home() {
        String projectName = "sotoseven";
        String projectDescription = "Demo test";

        return "<html>" +
               "<head>" +
               "<title>Welcome to the " + projectName + " application!</title>" +
               "</head>" +
               "<body>" +
               "<h1>Welcome to the " + projectName + " application!</h1>" +
               "<p>This application was scaffolded and built with the <strong>Harness Internal Developer Portal</strong>.</p>" +
               "<h2>Project Details</h2>" +
               "<ul>" +
               "<li><strong>Project Name:</strong> " + projectName + "</li>" +
               "<li><strong>Project Description:</strong> " + projectDescription + "</li>" +
               "</ul>" +
               "</body>" +
               "</html>";
    }
}
