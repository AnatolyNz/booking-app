package mate.academy.repository.accommodation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import mate.academy.model.Accommodation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
public class AccommodationRepositoryTest {
    @Autowired
    private AccommodationRepository accommodationRepository;

    @Test
    @DisplayName("Save and retrieve accommodation by ID")
    @Sql(scripts = {
            "classpath:database/accommodations/add-accommodations.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/accommodations/remove-accommodations.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getAccommodationById_WithValidId_ShouldReturnAccommodation() {
        Optional<Accommodation> optional = accommodationRepository.getAccommodationById(1L);

        assertTrue(optional.isPresent(), "Accommodation should be present");
        Accommodation accommodation = optional.get();

        assertEquals("Kyiv", accommodation.getLocation());
        assertEquals("LARGE", accommodation.getSize());
        assertEquals(0, accommodation.getPrice().compareTo(BigDecimal.valueOf(1000)));
    }

    @Test
    @DisplayName("Save a new accommodation and verify it is stored correctly")
    public void saveAccommodation_ShouldPersistAndReturnAccommodation() {
        Accommodation accommodation = new Accommodation();
        accommodation.setLocation("Lviv");
        accommodation.setSize("MEDIUM");
        accommodation.setPrice(BigDecimal.valueOf(750));
        accommodation.setAmenities(Collections.singletonList("WiFi"));
        accommodation.setType(Accommodation.Type.HOUSE);
        accommodation.setDailyRate(BigDecimal.valueOf(100));
        accommodation.setAvailability(10);

        Accommodation saved = accommodationRepository.save(accommodation);

        assertNotNull(saved.getId(), "Saved accommodation should have an ID");
        assertEquals(0, accommodation.getPrice().compareTo(BigDecimal.valueOf(750)));
    }

    @Test
    @DisplayName("Find all accommodations")
    @Sql(scripts = {
            "classpath:database/accommodations/add-accommodations.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/accommodations/remove-accommodations.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findAllAccommodations_ShouldReturnAllInsertedAccommodations() {
        List<Accommodation> accommodations = accommodationRepository.findAll();

        assertFalse(accommodations.isEmpty(), "Accommodations list should not be empty");
        assertEquals(2, accommodations.size());

        Accommodation first = accommodations.get(0);
        assertNotNull(first.getLocation());
    }
}
