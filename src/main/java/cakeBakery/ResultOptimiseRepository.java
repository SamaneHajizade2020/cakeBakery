package cakeBakery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResultOptimiseRepository extends JpaRepository<ResultOptimise, Long> {

}
