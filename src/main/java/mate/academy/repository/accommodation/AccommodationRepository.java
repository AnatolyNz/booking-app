package mate.academy.repository.accommodation;

import java.util.List;
import java.util.Optional;
import mate.academy.exception.model.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AccommodationRepository extends JpaRepository<Accommodation, Long>,
        JpaSpecificationExecutor<Accommodation> {

    Accommodation save(Accommodation accommodation);

    Optional<Accommodation> getAccommodationById(Long id);

    List<Accommodation> findAll();
}
