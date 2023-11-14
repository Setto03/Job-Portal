package project.job_portal.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import project.job_portal.model.Category;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query(value = "select c from Category c order by size(c.recruitments) desc")
    List<Category> findCategoriesByRecruitmentsCount(Pageable pageable);

    Category findCategoryByName(String name);

    Category findCategoryById(Long id);
}
