package mate.academy.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.accommodation.AccommodationDto;
import mate.academy.dto.accommodation.AccommodationSearchParameters;
import mate.academy.dto.accommodation.CreateAccommodationRequestDto;
import mate.academy.dto.accommodation.UpdateAccommodationRequestDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.AccommodationMapper;
import mate.academy.model.Accommodation;
import mate.academy.repository.accommodation.AccommodationRepository;
import mate.academy.repository.accommodation.AccommodationSpecificationBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccommodationServiceImpl implements AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final AccommodationMapper accommodationMapper;
    private final AccommodationSpecificationBuilder accommodationSpecificationBuilder;
    private final NotificationService notificationService;

    @Override
    public AccommodationDto save(CreateAccommodationRequestDto createAccommodationRequestDto) {
        Accommodation accommodation = accommodationMapper.toModel(createAccommodationRequestDto);
        Accommodation savedAccommodation = accommodationRepository.save(accommodation);

        String message = String.format(
                "New accommodation added:\nLocation: %s\nSize: %s\nPrice: %s\nAvailability: %d",
                savedAccommodation.getLocation(),
                savedAccommodation.getSize(),
                savedAccommodation.getPrice(),
                savedAccommodation.getAvailability());
        notificationService.sendMessage(savedAccommodation.getLocation(), message);

        return accommodationMapper.toDto(savedAccommodation);
    }

    @Override
    public List<AccommodationDto> findAll(Pageable pageable) {
        return accommodationRepository.findAll(pageable).stream()
                .map(accommodationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AccommodationDto getAccommodationById(Long id) {
        return accommodationMapper.toDto(accommodationRepository.getAccommodationById(id)
                .orElseThrow(() -> new
                        EntityNotFoundException("Can't find accommodation with id " + id)));
    }

    @Override
    public void updateById(Long id, UpdateAccommodationRequestDto updateAccommodationRequestDto) {
        Accommodation accommodation = accommodationMapper
                .toModel(new CreateAccommodationRequestDto());
        accommodation.setId(id);
        accommodationRepository.save(accommodation);
    }

    @Override
    public void deleteById(Long id) {
        accommodationRepository.deleteById(id);

        String message = "An accommodation has been released (deleted). ID: " + id;
        notificationService.sendMessage("Admin", message);
    }

    @Override
    public List<AccommodationDto> search(AccommodationSearchParameters params) {
        Specification<Accommodation> bookSpecification =
                accommodationSpecificationBuilder.build(params);
        return accommodationRepository.findAll(bookSpecification)
                .stream()
                .map(accommodationMapper::toDto)
                .toList();
    }
}
