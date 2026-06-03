package botanix.botanix.repository;

import botanix.botanix.model.Plantas;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlantasRepository extends JpaRepository<Plantas, Long> {
}
