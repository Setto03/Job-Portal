package project.job_portal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.job_portal.model.Company;
import project.job_portal.model.Recruitment;
import project.job_portal.model.User;
import project.job_portal.repository.CompanyRepository;
import project.job_portal.repository.RecruitmentRepository;
import project.job_portal.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecruitmentService {

    private final RecruitmentRepository repository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public int jobCount() {
        return repository.findAll().size();
    }

    public List<Recruitment> searchByCompany(String query) {
        String keyword = query.toLowerCase();
        Company check = companyRepository.findCompanyByNameContainingIgnoreCase(keyword);

        return check.getRecruitments();
    }

    public Recruitment findRecruitmentById(Long id) {
        Optional<Recruitment> check = repository.findRecruitmentById(id);

        if (check.isPresent()) {
            return check.get();
        }

        throw new RuntimeException("Không tìm thấy công việc");
    }

    public List<Recruitment> searchByKeyword(String query) {
        return repository.findRecruitmentByKeyword(query);
    }

    public List<Recruitment> searchByLocation(String query) {
        return repository.findRecruitmentByLocation(query);
    }

    public List<Recruitment> getList() {
        return repository.findRecruitmentsByCreated();
    }

    public void sendApply(User user, Recruitment recruitment) {
        user.getAppliedList().add(recruitment);
        userRepository.save(user);

        recruitment.getApplicants().add(user);
        repository.save(recruitment);
    }
}
