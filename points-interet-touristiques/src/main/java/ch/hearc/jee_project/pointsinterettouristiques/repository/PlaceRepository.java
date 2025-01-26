package ch.hearc.jee_project.pointsinterettouristiques.repository;

import ch.hearc.jee_project.pointsinterettouristiques.model.Place;
import ch.hearc.jee_project.pointsinterettouristiques.model.ValidationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {

    List<Place> findByStatus(ValidationStatus status);
    List<Place> findByStatusIn(List<ValidationStatus> statuses);
}
