package ch.hearc.jee_project.pointsinterettouristiques.controller;

import ch.hearc.jee_project.pointsinterettouristiques.model.Place;
import ch.hearc.jee_project.pointsinterettouristiques.repository.PlaceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/places")
public class PlaceController {

    private final PlaceRepository placeRepository;

    public PlaceController(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    // 1. Récupérer tous les lieux
    @GetMapping
    public List<Place> getAllPlaces() {
        return placeRepository.findAll();
    }

    // 2. Ajouter un lieu
    @PostMapping
    public ResponseEntity<Place> createPlace(@RequestBody Place place) {
        Place savedPlace = placeRepository.save(place);
        return new ResponseEntity<>(savedPlace, HttpStatus.CREATED);
    }

    // 3. Récupérer un lieu par ID
    @GetMapping("/{id}")
    public ResponseEntity<Place> getPlaceById(@PathVariable Long id) {
        return placeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 4. Mettre à jour un lieu
    @PutMapping("/{id}")
    public ResponseEntity<Place> updatePlace(@PathVariable Long id, @RequestBody Place placeDetails) {
        return placeRepository.findById(id)
                .map(place -> {
                    place.setName(placeDetails.getName());
                    place.setDescription(placeDetails.getDescription());
                    place.setLocation(placeDetails.getLocation());
                    Place updatedPlace = placeRepository.save(place);
                    return new ResponseEntity<>(updatedPlace, HttpStatus.OK);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 5. Supprimer un lieu
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlace(@PathVariable Long id) {
        return placeRepository.findById(id)
                .map(place -> {
                    placeRepository.delete(place);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
