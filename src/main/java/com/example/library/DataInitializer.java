package com.example.library;

import com.example.library.entity.*;
import com.example.library.repository.*;
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
                                      WorkerProfileRepository workerProfileRepository) {
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

            company1.setUser(boss1);
            boss1.setCompany(company1);

            companyRepository.save(company1);

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

            // 1. Δημιουργία 2ou Εργοδότη
//            User boss2 = new User();
//            boss2.setUsername("boss2@gmail.com");
//            boss2.setCity("Chalkida");
//            boss2.setPassword("12345678");
//            boss2.setRole("ROLE_EMPLOYER");
//            userService.registerUser(boss2);
//
//            Company company2 = new Company();
//            company2.setName("The Warehouse");
//            company2.setAfm("222222222");
//            company2.setRegistrationNumber("HE22222");
//            company2.setCertificatePath("/home/user/Documents/certificate2.pdf");
//            company2.setVerified(true);
//            company2.setUser(boss2);
//
//            Subscription sub2 = new Subscription();
//            sub2.setStartDate(LocalDate.now());
//            sub2.setEndDate(LocalDate.now().plusYears(1));
//            sub2.setActive(true);
//            sub2.setCompany(company2);
//            company2.setSubscription(sub2);
//            companyRepository.save(company2);
//
//            // Δημιουργία 1ης θέσης εργασίας για την εταιρεία company2
//            JobPosition job2 = new JobPosition();
//            job2.setTitle("Delivery");
//            job2.setCity("Piraeus");
//            job2.setHourlyRate(7.00);
//            job2.setDescription("Reliable delivery driver for Acropolis Group...");
//            job2.setImageUrl("delivery.jfif");
//            job2.setCompany(company2);
//            jobRepository.save(job2);
//
//            // Δημιουργία 2ης θέσης εργασίας για την εταιρεία company2
//            JobPosition job3 = new JobPosition();
//            job3.setTitle("Storekeeper");
//            job3.setCity("Chalkida");
//            job3.setHourlyRate(6.00);
//            job3.setDescription("We are seeking a storekeeper for our main warehouse!");
//            job3.setImageUrl("storeman.jpeg");
//            job3.setCompany(company2);
//            jobRepository.save(job3);
// ****************************************************************************** //

// 1. Φτιάχνουμε τον User
            User workerUser = new User();
            workerUser.setUsername("giannis@hotmail.com");
            workerUser.setPassword(passwordEncoder.encode("12345678"));
            workerUser.setRole("ROLE_WORKER");
            workerUser.setCity("Nigrita");

// 2. Φτιάχνουμε το Profile
            WorkerProfile profile = new WorkerProfile();
            profile.setProfession("Waiter");
// ... υπόλοιπα πεδία ...

// 3. ΣΥΝΔΕΣΗ
            profile.setUser(workerUser);
            workerUser.setWorkerProfile(profile);

// 4. ΣΩΖΟΥΜΕ ΜΟΝΟ ΤΟ PROFILE (ή τον User αν έχει Cascade ALL)
            workerProfileRepository.save(profile);


        };
    }
}