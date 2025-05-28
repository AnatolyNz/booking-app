package mate.academy.service.impl;

import java.util.List;
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
import mate.academy.service.AccommodationService;
import mate.academy.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccommodationServiceImpl implements AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final AccommodationMapper accommodationMapper;
    private final AccommodationSpecificationBuilder accommodationSpecificationBuilder;
    private final NotificationService notificationService;

    @Override
    @Transactional
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
    public Page<AccommodationDto> findAll(Pageable pageable) {
        return accommodationRepository.findAll(pageable)
                .map(accommodationMapper::toDto);
    }

    @Override
    public AccommodationDto getAccommodationById(Long id) {
        return accommodationMapper.toDto(accommodationRepository.getAccommodationById(id)
                .orElseThrow(() -> new
                        EntityNotFoundException("Can't find accommodation with id " + id)));
    }

    @Override
    @Transactional
    public AccommodationDto updateById(Long id, UpdateAccommodationRequestDto
            updateAccommodationRequestDto) {
        Accommodation accommodation = accommodationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Accommodation not found with id: " + id));

        accommodationMapper.updateAccommodationFromDto(updateAccommodationRequestDto,
                accommodation);

        accommodationRepository.save(accommodation);
        return accommodationMapper.toDto(accommodation);
    }

    @Override
    @Transactional
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
