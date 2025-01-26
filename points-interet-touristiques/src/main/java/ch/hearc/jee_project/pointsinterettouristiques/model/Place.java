package ch.hearc.jee_project.pointsinterettouristiques.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @ElementCollection
    private List<Integer> ratings = new ArrayList<>(); // Stocke les notes individuelles

    @Column(nullable = false)
    private double averageRating = 0.0; // Moyenne des notes

    @Enumerated(EnumType.STRING) // Stocke l'état en tant que chaîne
    @Column(nullable = false)
    private ValidationStatus status = ValidationStatus.UNVALIDATED;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public ValidationStatus getStatus() {
        return status;
    }

    public void setStatus(ValidationStatus status) {
        this.status = status;
    }

    // ratings

    public List<Integer> getRatings() {
        return ratings;
    }

    public void setRatings(List<Integer> ratings) {
        this.ratings = ratings;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public void addRating(int rating) {
        this.ratings.add(rating);
        updateAverageRating();
    }

    private void updateAverageRating() {
        if (!ratings.isEmpty()) {
            this.averageRating = ratings.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        } else {
            this.averageRating = 0.0;
        }
    }
}
