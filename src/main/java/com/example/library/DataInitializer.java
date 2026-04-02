package com.example.library;

import com.example.library.entity.Company;
import com.example.library.entity.JobPosition;
import com.example.library.entity.Subscription;
import com.example.library.entity.User;
import com.example.library.repository.CompanyRepository;
import com.example.library.repository.JobRepository;
import com.example.library.repository.SubscriptionRepository;
import com.example.library.repository.UserRepository;
import com.example.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;


    @Bean
    public CommandLineRunner loadData(UserService userService,
                                      UserRepository userRepository,
                                      CompanyRepository companyRepository,
                                      JobRepository jobRepository,
                                      SubscriptionRepository subscriptionRepository) {
        return args -> {
            // 1. Καθαρισμός δεδομένων (Σωστή σειρά!)
            subscriptionRepository.deleteAll();
            jobRepository.deleteAll();
            companyRepository.deleteAll();
            userRepository.deleteAll(); // Καθαρίζουμε και τους χρήστες για να μην έχουμε διπλότυπα

            // 2. Δημιουργία Χρηστών
            User boss1 = new User();
            boss1.setUsername("boss1"); // Απλό όνομα για να το βρίσκουμε εύκολα
            boss1.setCity("Athens");
            boss1.setPassword("123");
            boss1.setEmail("boss1@gmail.com");
            boss1.setRole("ROLE_EMPLOYER");
            userService.registerUser(boss1);

            User boss2 = new User();
            boss2.setUsername("boss2");
            boss2.setCity("Chalkida");
            boss2.setPassword("123");
            boss2.setEmail("boss2@gmail.com");
            boss2.setRole("ROLE_EMPLOYER");
            userService.registerUser(boss2);

            User worker = new User();
            worker.setUsername("giannis");
            worker.setCity("Thessaloniki");
            worker.setPassword("123");
            worker.setEmail("giannis@hotmail.com");
            worker.setRole("ROLE_WORKER");
            userService.registerUser(worker);

            // 3. Ανάκτηση των χρηστών από τη βάση (για να έχουν το ID και το σωστό state)
            User boss1FromDb = userRepository.findByUsername("boss1").get();
            User boss2FromDb = userRepository.findByUsername("boss2").get();

            // 4. Δημιουργία Εταιρείας 1
            Company company1 = new Company();
            company1.setName("Acropolis Group");
            company1.setAfm("111111111");
            company1.setUser(boss1FromDb);

            Subscription sub1 = new Subscription();
            sub1.setStartDate(LocalDate.now());
            sub1.setEndDate(LocalDate.now().plusYears(1));
            sub1.setActive(true);
            sub1.setCompany(company1);
            company1.setSubscription(sub1);

            // 5. Δημιουργία Εταιρείας 2
            Company company2 = new Company();
            company2.setName("The Warehouse");
            company2.setAfm("222222222");
            company2.setUser(boss2FromDb);

            Subscription sub2 = new Subscription();
            sub2.setStartDate(LocalDate.now());
            sub2.setEndDate(LocalDate.now().plusYears(1));
            sub2.setActive(true);
            sub2.setCompany(company2);
            company2.setSubscription(sub2);

            // Αποθήκευση Εταιρειών
            companyRepository.save(company1);
            companyRepository.save(company2);

            // 6. Δημιουργία Θέσεων Εργασίας
            JobPosition job1 = new JobPosition();
            job1.setTitle("Waiter");
            job1.setCity("Athens");
            job1.setHourlyRate(8.50);
            job1.setDescription("Excellent waiter needed for Acropolis Group...");
            job1.setImageUrl("ice-Cream.png");
            job1.setCompany(company1);
            jobRepository.save(job1);

            JobPosition job2 = new JobPosition();
            job2.setTitle("Delivery");
            job2.setCity("Piraeus");
            job2.setHourlyRate(7.00);
            job2.setDescription("Reliable delivery driver for Acropolis Group...");
            job2.setImageUrl("restaurant.jfif");
            job2.setCompany(company1);
            jobRepository.save(job2);

            JobPosition job3 = new JobPosition();
            job3.setTitle("Storekeeper");
            job3.setCity("Chalkida");
            job3.setHourlyRate(6.00);
            job3.setDescription("We are seeking for a storekeeper for our main warehouse!");
            job3.setImageUrl("apothikarios.png");
            job3.setCompany(company2);
            jobRepository.save(job3);

            System.out.println("✓ Database fully initialized with 2 Companies and 3 Jobs!");
        };
    }
}