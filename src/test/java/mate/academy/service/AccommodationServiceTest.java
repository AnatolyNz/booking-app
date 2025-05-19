package mate.academy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.contains;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import mate.academy.dto.accommodation.AccommodationDto;
import mate.academy.dto.accommodation.AccommodationSearchParameters;
import mate.academy.dto.accommodation.CreateAccommodationRequestDto;
import mate.academy.dto.accommodation.UpdateAccommodationRequestDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.AccommodationMapper;
import mate.academy.model.Accommodation;
import mate.academy.repository.accommodation.AccommodationRepository;
import mate.academy.repository.accommodation.AccommodationSpecificationBuilder;
import mate.academy.service.impl.AccommodationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class AccommodationServiceTest {

    @Mock
    private AccommodationRepository accommodationRepository;

    @Mock
    private AccommodationMapper accommodationMapper;

    @Mock
    private AccommodationSpecificationBuilder accommodationSpecificationBuilder;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AccommodationServiceImpl accommodationService;

    @BeforeEach
    public void setUp() {
        reset(accommodationRepository, accommodationMapper, notificationService);
    }

    @Test
    @DisplayName("Verify save() creates accommodation and sends notification")
    public void save_ShouldSaveAccommodationAndSendNotification() {
        Accommodation savedAccommodation = new Accommodation();
        savedAccommodation.setLocation("Paris");
        savedAccommodation.setSize("Large");
        savedAccommodation.setPrice(BigDecimal.valueOf(150));
        savedAccommodation.setAvailability(3);
        AccommodationDto expectedDto = new AccommodationDto();
        CreateAccommodationRequestDto requestDto = new CreateAccommodationRequestDto();
        Accommodation accommodation = new Accommodation();

        when(accommodationMapper.toModel(requestDto)).thenReturn(accommodation);
        when(accommodationRepository.save(accommodation)).thenReturn(savedAccommodation);
        when(accommodationMapper.toDto(savedAccommodation)).thenReturn(expectedDto);

        AccommodationDto result = accommodationService.save(requestDto);

        assertEquals(expectedDto, result);
        verify(notificationService).sendMessage(eq("Paris"), contains("New accommodation added"));
    }

    @Test
    @DisplayName("Verify findAll() returns a list of accommodations")
    public void findAll_ShouldReturnListOfAccommodations() {
        Accommodation acc1 = new Accommodation();
        Accommodation acc2 = new Accommodation();
        List<Accommodation> accommodations = List.of(acc1, acc2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Accommodation> accommodationPage = new PageImpl<>(accommodations);

        when(accommodationRepository.findAll(pageable)).thenReturn(accommodationPage);
        when(accommodationMapper.toDto(acc1)).thenReturn(new AccommodationDto());
        when(accommodationMapper.toDto(acc2)).thenReturn(new AccommodationDto());

        Page<AccommodationDto> result = accommodationService.findAll(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
    }

    @Test
    @DisplayName("Verify getAccommodationById returns DTO for valid ID")
    public void getAccommodationById_ShouldReturnDto() {
        Long id = 1L;
        Accommodation acc = new Accommodation();
        acc.setId(id);
        AccommodationDto expected = new AccommodationDto();
        when(accommodationRepository.getAccommodationById(id)).thenReturn(Optional.of(acc));
        when(accommodationMapper.toDto(acc)).thenReturn(expected);

        AccommodationDto result = accommodationService.getAccommodationById(id);

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Verify getAccommodationById throws for invalid ID")
    public void getAccommodationById_ShouldThrowWhenNotFound() {
        Long id = 999L;
        when(accommodationRepository.getAccommodationById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> accommodationService.getAccommodationById(id));

        assertEquals("Can't find accommodation with id " + id, exception.getMessage());
    }

    @Test
    @DisplayName("Verify updateById updates fields and calls repository")
    public void updateById_ShouldUpdateAccommodation() {
        UpdateAccommodationRequestDto updateDto = new UpdateAccommodationRequestDto();
        updateDto.setLocation("New York");
        updateDto.setSize("Large");
        updateDto.setAmenities(List.of("WiFi", "TV"));
        updateDto.setPrice(BigDecimal.valueOf(150.00));
        updateDto.setAvailability(3);
        updateDto.setDailyRate(BigDecimal.valueOf(100.00));
        updateDto.setType(Accommodation.Type.APARTMENT);

        Long id = 1L;
        Accommodation existingAccommodation = new Accommodation();
        existingAccommodation.setId(id);

        when(accommodationRepository.findById(id)).thenReturn(Optional.of(existingAccommodation));

        when(accommodationRepository.save(existingAccommodation)).thenReturn(existingAccommodation);

        AccommodationDto dto = new AccommodationDto();
        when(accommodationMapper.toDto(existingAccommodation)).thenReturn(dto);

        AccommodationDto result = accommodationService.updateById(id, updateDto);

        assertEquals(dto, result);

        assertEquals("New York", existingAccommodation.getLocation());
        assertEquals("Large", existingAccommodation.getSize());
        assertEquals(List.of("WiFi", "TV"), existingAccommodation.getAmenities());
        assertEquals(BigDecimal.valueOf(150.00), existingAccommodation.getPrice());
        assertEquals(3, existingAccommodation.getAvailability());
        assertEquals(BigDecimal.valueOf(100.00), existingAccommodation.getDailyRate());
        assertEquals(Accommodation.Type.APARTMENT, existingAccommodation.getType());

        verify(accommodationRepository).save(existingAccommodation);
    }

    @Test
    @DisplayName("Verify deleteById removes accommodation and sends notification")
    public void deleteById_ShouldDeleteAccommodationAndSendMessage() {
        Long id = 1L;

        accommodationService.deleteById(id);

        verify(accommodationRepository).deleteById(id);
        verify(notificationService).sendMessage(eq("Admin"), contains("deleted"));
    }

    @Test
    @DisplayName("Verify search returns matching accommodations")
    public void search_ShouldReturnMatchingAccommodations() {
        AccommodationSearchParameters params = new AccommodationSearchParameters(
                new String[]{}, new String[]{}, new String[]{}, new String[]{}
        );
        Accommodation acc = new Accommodation();
        AccommodationDto dto = new AccommodationDto();
        Specification<Accommodation> spec = mock(Specification.class);

        when(accommodationSpecificationBuilder.build(params)).thenReturn(spec);
        when(accommodationRepository.findAll(spec)).thenReturn(List.of(acc));
        when(accommodationMapper.toDto(acc)).thenReturn(dto);

        List<AccommodationDto> result = accommodationService.search(params);

        assertEquals(1, result.size());
    }
}
