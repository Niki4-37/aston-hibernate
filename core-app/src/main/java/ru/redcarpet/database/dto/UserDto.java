package ru.redcarpet.database.dto;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.core.Relation;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Past;
import ru.redcarpet.database.UserController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Relation(collectionRelation = "users")
public record UserDto(
    @Null 
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id,
    @NotEmpty (message="empty name field")
    String name,
    @Email (message="wrong email format")
    @NotBlank(message="empty email field")
    String email,
    @Past(message="birthday should be at past time")
    LocalDate birthDate,
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    LocalDate createdAt
) {
    public Long getAge() {
        return ChronoUnit.YEARS.between(birthDate, LocalDate.now());
    }

    @Override
    public String toString() {
        return "[ User with name: "
            + name
            + " email: "
            + email
            + " birthday: "
            + birthDate
            + " age: "
            + getAge()
            + " created at: "
            + createdAt
            + " ]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof UserDto other)) return false;

        return Objects.equals(name,     other.name) &&
               Objects.equals(email,    other.email) &&
               Objects.equals(birthDate,other.birthDate) &&
               Objects.equals(createdAt,other.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, birthDate, createdAt);
    }

    public EntityModel<UserDto> toEntityModel() {
        return EntityModel.of(
            this,
            linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel(),
            linkTo(methodOn(UserController.class).updateUser(id, this)).withRel("update"),
            linkTo(methodOn(UserController.class).deleteUser(id)).withRel("delete")
        );
    }
}
