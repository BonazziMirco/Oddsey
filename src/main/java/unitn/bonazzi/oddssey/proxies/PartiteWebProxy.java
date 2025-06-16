package unitn.bonazzi.oddssey.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import unitn.bonazzi.oddssey.pojos.Match;
import unitn.bonazzi.oddssey.pojos.Team;
import unitn.bonazzi.oddssey.configurations.OpenFeignConfig;

import java.time.LocalDate;
import java.util.List;

@FeignClient(name = "PartiteWeb",
        url = "${name.service.url}",
        configuration = OpenFeignConfig.class)
public interface PartiteWebProxy {

    @PostMapping("/matchList")
    public abstract List<Match> getMatches(@RequestParam String sport, @RequestParam LocalDate date);

    @PostMapping("/matchCalendar")
    public abstract List<Match> getAllMatches(@RequestParam String sport);

    @PostMapping("/getTeams")
    public abstract List<Team> getTeams(@RequestParam String sport);

    @GetMapping("/getAllTeams")
    public abstract List<Team> getAllTeams();

    @PostMapping("/createMatches")
    public abstract void createMatches(@RequestBody String sport);

    @PostMapping("/getResults")
    public abstract List<Integer> getResults(@RequestParam String sport, @RequestParam LocalDate date);
}