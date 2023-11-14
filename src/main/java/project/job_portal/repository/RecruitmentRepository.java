package project.job_portal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import project.job_portal.model.Recruitment;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {

    @Query("SELECT r FROM Recruitment r WHERE "
            + "r.title LIKE CONCAT('%', ?1, '%')"
            + "OR r.description LIKE CONCAT('%', ?1, '%')"
            + "OR r.category.name LIKE CONCAT('%', ?1, '%') "
            + "OR r.experience LIKE CONCAT('%', ?1, '%')")
    public List<Recruitment> findRecruitmentByKeyword(String keyword);

    @Query("SELECT r FROM Recruitment r WHERE r.location LIKE CONCAT('%', ?1, '%')")
    public List<Recruitment> findRecruitmentByLocation(String location);

    public Optional<Recruitment> findRecruitmentById(Long id);

    @Query("select r from Recruitment r order by r.created desc")
    List<Recruitment> findRecruitmentsByCreated();

    @Query("select r from Recruitment r where r.category.id = ?1")
    List<Recruitment> findRecruitmentsByCategoryId(Long id);
}
