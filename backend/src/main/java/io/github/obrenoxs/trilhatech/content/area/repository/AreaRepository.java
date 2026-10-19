package io.github.obrenoxs.trilhatech.content.area.repository;

import io.github.obrenoxs.trilhatech.content.area.entity.Area;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AreaRepository extends JpaRepository<Area, UUID> {

    Optional<Area> findBySlug(String slug);
}
