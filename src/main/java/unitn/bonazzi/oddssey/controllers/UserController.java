package unitn.bonazzi.oddssey.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import unitn.bonazzi.oddssey.pojos.Match;
import unitn.bonazzi.oddssey.pojos.Review;
import unitn.bonazzi.oddssey.pojos.User;
import unitn.bonazzi.oddssey.proxies.PartiteWebProxy;
import unitn.bonazzi.oddssey.repositories.ReviewRepository;
import unitn.bonazzi.oddssey.repositories.UserRepository;

import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

@Controller
public class UserController {
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final PartiteWebProxy partiteWebProxy;
    

    public UserController(UserRepository userRepository, ReviewRepository reviewRepository, PartiteWebProxy partiteWebProxy) {
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.partiteWebProxy = partiteWebProxy;
    }

    @GetMapping("/userDetails")
    public String userDetails(Authentication authentication, Model model) {
        User user = userRepository.userData(authentication.getName());
        model.addAttribute("firstName", user.getFirstName());
        model.addAttribute("lastName", user.getLastName());
        model.addAttribute("birthDate", user.getBirthdate());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("role", user.getRole());
        model.addAttribute("username", authentication.getName());
        model.addAttribute("wins", user.getWins());
        model.addAttribute("rank", user.getRank());
        model.addAttribute("prizesWon", user.getPrizes());
        model.addAttribute("wagers", user.getPlays());

        if(user.getPropic()!=null) {
            String base64Image = Base64.getEncoder().encodeToString(user.getPropic());
            model.addAttribute("propic", "data:image/jpeg;base64," + base64Image);
        }

        return "segments/userActions/userDetails";
    }

    @GetMapping("/changePassword")
    public String changePassword(Model model) {
        model.addAttribute("wrongPassword", false);
        return "segments/userActions/changePassword";
    }

    @GetMapping("/wrongPassword")
    public String wrongPassword(Model model) {
        model.addAttribute("wrongPassword", true);
        return "segments/userActions/changePassword";
    }

    @GetMapping("/matchCalendar")
    public String matchCalendar(Model model, Authentication authentication) {
        String sport = userRepository.getUserSport(authentication.getName());
        model.addAttribute("sport", sport);
        List<Match> matchList = partiteWebProxy.getAllMatches(sport);
        model.addAttribute("matchList", matchList);
        return "segments/userActions/matchCalendar";
    }

    @GetMapping("/wager")
    public String wager(Model model, Authentication authentication) {
        String sport = userRepository.getUserSport(authentication.getName());
        model.addAttribute("sport", sport);
        model.addAttribute("date", LocalDate.now());
        List<Match> matchList = partiteWebProxy.getMatches(sport, LocalDate.now());
        if (matchList.isEmpty()) {
            partiteWebProxy.createMatches(userRepository.getUserSport(authentication.getName()));
            matchList = partiteWebProxy.getMatches(sport, LocalDate.now());
        }
        model.addAttribute("matchList", matchList);
        return "segments/userActions/wager";
    }

    @GetMapping("/review")
    public String review(Authentication authentication, Model model) {
        return "segments/userActions/review";
    }

    @PostMapping("/publishReview")
    public void publishReview(Authentication authentication,
                              @RequestParam int rating,
                              @RequestParam String reviewText){
        reviewRepository.addReview(new Review(reviewText, rating, authentication.getName()));
    }
}
