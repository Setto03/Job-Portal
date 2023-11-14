package project.job_portal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import project.job_portal.model.Company;
import project.job_portal.model.User;
import project.job_portal.repository.CompanyRepository;
import project.job_portal.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    private final CompanyRepository companyRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userCheck = userRepository.findUserByEmail(username);
        Optional<Company> companyCheck = companyRepository.findCompanyByEmail(username);

        if (userCheck.isPresent()) {
            User user = userCheck.get();

            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getEmail())
                    .password(user.getPassword())
                    .authorities(user.getAuthorities())
                    .build();
        }

        if (companyCheck.isPresent()) {
            Company company = companyCheck.get();

            return org.springframework.security.core.userdetails.User.builder()
                    .username(company.getEmail())
                    .password(company.getPassword())
                    .authorities(company.getAuthorities())
                    .build();
        }

        throw new UsernameNotFoundException("Không tìm thấy email");
    }
}
