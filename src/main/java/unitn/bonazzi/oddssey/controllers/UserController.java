package unitn.bonazzi.oddssey.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import unitn.bonazzi.oddssey.repositories.PlaysRepository;
import unitn.bonazzi.oddssey.requestBodies.ChangePasswordRequest;
import unitn.bonazzi.oddssey.pojos.Match;
import unitn.bonazzi.oddssey.pojos.Review;
import unitn.bonazzi.oddssey.pojos.User;
import unitn.bonazzi.oddssey.proxies.PartiteWebProxy;
import unitn.bonazzi.oddssey.repositories.ReviewRepository;
import unitn.bonazzi.oddssey.repositories.UserRepository;
import unitn.bonazzi.oddssey.requestBodies.WagerRequest;

import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

@Controller
public class UserController {
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final PartiteWebProxy partiteWebProxy;
    private final PlaysRepository playsRepository;
    

    public UserController(UserRepository userRepository, ReviewRepository reviewRepository, PartiteWebProxy partiteWebProxy, PlaysRepository playsRepository) {
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.partiteWebProxy = partiteWebProxy;
        this.playsRepository = playsRepository;
    }

    // show user informations
    @GetMapping("/userDetails")
    public String userDetails(Authentication authentication, Model model) {
        User user = userRepository.userData(authentication.getName());
        model.addAttribute("id", user.getId());
        model.addAttribute("firstName", user.getFirstName());
        model.addAttribute("lastName", user.getLastName());
        model.addAttribute("birthDate", user.getBirthdate());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("role", user.getRole());
        model.addAttribute("username", authentication.getName());
        model.addAttribute("sport", user.getSport());
        model.addAttribute("squadra", user.getTeam());
        model.addAttribute("prizesWon", user.getPrizes());
        model.addAttribute("wagers", user.getPlays());
        model.addAttribute("points", user.getPoints());
        model.addAttribute("rank", user.getRank());

        if(user.getPropic()!=null) {
            String base64Image = Base64.getEncoder().encodeToString(user.getPropic());
            model.addAttribute("propic", "data:image/jpeg;base64," + base64Image);
        }

        return "segments/userActions/userDetails";
    }

    // change password form
    @GetMapping("/changePassword")
    public String changePassword(Model model) {
        model.addAttribute("wrongPassword", false);
        model.addAttribute("passwordChanged", false);
        return "segments/userActions/changePassword";
    }

    // show the calendar of matches
    @GetMapping("/matchCalendar")
    public String matchCalendar(Model model, Authentication authentication) {
        String sport = userRepository.getUserSport(authentication.getName());
        model.addAttribute("sport", sport);
        List<Match> matchList = partiteWebProxy.getAllMatches(sport);
        model.addAttribute("matchList", matchList);
        return "segments/userActions/matchCalendar";
    }

    // show the matches of the day
    // this is the page where the user can place a wager
    @GetMapping("/wager")
    public String wager(Model model, Authentication authentication) {
        String html;
        if(!playsRepository.userPlayed(authentication.getName())){
            String sport = userRepository.getUserSport(authentication.getName());
            model.addAttribute("sport", sport);
            model.addAttribute("date", LocalDate.now());
            model.addAttribute("done", false);
            List<Match> matchList = partiteWebProxy.getMatches(sport, LocalDate.now());
            if (matchList.isEmpty()) {
                partiteWebProxy.createMatches(userRepository.getUserSport(authentication.getName()));
                matchList = partiteWebProxy.getMatches(sport, LocalDate.now());
            }
            model.addAttribute("matchList", matchList);
            html = "segments/userActions/wager";
        }else{
            model.addAttribute("points", playsRepository.getTodaysPoints(authentication.getName()));
            html = "segments/userActions/wagerDone";
        }
        return html;
    }

    // show the review form
    // if the user has already published a review, it will be shown in the form
    @GetMapping("/review")
    public String review(Authentication authentication, Model model) {
        if(reviewRepository.userReviewed(authentication.getName())){
            model.addAttribute("reviewPublished", true);
            Review review = reviewRepository.getUserReview(authentication.getName());
            model.addAttribute("startingRating", review.getRating());
            model.addAttribute("oldReviewText", review.getText());
            model.addAttribute("oldReviewDate", review.getDate());
        } else {
            model.addAttribute("reviewPublished", false);
            model.addAttribute("startingRating", 5);
        }
        return "segments/userActions/review";
    }

    // publish the review
    @PostMapping("/publishReview")
    public String publishReview(Authentication authentication,
                                @RequestParam int rating,
                                @RequestParam String reviewText,
                                Model model){
        if(reviewRepository.userReviewed(authentication.getName())){
            reviewRepository.updateReview(new Review(reviewText, rating, authentication.getName()));
        } else {
            reviewRepository.addReview(new Review(reviewText, rating, authentication.getName()));
        }
        model.addAttribute("reviewPublished", true);
        return "segments/userActions/review";
    }

    // saves the new password if possible
    // shows a message if the old password is wrong
    @PostMapping("/setNewPassword")
    public String setNewPassword(Authentication authentication,
                                 @RequestBody ChangePasswordRequest passwords,
                                 Model model) {
        boolean result = userRepository.changePassword(authentication.getName(), passwords.getNewPassword(), passwords.getOldPassword());
        model.addAttribute("passwordChanged", result);
        model.addAttribute("wrongPassword", !result);
        return "segments/userActions/changePassword";
    }

    // get the results of the matches of the day
    // compares them with the predictions of the user
    // shows the results and the points won
    @PostMapping("/getResults")
    public String getResults(@RequestBody WagerRequest requestBody, Authentication authentication, Model model){

        String sport = userRepository.getUserSport(authentication.getName());

        List<Integer> predictions = requestBody.getPredictions();
        List<Integer> results = partiteWebProxy.getResults(sport, LocalDate.now());
        List<Boolean> confirmations = new java.util.ArrayList<>();
        int points = 0;
        for (int i = 0; i < results.size(); i++) {
            if (predictions.get(i).equals(results.get(i))) {
                confirmations.add(true);
                points++;
            } else {
                confirmations.add(false);
            }
        }
        playsRepository.addPlay(authentication.getName(), points);

        model.addAttribute("sport", sport);
        model.addAttribute("date", LocalDate.now());
        model.addAttribute("done", true);
        model.addAttribute("confirmations", confirmations);

        List<Match> matchList = partiteWebProxy.getMatches(sport, LocalDate.now());
        if (matchList.isEmpty()) {
            partiteWebProxy.createMatches(userRepository.getUserSport(authentication.getName()));
            matchList = partiteWebProxy.getMatches(sport, LocalDate.now());
        }
        model.addAttribute("matchList", matchList);
        return "segments/userActions/wager";
    }
}
