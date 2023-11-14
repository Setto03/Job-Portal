package project.job_portal.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.job_portal.dto.CompanyDto;
import project.job_portal.dto.CompanyUpdateDto;
import project.job_portal.dto.RecruitmentDto;
import project.job_portal.model.Category;
import project.job_portal.model.Company;
import project.job_portal.model.Recruitment;
import project.job_portal.model.Role;
import project.job_portal.repository.CategoryRepository;
import project.job_portal.repository.CompanyRepository;
import project.job_portal.repository.RecruitmentRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Service
@RequiredArgsConstructor
public class CompanyService {

    @Autowired
    HttpServletRequest request;

    private final CompanyRepository repository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final RecruitmentRepository recruitmentRepository;
    private final AuthenticationManager authenticationManager;

    private Company getCurrentCompany() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        return findCompanyByEmail(email);
    }

    public int companyCount() {
        return repository.findAll().size();
    }

    public Company findCompanyById(Long id) {
        return repository.findCompanyById(id);
    }

    public Company findCompanyByEmail(String email) {
        Optional<Company> check = repository.findCompanyByEmail(email);

        if (check.isPresent()) {
            return check.get();
        }

        throw new RuntimeException("Không tìm thấy công ty");
    }

    public void updateCompany(CompanyUpdateDto company) {
        Company company1 = getCurrentCompany();

        // Không sử dụng builder vì Spring sẽ tạo object mới
        company1.setAddress(company.getAddress());
        company1.setName(company.getName());
        company1.setPhone(company.getPhone());
        company1.setDescription(company.getDescription());

        repository.save(company1);
    }

    public void registerCompany(CompanyDto dto) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Optional<Company> check = repository.findCompanyByEmail(dto.getEmail());

        if (check.isPresent()) {
            throw new RuntimeException("Công ty đã được đăng ký.");
        }

        var company = Company.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .created(format.format(new Date(System.currentTimeMillis())))
                .roles(List.of(Role.COMPANY))
                .build();

        repository.save(company);

        // Thực hiện login sau khi đăng ký thành công để truy cập secured endpoint
        login(dto, request);
    }

    public void createJob(RecruitmentDto dto, Long id) {
        Company company = getCurrentCompany();

        Category category = categoryRepository.findCategoryById(id);

        var recruitment = Recruitment.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .experience(dto.getExperience())
                .salary(dto.getSalary())
                .quantity(dto.getQuantity())
                .location(dto.getLocation())
                .category(category)
                .company(company)
                .created(String.valueOf(new Date(System.currentTimeMillis())))
                .build();

        recruitmentRepository.save(recruitment);

        // Tạo mới danh sách nếu chưa có data
        if (category.getRecruitments() == null) {
            List<Recruitment> newList = new ArrayList<>();
            newList.add(recruitment);

            category.setRecruitments(newList);
        } else {
            category.getRecruitments().add(recruitment);
        }

        categoryRepository.save(category);

        company.getRecruitments().add(recruitment);
        repository.save(company);
    }

    public void updateJob(RecruitmentDto dto, Long id, Long jobId) {
        Recruitment recruitment = recruitmentRepository.findRecruitmentById(jobId).orElseThrow();
        Category category = categoryRepository.findCategoryById(id);

        recruitment.setTitle(dto.getTitle());
        recruitment.setDescription(dto.getDescription());
        recruitment.setExperience(dto.getExperience());
        recruitment.setSalary(dto.getSalary());
        recruitment.setQuantity(dto.getQuantity());
        recruitment.setLocation(dto.getLocation());
        recruitment.setCategory(category);

        recruitmentRepository.save(recruitment);
    }

    public void removeJob(Long id) {

        Company company = getCurrentCompany();
        List<Recruitment> list = company.getRecruitments();

        // Xóa công việc khỏi danh sách đăng tuyển của công ty
        list.removeIf(r -> r.getId().equals(id));

        company.setRecruitments(list);
        repository.save(company);

        // Xóa công việc khỏi database
        recruitmentRepository.deleteById(id);
    }

    public List<Company> getList() {
        return repository.findCompaniesByRecruitmentsCount(PageRequest.of(0,3));
    }

    private void login(CompanyDto dto, HttpServletRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword())
        );

        // Gọi SecurityContextHolder để cập nhật cho người dùng đăng nhập
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(auth);

        // Gọi HttpSession để thiết lập context chứa thông tin authenticated
        HttpSession session = request.getSession();
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, context);
    }

}
