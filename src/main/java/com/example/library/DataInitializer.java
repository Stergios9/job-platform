package com.example.library;

import com.example.library.entity.*;
import com.example.library.repository.*;
import com.example.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class DataInitializer {
//    @Autowired
//    private UserService userService;


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

            // 1. Δημιουργία 1ou Εργοδότη
            User boss1 = new User();
            boss1.setUsername("boss1");
            boss1.setCity("Athens");
            boss1.setPassword("12345678"); // Τουλάχιστον 8 χαρακτήρες βάσει Validation
            boss1.setEmail("boss1@gmail.com");
            boss1.setRole("ROLE_EMPLOYER");
            userService.registerUser(boss1);

            // Σύνδεση Εργοδότη με την εταιρεία
            Company company1 = new Company();
            company1.setName("Acropolis Group");
            company1.setAfm("111111111");
            company1.setRegistrationNumber("HE11111");
            company1.setVerified(true);
            company1.setUser(boss1);

            // Έναρξη συνδρομής για την εταιρεία του εργοδότη
            Subscription sub1 = new Subscription();
            sub1.setStartDate(LocalDate.now());
            sub1.setEndDate(LocalDate.now().plusYears(1));
            sub1.setActive(true);
            sub1.setCompany(company1);
            company1.setSubscription(sub1);
            companyRepository.save(company1);

            //  Δημιουργία Θέσης Εργασίας για την εταιρεία
            JobPosition job1 = new JobPosition();
            job1.setTitle("Waiter");
            job1.setCity("Athens");
            job1.setHourlyRate(8.50);
            job1.setDescription("Excellent waiter needed for Acropolis Group...");
            job1.setImageUrl("restaurant.jfif");
            job1.setCompany(company1);
            jobRepository.save(job1);
// ****************************************************************************** //

            // 1. Δημιουργία 2ou Εργοδότη
            User boss2 = new User();
            boss2.setUsername("boss2");
            boss2.setCity("Chalkida");
            boss2.setPassword("12345678");
            boss2.setEmail("boss2@gmail.com");
            boss2.setRole("ROLE_EMPLOYER");
            userService.registerUser(boss2);

            Company company2 = new Company();
            company2.setName("The Warehouse");
            company2.setAfm("222222222");
            company2.setRegistrationNumber("HE22222");
            company2.setVerified(true);
            company2.setUser(boss2);

            Subscription sub2 = new Subscription();
            sub2.setStartDate(LocalDate.now());
            sub2.setEndDate(LocalDate.now().plusYears(1));
            sub2.setActive(true);
            sub2.setCompany(company2);
            company2.setSubscription(sub2);
            companyRepository.save(company2);

            // Δημιουργία 1ης θέσης εργασίας για την εταιρεία company2
            JobPosition job2 = new JobPosition();
            job2.setTitle("Delivery");
            job2.setCity("Piraeus");
            job2.setHourlyRate(7.00);
            job2.setDescription("Reliable delivery driver for Acropolis Group...");
            job2.setImageUrl("delivery.jfif");
            job2.setCompany(company1);
            jobRepository.save(job2);

            // Δημιουργία 2ης θέσης εργασίας για την εταιρεία company2
            JobPosition job3 = new JobPosition();
            job3.setTitle("Storekeeper");
            job3.setCity("Chalkida");
            job3.setHourlyRate(6.00);
            job3.setDescription("We are seeking a storekeeper for our main warehouse!");
            job2.setImageUrl("apothikarios.png");
            job3.setCompany(company2);
            jobRepository.save(job3);
// ****************************************************************************** //

            // Δημιουργία εργαζόμενου
            User workerUser = new User();
            workerUser.setUsername("giannis");
            workerUser.setCity("Thessaloniki");
            workerUser.setPassword("12345678");
            workerUser.setEmail("giannis@hotmail.com");
            workerUser.setRole("ROLE_WORKER");
            userService.registerUser(workerUser);

            WorkerProfile profile = new WorkerProfile();
            profile.setUser(workerUser);
            profile.setProfession("Waiter");
            profile.setBio("Experienced waiter with 5 years in tourism.");
            profile.setProfileVerified(true);
            workerProfileRepository.save(profile);











            System.out.println("✓ Database fully initialized!");
        };
    }
}