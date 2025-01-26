package ch.hearc.jee_project.pointsinterettouristiques;

import ch.hearc.jee_project.pointsinterettouristiques.model.Place;
import ch.hearc.jee_project.pointsinterettouristiques.model.User;
import ch.hearc.jee_project.pointsinterettouristiques.model.Role;
import ch.hearc.jee_project.pointsinterettouristiques.model.ValidationStatus;
import ch.hearc.jee_project.pointsinterettouristiques.repository.PlaceRepository;
import ch.hearc.jee_project.pointsinterettouristiques.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;

    public DataLoader(UserRepository userRepository, PlaceRepository placeRepository) {
        this.userRepository = userRepository;
        this.placeRepository = placeRepository;
    }

    @Override
    public void run(String... args) {
        // Ajouter un administrateur
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("password");
        admin.setRole(Role.ADMIN);

        // Ajouter un utilisateur
        User user = new User();
        user.setUsername("user");
        user.setPassword("password");
        user.setRole(Role.USER);

        userRepository.save(admin);
        userRepository.save(user);

        // Ajouter des lieux
        Place eiffelTower = new Place();
        eiffelTower.setName("Tour Eiffel");
        eiffelTower.setDescription("Un monument célèbre à Paris.");
        eiffelTower.setLocation("Paris, France");
        eiffelTower.setLatitude(48.858844);
        eiffelTower.setLongitude(2.294351);
        eiffelTower.setStatus(ValidationStatus.VALIDATED);
        eiffelTower.addOrUpdateRating(user.getId(),5);
        eiffelTower.addOrUpdateRating(user.getId(),10);
        eiffelTower.addOrUpdateRating(admin.getId(),8);

        Place colosseum = new Place();
        colosseum.setName("Colisée");
        colosseum.setDescription("Un ancien amphithéâtre romain situé à Rome.");
        colosseum.setLocation("Rome, Italie");
        colosseum.setLatitude(41.890251);
        colosseum.setLongitude(12.492373);
        colosseum.setStatus(ValidationStatus.VALIDATED);

        Place statueOfLiberty = new Place();
        statueOfLiberty.setName("Statue de la Liberté");
        statueOfLiberty.setDescription("Un symbole de la liberté situé à New York.");
        statueOfLiberty.setLocation("New York, USA");
        statueOfLiberty.setLatitude(40.689247);
        statueOfLiberty.setLongitude(-74.044502);
        statueOfLiberty.setStatus(ValidationStatus.UNVALIDATED);

        Place greatWall = new Place();
        greatWall.setName("Grande Muraille de Chine");
        greatWall.setDescription("Une ancienne série de murs défensifs en Chine.");
        greatWall.setLocation("Chine");
        greatWall.setLatitude(40.431908);
        greatWall.setLongitude(116.570374);
        greatWall.setStatus(ValidationStatus.REJECTED);

        Place machuPicchu = new Place();
        machuPicchu.setName("Machu Picchu");
        machuPicchu.setDescription("Une ancienne cité inca située dans les Andes.");
        machuPicchu.setLocation("Pérou");
        machuPicchu.setLatitude(-13.163141);
        machuPicchu.setLongitude(-72.544963);
        machuPicchu.setStatus(ValidationStatus.VALIDATED);

        // Sauvegarder les lieux dans la base de données
        placeRepository.save(eiffelTower);
        placeRepository.save(colosseum);
        placeRepository.save(statueOfLiberty);
        placeRepository.save(greatWall);
        placeRepository.save(machuPicchu);

        System.out.println("Données initiales ajoutées !");
    }


}


