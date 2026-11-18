package kh.edu.istad.iam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
@Controller
public class IamServiceApplication {

    @GetMapping("/login")
    public String login(Model model){
        return "login";
    }

    @GetMapping("/")
    public String index(Authentication authentication, Model model){
        String name;
        String email = null;
        String avatar = null;

        var principle = authentication.getPrincipal();

        switch (principle) {
            case OidcUser oidc -> {
                name = oidc.getFullName() != null ? oidc.getFullName() : oidc.getName();
                email = oidc.getEmail();
                avatar = oidc.getPicture();
            }
            case OAuth2User oauth -> {
                String fullName = oauth.getAttribute("name");
                String login = oauth.getAttribute("login");
                name = fullName != null ? fullName : login;
                email = oauth.getAttribute("email");
                avatar = oauth.getAttribute("avatar_url");
            }
            case UserDetails user -> name = user.getUsername();
            case null, default -> name = authentication.getName();
        }

        model.addAttribute("name", name);
        model.addAttribute("email", email);
        model.addAttribute("avatar", avatar);
        model.addAttribute("authorities", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList());

        return "index";
    }

    @GetMapping("/logout")
    public String logout(){
        return "logout";
    }

    public static void main(String[] args) {
        SpringApplication.run(IamServiceApplication.class, args);
    }

}
