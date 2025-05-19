package mate.academy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE accommodations SET is_deleted = true WHERE id=?")
@FilterDef(name = "accommodationSoftDeleteFilter", parameters = @ParamDef(name = "isDeleted",
        type = Boolean.class))
@Filter(name = "accommodationSoftDeleteFilter", condition = "is_deleted = :isDeleted")
@Table(name = "accommodations")
public class Accommodation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "accommodation")
    private List<Booking> bookings;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String size;

    @NotEmpty
    private List<String> amenities;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer availability;

    @Column(nullable = false)
    private boolean isDeleted = false;

    private BigDecimal dailyRate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    public enum Type {
        HOUSE,
        APARTMENT,
        CONDO,
        VACATION_HOME
    }
}
