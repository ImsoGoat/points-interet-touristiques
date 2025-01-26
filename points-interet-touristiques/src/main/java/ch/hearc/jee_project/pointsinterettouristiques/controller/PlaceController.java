package ch.hearc.jee_project.pointsinterettouristiques.controller;

import ch.hearc.jee_project.pointsinterettouristiques.model.Place;
import ch.hearc.jee_project.pointsinterettouristiques.model.Role;
import ch.hearc.jee_project.pointsinterettouristiques.model.User;
import ch.hearc.jee_project.pointsinterettouristiques.model.ValidationStatus;
import ch.hearc.jee_project.pointsinterettouristiques.repository.PlaceRepository;
import ch.hearc.jee_project.pointsinterettouristiques.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/places")
public class PlaceController {

    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;

    public PlaceController(PlaceRepository placeRepository, UserRepository userRepository) {
        this.placeRepository = placeRepository;
        this.userRepository = userRepository;
    }

    // Récupérer tous les lieux (admin)
    @GetMapping
    public ResponseEntity<List<Place>> getAllPlaces(@RequestParam Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Place> places = placeRepository.findAll();
        return ResponseEntity.ok(places);
    }


    // Ajouter un lieu (par défaut en état non validé)
    @PostMapping
    public ResponseEntity<Place> createPlace(@RequestBody Place place) {
        place.setStatus(ValidationStatus.UNVALIDATED);
        Place savedPlace = placeRepository.save(place);
        return new ResponseEntity<>(savedPlace, HttpStatus.CREATED);
    }

    // Récupérer un lieu par ID (admin peut tout voir, user seulement lieux publiés)
    @GetMapping("/{id}")
    public ResponseEntity<Place> getPlaceById(@PathVariable Long id, @RequestParam Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Place place = placeRepository.findById(id).orElseThrow(() -> new RuntimeException("Place not found"));

        if (user.getRole() == Role.ADMIN || place.getStatus() == ValidationStatus.VALIDATED) {
            return ResponseEntity.ok(place);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    // Mettre à jour un lieu (admin)
    @PutMapping("/{id}")
    public ResponseEntity<Place> updatePlace(@PathVariable Long id, @RequestParam Long userId, @RequestBody Place placeDetails) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return placeRepository.findById(id)
                .map(place -> {
                    place.setName(placeDetails.getName());
                    place.setDescription(placeDetails.getDescription());
                    place.setLocation(placeDetails.getLocation());
                    place.setLatitude(placeDetails.getLatitude());
                    place.setLongitude(placeDetails.getLongitude());
                    Place updatedPlace = placeRepository.save(place);
                    return new ResponseEntity<>(updatedPlace, HttpStatus.OK);
                })
                .orElse(ResponseEntity.notFound().build());
    }


    // Supprimer un lieu (admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlace(@PathVariable Long id, @RequestParam Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return placeRepository.findById(id)
                .map(place -> {
                    placeRepository.delete(place);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElse(ResponseEntity.notFound().build());
    }



    // Récupérer tous les lieux validés (accessibles par tous)
    @GetMapping("/validatedPlaces")
    public ResponseEntity<List<Place>> getValidatedPlaces() {
        List<Place> places = placeRepository.findByStatus(ValidationStatus.VALIDATED);
        return ResponseEntity.ok(places);
    }

    // Récupérer les lieux non validés (uniquement pour l'administrateur)
    @GetMapping("/unvalidatedPlaces")
    public ResponseEntity<List<Place>> getUnvalidatedPlaces(@RequestParam Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Place> places = placeRepository.findByStatus(ValidationStatus.UNVALIDATED);
        return ResponseEntity.ok(places);
    }

    // Récupérer les lieux refusés (uniquement pour l'administrateur)
    @GetMapping("/rejectedPlaces")
    public ResponseEntity<List<Place>> getRejectedPlaces(@RequestParam Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Place> places = placeRepository.findByStatus(ValidationStatus.REJECTED);
        return ResponseEntity.ok(places);
    }

    // Récupérer les lieux non validés et refusés combinés (uniquement pour l'administrateur)
    @GetMapping("/unvalidatedAndRejectedPlaces")
    public ResponseEntity<List<Place>> getUnvalidatedAndRejectedPlaces(@RequestParam Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Place> places = placeRepository.findByStatusIn(List.of(ValidationStatus.UNVALIDATED, ValidationStatus.REJECTED));
        return ResponseEntity.ok(places);
    }

    // Validation d'un lieu par l'admin
    @PatchMapping("/{id}/validate")
    public ResponseEntity<Place> validatePlace(@PathVariable Long id, @RequestParam Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Place place = placeRepository.findById(id).orElseThrow(() -> new RuntimeException("Place not found"));
        place.setStatus(ValidationStatus.VALIDATED);
        placeRepository.save(place);

        return ResponseEntity.ok(place);
    }


    // Rejeter un lieu par l'admin
    @PatchMapping("/{id}/reject")
    public ResponseEntity<Place> rejectPlace(@PathVariable Long id, @RequestParam Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Place place = placeRepository.findById(id).orElseThrow(() -> new RuntimeException("Place not found"));
        place.setStatus(ValidationStatus.REJECTED);
        placeRepository.save(place);

        return ResponseEntity.ok(place);
    }

}
