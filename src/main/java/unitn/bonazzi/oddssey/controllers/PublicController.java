package unitn.bonazzi.oddssey.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import unitn.bonazzi.oddssey.pojos.News;
import unitn.bonazzi.oddssey.pojos.User;
import unitn.bonazzi.oddssey.proxies.PartiteWebProxy;
import unitn.bonazzi.oddssey.repositories.NewsRepository;
import unitn.bonazzi.oddssey.repositories.ReviewRepository;
import unitn.bonazzi.oddssey.repositories.UserRepository;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@Controller
public class PublicController {
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final NewsRepository newsRepository;
    private final PartiteWebProxy partiteWebProxy;

    public PublicController(UserRepository userRepository, PartiteWebProxy partiteWebProxy,
                            ReviewRepository reviewRepository, NewsRepository newsRepository) {
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.partiteWebProxy = partiteWebProxy;
        this.newsRepository = newsRepository;
    }

    @GetMapping("/index")
    public String index(Authentication authentication, Model model) {
        boolean isLoggedIn = authentication != null && authentication.isAuthenticated();
        model.addAttribute("logged", isLoggedIn);
        return "public/index";
    }

    @GetMapping("/rowing")
    public String rowing(Authentication authentication, Model model) {
        boolean isLoggedIn = authentication != null && authentication.isAuthenticated();
        model.addAttribute("logged", isLoggedIn);
        model.addAttribute("teamList", partiteWebProxy.getTeams("Canottaggio"));
        return "public/sports/rowing";
    }

    @GetMapping("/rafting")
    public String rafting(Authentication authentication, Model model) {
        boolean isLoggedIn = authentication != null && authentication.isAuthenticated();
        model.addAttribute("logged", isLoggedIn);
        model.addAttribute("teamList", partiteWebProxy.getTeams("Rafting"));
        return "public/sports/rafting";
    }

    @GetMapping("/dragonBoatRacing")
    public String dragonBoatRacing(Authentication authentication, Model model) {
        boolean isLoggedIn = authentication != null && authentication.isAuthenticated();
        model.addAttribute("logged", isLoggedIn);
        model.addAttribute("teamList", partiteWebProxy.getTeams("Dragon Boat Racing"));
        return "public/sports/dragonBoatRacing";
    }

    @GetMapping("/reviews")
    public String reviews(Authentication authentication, Model model) {
        boolean isLoggedIn = authentication != null && authentication.isAuthenticated();
        model.addAttribute("logged", isLoggedIn);
        model.addAttribute("reviews", reviewRepository.getAllReviews());
        model.addAttribute("averageRating", reviewRepository.getAverageRating());
        return "public/reviews";
    }

    @GetMapping("/sponsors")
    public String sponsors(Authentication authentication, Model model) {
        boolean isLoggedIn = authentication != null && authentication.isAuthenticated();
        model.addAttribute("logged", isLoggedIn);
        return "public/sponsors";
    }

    @GetMapping("/signup")
    public String signup(Model model) throws JsonProcessingException {
        model.addAttribute("logged", false);
        model.addAttribute("teamList", new ObjectMapper().writeValueAsString(partiteWebProxy.getAllTeams()));
        return "userManagement/signup";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("logged", false);
        return "userManagement/login";
    }

    @PostMapping("/loginFailure")
    public String loginFailure(Model model) {
        model.addAttribute("logged", false);
        model.addAttribute("loginFailed", true);
        return "userManagement/login";
    }

    @PostMapping("/addUser")
    public String addUser(@RequestParam String firstName,
                          @RequestParam String lastName,
                          @RequestParam String birthdate,
                          @RequestParam MultipartFile profilePicture,
                          @RequestParam String email,
                          @RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String sport,
                          @RequestParam String team,
                          Model model) throws ParseException, IOException {
        String returnPage;
        if (userRepository.userExists(username)) {
            model.addAttribute("usernameTaken", true);
            returnPage = "userManagement/signup";
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            byte[] pictureBytes = profilePicture != null && !profilePicture.isEmpty() ? profilePicture.getBytes() : null;
            userRepository.addUser(new User(firstName, lastName, formatter.parse(birthdate), email, username, password, sport, team, "ROLE_USER", pictureBytes));
            model.addAttribute("logged", true);
            returnPage = "userManagement/signupSuccessful";
        }
        return returnPage;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        String returnPage;
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            returnPage = "forward:adminDashboard";
        } else {
            returnPage = "forward:userDashboard";
        }
        return returnPage;
    }

    // Dashboards
    @GetMapping("/userDashboard")
    public String userDashboard(Authentication authentication, Model model) {
        model.addAttribute("logged", true);
        model.addAttribute("username", authentication.getName());
        return "dashboards/userDashboard";
    }

    @GetMapping("/adminDashboard")
    public String adminDashboard(Authentication authentication, Model model) {
        model.addAttribute("logged", true);
        model.addAttribute("username", authentication.getName());
        return "dashboards/adminDashboard";
    }

    @GetMapping("/perform_logout")
    public String logoutPage(Model model) {
        model.addAttribute("logged", true);
        return "userManagement/logoutPage";
    }

    @GetMapping("/logout")
    public String logout(Authentication authentication) {
        authentication.setAuthenticated(false);
        return "public/index";
    }

    @GetMapping(value = "/news", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<News> news(){
        List<News> result = newsRepository.getAllNews();
        if (result.isEmpty()) {
                newsRepository.createStandardNews();
                result = newsRepository.getAllNews();
        }
        return result;
    }
}
