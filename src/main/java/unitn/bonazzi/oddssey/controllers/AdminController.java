package unitn.bonazzi.oddssey.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import unitn.bonazzi.oddssey.repositories.UserRepository;

import java.util.Map;

@Controller
public class AdminController {
    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/userList")
    public String userList(Model model) {
        model.addAttribute("userList", userRepository.findAllUsers());
        return "segments/adminActions/userList";
    }

    @GetMapping("/rankingList")
    public String rankingList(Model model) {
        model.addAttribute("userList", userRepository.getRankingList());
        return "segments/adminActions/userList";
    }

    @GetMapping("/assegnaPremi")
    public String assegnaPremi() {
        return "segments/userActions/changePassword";
    }

    @GetMapping("/upgradeUser")
    public String upgradeUser(Model model) {
        model.addAttribute("userList", userRepository.getUsersOnly());
        return "segments/adminActions/promote";
    }

    @PostMapping("/promote")
    public String promote(@RequestBody Map<String, Object> payload, Model model) {
        int userId = (int) payload.get("userId");
        userRepository.promoteUser(userId);
        model.addAttribute("userList", userRepository.getUsersOnly());
        return "segments/adminActions/promote"; // returns updated HTML fragment
    }
}
