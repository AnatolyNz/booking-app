package mate.academy.repository.accommodation;

import java.util.Optional;
import mate.academy.model.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long>,
        JpaSpecificationExecutor<Accommodation> {

    Optional<Accommodation> getAccommodationById(Long id);
}
