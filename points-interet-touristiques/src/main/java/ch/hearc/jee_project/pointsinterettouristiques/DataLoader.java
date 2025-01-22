package ch.hearc.jee_project.pointsinterettouristiques;

import ch.hearc.jee_project.pointsinterettouristiques.model.Place;
import ch.hearc.jee_project.pointsinterettouristiques.repository.PlaceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final PlaceRepository placeRepository;

    public DataLoader(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    @Override
    public void run(String... args) {
        Place place = new Place();
        place.setName("Tour Eiffel");
        place.setDescription("Un monument célèbre à Paris.");
        place.setLocation("Paris, France");

        placeRepository.save(place);

        System.out.println("Données initiales ajoutées !");
    }
}
