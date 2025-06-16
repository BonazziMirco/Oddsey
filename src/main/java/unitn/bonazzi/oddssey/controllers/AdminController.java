package unitn.bonazzi.oddssey.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import unitn.bonazzi.oddssey.pojos.User;
import unitn.bonazzi.oddssey.repositories.UserRepository;
import unitn.bonazzi.oddssey.services.AssegnaPremi;

import java.util.List;
import java.util.Map;

@Controller
public class AdminController {
    private final UserRepository userRepository;
    private final AssegnaPremi assegnaPremi;

    public AdminController(UserRepository userRepository, AssegnaPremi assegnaPremi) {
        this.userRepository = userRepository;
        this.assegnaPremi = assegnaPremi;
    }

    // show the list of all accounts
    @GetMapping("/userList")
    public String userList(Model model) {
        model.addAttribute("userList", userRepository.findAllUsers());
        return "segments/adminActions/userList";
    }

    // show the ranking list
    @GetMapping("/rankingList")
    public String rankingList(Model model) {
        model.addAttribute("userList", userRepository.getRankingList());
        return "segments/adminActions/rankingList";
    }

    // show the first three users in the ranking list
    // allows the admin to assign prizes to the top three users
    @GetMapping("/assegnaPremi")
    public String assegnaPremi(Model model) {
        List<User> rankingList = userRepository.getRankingList();
        model.addAttribute("user1", rankingList.getFirst().getUsername());
        model.addAttribute("user2", rankingList.get(1).getUsername());
        model.addAttribute("user3", rankingList.get(2).getUsername());
        return "segments/adminActions/prizeAssignation";
    }

    // show the list of all users (only users)
    // allows the admin to promote a user to admin
    @GetMapping("/upgradeUser")
    public String upgradeUser(Model model) {
        model.addAttribute("userList", userRepository.getUsersOnly());
        return "segments/adminActions/promote";
    }

    // actual promotion of a user to admin
    // reloads the user list after promotion
    @PostMapping("/promote")
    public String promote(@RequestBody Map<String, Object> payload, Model model) {
        int userId = (int) payload.get("userId");
        userRepository.promoteUser(userId);
        model.addAttribute("userList", userRepository.getUsersOnly());
        return "segments/adminActions/promote";
    }

    // actual assignment of prizes to the top three users
    // returns the list of assigned prizes in order of ranking
    @GetMapping("/assignPrizes")
    @ResponseBody
    public List<String> assignPrizes() {
        return assegnaPremi.assegnaPremi();
    }
}
