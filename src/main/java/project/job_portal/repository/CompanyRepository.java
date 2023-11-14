package project.job_portal.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import project.job_portal.model.Company;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    public Optional<Company> findCompanyByEmail(String email);

    public Company findCompanyById(Long id);

    public Company findCompanyByNameContainingIgnoreCase(String keyword);


    @Query("select c from Company c order by size(c.recruitments) desc")
    List<Company> findCompaniesByRecruitmentsCount(Pageable pageable);
}
