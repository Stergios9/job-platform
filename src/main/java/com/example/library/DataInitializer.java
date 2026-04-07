package com.example.library;

import com.example.library.entity.*;
import com.example.library.repository.*;
import com.example.library.service.CompanyService;
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
    private BCryptPasswordEncoder passwordEncoder;


    @Bean
    public CommandLineRunner loadData(UserService userService,
                                      UserRepository userRepository,
                                      CompanyRepository companyRepository,
                                      JobRepository jobRepository,
                                      SubscriptionRepository subscriptionRepository,
                                      WorkerProfileRepository workerProfileRepository, CompanyService companyService) {
        return args -> {
            // 1. Καθαρισμός δεδομένων (Σωστή σειρά λόγω Foreign Keys)
            jobRepository.deleteAll();
            subscriptionRepository.deleteAll();
            workerProfileRepository.deleteAll();
            companyRepository.deleteAll();
            userRepository.deleteAll();

            // 1. Δημιουργία User (Απλό αντικείμενο, ΟΧΙ save ακόμα)
            // 1. Φτιάχνουμε τον User (ΜΟΝΟ στη μνήμη)
            User boss1 = new User();
            boss1.setUsername("boss1@gmail.com");
            boss1.setPassword(passwordEncoder.encode("12345678")); // Χειροκίνητο encode
            boss1.setRole("ROLE_EMPLOYER");
            boss1.setCity("Athens");

            Company company1 = new Company();
            company1.setName("Acropolis Group");
            company1.setAfm("111111111");
            companyService.createSubscription(company1);
            company1.setCertificatePath("C:User/.../uploads/viografiko.pdf");

            company1.setUser(boss1);
            boss1.setCompany(company1);

            companyService.saveCompany(company1);

            // 6. Μετά σώζουμε το Job γιατί χρειάζεται το ID της ήδη σωσμένης Company
            JobPosition job1 = new JobPosition();
            job1.setTitle("Waiter");
            job1.setCity("Athens");
            job1.setHourlyRate(8.50);
            job1.setDescription("We are looking for a friendly and efficient waiter to join our team at Acropolis Group. The ideal candidate will have excellent communication skills, a positive attitude, and the ability to work in a fast-paced environment. Responsibilities include taking orders, serving food and beverages, and ensuring customer satisfaction.");
            job1.setImageUrl("waiter.jfif");

            job1.setCompany(company1);
            // ... λοιπά πεδία
            jobRepository.save(job1);
// ****************************************************************************** //

//             1. Δημιουργία 2ou Εργοδότη
            User boss2 = new User();
            boss2.setUsername("boss2@gmail.com");
            boss2.setPassword(passwordEncoder.encode("87654321")); // Χειροκίνητο encode
            boss2.setRole("ROLE_EMPLOYER");
            boss2.setCity("Athens");


            Company company2 = new Company();
            company2.setName("Coffee Lab");
            company2.setAfm("222222222");
            companyService.createSubscription(company2);
            company2.setCertificatePath("C:User/.../uploads/viografiko2.pdf");

            company2.setUser(boss2);
            boss2.setCompany(company2);

            companyService.saveCompany(company2);

            // 6. Μετά σώζουμε το Job γιατί χρειάζεται το ID της ήδη σωσμένης Company
            JobPosition job2 = new JobPosition();
            job2.setTitle("Delivery");
            job2.setCity("Salamanina");
            job2.setHourlyRate(5.50);
            job2.setDescription("We are looking for a friendly and efficient waiter to deliver our products");
            job2.setImageUrl("delivery.jfif");

            job2.setCompany(company2);
            // ... λοιπά πεδία
            jobRepository.save(job2);


            User boss3 = new User();
            boss3.setUsername("boss3@gmail.com");
            boss3.setPassword(passwordEncoder.encode("87564321")); // Χειροκίνητο encode
            boss3.setRole("ROLE_EMPLOYER");
            boss3.setCity("Athens");

            Company company3 = new Company();
            company3.setName("Il Toto");
            company3.setAfm("222222233");
            companyService.createSubscription(company3);
            company3.setCertificatePath("C:User/.../uploads/viografiko2.pdf");

            company3.setUser(boss3);
            boss3.setCompany(company3);

            companyService.saveCompany(company3);
// ****************************************************************************** //

            // 1. Φτιάχνουμε τον User
            User workerUser = new User();
            workerUser.setUsername("giannis@hotmail.com");
            workerUser.setPassword(passwordEncoder.encode("12435678"));
            workerUser.setRole("ROLE_WORKER");
            workerUser.setCity("Nigrita");

            // 2. Φτιάχνουμε το Profile
            WorkerProfile profile = new WorkerProfile();
            profile.setProfession("Waiter");

            // 3. ΣΥΝΔΕΣΗ
            profile.setUser(workerUser);
            workerUser.setWorkerProfile(profile);

            // 4. ΣΩΖΟΥΜΕ ΜΟΝΟ ΤΟ PROFILE (ή τον User αν έχει Cascade ALL)
            workerProfileRepository.save(profile);


        };
    }
}