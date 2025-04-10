package mate.academy.mapper;

import mate.academy.config.MapperConfig;
import mate.academy.dto.payment.PaymentDto;
import mate.academy.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {

    @Mapping(source = "booking.id", target = "bookingId")
    @Mapping(source = "booking.user.id", target = "userId")
    PaymentDto toDto(Payment payment);
}
