package project.job_portal.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.job_portal.dto.LoginDto;
import project.job_portal.dto.UserDto;
import project.job_portal.dto.UserUpdateDto;
import project.job_portal.model.Company;
import project.job_portal.model.Role;
import project.job_portal.model.User;
import project.job_portal.repository.CompanyRepository;
import project.job_portal.repository.UserRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    private final CompanyRepository companyRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    @Autowired
    private HttpServletRequest request;

    public int userCount() {
        return repository.findAll().size();
    }

    public User findUserByEmail(String email) {
        Optional<User> check = repository.findUserByEmail(email);

        if (check.isPresent()) {
            return check.get();
        }

        throw new RuntimeException("Không tìm thấy người dùng.");
    }

    public void updateUser(UserUpdateDto input) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = findUserByEmail(email);

        user.setFullname(input.getFullname());
        user.setPhone(input.getPhone());
        user.setName(input.getUsername());
        user.setAddress(input.getAddress());
        user.setDescription(input.getDescription());

        repository.save(user);
    }

    public void registerUser(UserDto input) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Optional<User> check = repository.findUserByEmail(input.getEmail());

        if (check.isPresent()) {
            throw new RuntimeException("Tài khoản đã có người sử dụng.");
        }

        var user = User.builder()
                .email(input.getEmail())
                .password(passwordEncoder.encode(input.getPassword()))
                .created(formatter.format(new Date(System.currentTimeMillis())))
                .role(Role.USER)
                .build();

        repository.save(user);
        login(input, request);
    }

    public void login(LoginDto dto, HttpServletRequest request) {
        Optional<User> check = repository.findUserByEmail(dto.getEmail());
        Optional<Company> check1 = companyRepository.findCompanyByEmail(dto.getEmail());

        if (check.isEmpty() && check1.isEmpty()) {
            throw new UsernameNotFoundException("Không tìm thấy tài khoản");
        }

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword())
        );

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(auth);
        HttpSession session = request.getSession();
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, context);
    }

    public User findUserById(int id) {
        return repository.findUserById(id);
    }

    private void login(UserDto dto, HttpServletRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword())
        );

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(auth);
        HttpSession session = request.getSession();
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, context);
    }
}
