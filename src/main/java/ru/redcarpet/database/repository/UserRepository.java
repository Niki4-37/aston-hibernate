package ru.redcarpet.database.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.redcarpet.database.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("""
            select u.id from User u
            where u.name = :name
            and u.email = :email
            and u.birthDate = :birthDate
            """)
    Long getIdByNameEmailBirthdate(
        @Param("name")  String name,
        @Param("email") String email,
        @Param("birthDate") LocalDate birthDate
    );
}
