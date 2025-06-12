package unitn.bonazzi.oddssey.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import unitn.bonazzi.oddssey.pojos.User;
import unitn.bonazzi.oddssey.repositories.UserRepository;

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
        model.addAttribute("userList", userRepository.getRankinList());
        return "segments/adminActions/userList";
    }

    @GetMapping("/assegnaPremi")
    public String assegnaPremi() {
        return "segments/userActions/changePassword";
    }

    @RequestMapping("/upgradeUser")
    public String upgradeUser(Model model) {
        model.addAttribute("userList", userRepository.getUsersOnly());
        return "segments/adminActions/promote";
    }

    @PostMapping("/promote")
    public String promote(@RequestParam int userId) {
        System.out.println("Promoting user with ID: " + userId);
        userRepository.promoteUser(userId);
        return "redirect:/upgradeUser";
    }
}
