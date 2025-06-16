package unitn.bonazzi.oddssey.services;

import org.springframework.stereotype.Service;
import unitn.bonazzi.oddssey.pojos.User;
import unitn.bonazzi.oddssey.repositories.PrizeRepository;
import unitn.bonazzi.oddssey.repositories.UserRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class AssegnaPremi {
    private final UserRepository userRepository;
    private final PrizeRepository prizeRepository;

    public AssegnaPremi(UserRepository userRepository, PrizeRepository prizeRepository) {
        this.userRepository = userRepository;
        this.prizeRepository = prizeRepository;
    }

    public List<String> assegnaPremi(){
        List<String> prizes = Arrays.asList(
                "Biglietto per assistere a una partita della squadra del cuore",
                "Maglia ufficiale di un giocatore della squadra del cuore",
                "Serata in pizzeria insieme alla squadra del cuore"
        );
        Collections.shuffle(prizes);

        List<User> rankingList = userRepository.getRankingList();
        prizeRepository.createPrize(rankingList.get(0).getUsername(), prizes.get(0));
        prizeRepository.createPrize(rankingList.get(1).getUsername(), prizes.get(1));
        prizeRepository.createPrize(rankingList.get(2).getUsername(), prizes.get(2));

        return prizes;
    }
}
