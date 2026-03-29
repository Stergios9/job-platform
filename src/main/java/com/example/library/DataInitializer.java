package com.example.library;

import com.example.library.entity.Book;
import com.example.library.entity.JobPosition;
import com.example.library.entity.User;
import com.example.library.repository.BookRepository;
import com.example.library.repository.JobRepository;
import com.example.library.repository.UserRepository;
import com.example.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;

@Configuration
public class DataInitializer {
    @Autowired
    private UserRepository userRepository;

//    @Bean
//    public CommandLineRunner loadBooks(BookRepository repository) {
//        return args -> {
//            if (repository.count() == 0) {
//                repository.save(new Book("The Hobbit", "J.R.R. Tolkien", "123456", LocalDate.of(1937, 9, 21)));
//                repository.save(new Book("Java Design Patterns", "Vaskaran Sarcar", "789101", LocalDate.of(2018, 12, 1)));
//                System.out.println("✓ Sample books loaded into MySQL.");
//            }
//        };
//    }
//
//    @Bean
//    public CommandLineRunner loadUsers(UserService service) {
//
//        return args -> {
//            if (service.countUsers() == 0) {
//                // User 1: Admin
//                User admin = new User();
//                admin.setUsername("admin");
//                admin.setPassword("admin123");
//                service.registerUser(admin);
//
//                // User 2: Librarian
//                User librarian = new User();
//                librarian.setUsername("maria_lib");
//                librarian.setPassword("library2026");
//                service.registerUser(librarian);
//
//                // User 3: Standard Member
//                User member = new User();
//                member.setUsername("john_doe");
//                member.setPassword("secretPass");
//                service.registerUser(member);
//            }
//        };
//    }

    @Bean
    public CommandLineRunner loadData(UserService userService, JobRepository jobRepository, BCryptPasswordEncoder encoder) {
        return args -> {

            if (userRepository.findByUsername("boss").isEmpty()) {
                // 1. Δημιουργία Εργοδότη (Business)
                User employer = new User();
                employer.setUsername("boss");
                employer.setPassword("123"); // Το UserService.registerUser πρέπει να το κάνει encode
                employer.setRole("ROLE_EMPLOYER");
                employer.setCity("Athens");
                userService.registerUser(employer);

                // 2. Δημιουργία Εργαζόμενου (Worker)
                User worker = new User();
                worker.setUsername("giannis");
                worker.setPassword("123");
                worker.setRole("ROLE_WORKER");
                worker.setCity("Athens");
                userService.registerUser(worker);

                System.out.println("✓ Test Users Created: boss (Employer) & giannis (Worker)");

                // 3. Δημιουργία μερικών θέσεων εργασίας για τεστ
                if (jobRepository.count() == 0) {
                    JobPosition job1 = new JobPosition();
                    job1.setTitle("Waiter");
                    job1.setBusinessName("Acropolis Cafe");
                    job1.setCity("Athens");
                    job1.setHourlyRate(8.50);
                    job1.setDescription("Evening shift, 18:00 - 00:00");
                    job1.setEmployer(employer);
                    jobRepository.save(job1);

                    JobPosition job2 = new JobPosition();
                    job2.setTitle("Delivery");
                    job2.setBusinessName("Pizza Fast");
                    job2.setCity("Piraeus");
                    job2.setHourlyRate(7.00);
                    job2.setDescription("Morning shift with own motorcycle");
                    job2.setEmployer(employer);
                    jobRepository.save(job2);

                    System.out.println("✓ Sample Job Positions Created.");
                }
            }
        };
    }
}