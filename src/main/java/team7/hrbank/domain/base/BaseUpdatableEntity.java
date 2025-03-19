package team7.hrbank.domain.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import lombok.Getter;
import org.hibernate.annotations.UpdateTimestamp;

@MappedSuperclass
@Getter
public class BaseUpdatableEntity extends BaseEntity{

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
