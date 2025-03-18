package team7.hrbank.domain.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@MappedSuperclass
@Getter
public class BaseUpdatableEntity extends BaseEntity{

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
