package ch.hearc.jee_project.pointsinterettouristiques.controller;

import ch.hearc.jee_project.pointsinterettouristiques.model.User;
import ch.hearc.jee_project.pointsinterettouristiques.repository.UserRepository;
import ch.hearc.jee_project.pointsinterettouristiques.service.PlaceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final PlaceService placeService;

    public UserController(UserRepository userRepository, PlaceService placeService) {
        this.userRepository = userRepository;
        this.placeService = placeService;
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class UnauthorizedAccessException extends RuntimeException {
        public UnauthorizedAccessException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    private void authorizeAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
            throw new UnauthorizedAccessException("Only admins can access this endpoint");
        }
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        authorizeAdmin(); // Protège la création d’un utilisateur
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        authorizeAdmin(); // Protège la suppression d’un utilisateur
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Supprimer les évaluations de cet utilisateur
        placeService.removeUserRatings(user);

        // Supprimer l’utilisateur
        userRepository.delete(user);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        authorizeAdmin(); // Protège la récupération de tous les utilisateurs
        return ResponseEntity.ok(userRepository.findAll());
    }

    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAllUsers() {
        authorizeAdmin(); // Protège la suppression de tous les utilisateurs
        userRepository.deleteAll();
        return ResponseEntity.noContent().build();
    }
}
