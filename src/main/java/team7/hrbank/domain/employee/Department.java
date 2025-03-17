package team7.hrbank.domain.employee;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import team7.hrbank.domain.base.BaseEntity;

import java.util.Collection;

@Entity
@Getter @Setter
public class Department extends BaseEntity {

    private String name;

}
