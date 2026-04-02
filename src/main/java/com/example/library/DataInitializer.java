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
                                      CompanyRepository companyRepository, // Χρειάζεσαι αυτό το Repository
                                      JobRepository jobRepository) {
        return args -> {
            // 1. Καθαρισμός δεδομένων (με τη σωστή σειρά λόγω foreign keys)

            subscriptionRepository.deleteAll();
            jobRepository.deleteAll();
            companyRepository.deleteAll();
            // Προσοχή: το userRepository.deleteAll() αν το θέλεις

            // 2. Δημιουργία Χρηστών
            if (userRepository.findByUsername("boss").isEmpty()) {
                User employer = new User();
                employer.setUsername("boss");
                employer.setCity("Athens");
                employer.setPassword("123");
                employer.setEmail("boss@gmail.com");
                employer.setRole("ROLE_EMPLOYER");
                userService.registerUser(employer);

                User worker = new User();
                worker.setUsername("giannis");
                worker.setCity("Thessaloniki");
                worker.setPassword("123");
                worker.setEmail("giannis@hotmail.com");
                worker.setRole("ROLE_WORKER");
                userService.registerUser(worker);
            }

            User bossFromDb = userRepository.findByUsername("boss").get();

            // 3. Δημιουργία Εταιρείας για τον "boss"
            Company myCompany = new Company();
            myCompany.setName("Acropolis Group");
            myCompany.setAfm("123456789");
            myCompany.setUser(bossFromDb); // Σύνδεση με τον User

            // 4. Δημιουργία Συνδρομής (Ενεργή για 1 χρόνο)
            Subscription sub = new Subscription();
            sub.setStartDate(LocalDate.now());
            sub.setEndDate(LocalDate.now().plusYears(1));
            sub.setActive(true);
            sub.setCompany(myCompany);

            myCompany.setSubscription(sub); // Σύνδεση συνδρομής με εταιρεία
            companyRepository.save(myCompany); // Σώζει και τη συνδρομή λόγω CascadeType.ALL

            // 5. Δημιουργία Θέσεων Εργασίας
            JobPosition job1 = new JobPosition();
            job1.setTitle("Waiter");
            job1.setCity("Athens");
            job1.setHourlyRate(8.50);
            job1.setDescription("Looking for a friendly and efficient waiter to join our team at Acropolis Group. Responsibilities include taking orders, serving food and drinks, and ensuring customer satisfaction. Previous experience in a similar role is a plus, but we are willing to train the right candidate. If you have excellent communication skills and a positive attitude, we would love to hear from you!");
            job1.setImageUrl("ice-Cream.png");
            job1.setCompany(myCompany); // Σύνδεση με την Εταιρεία
            jobRepository.save(job1);

            JobPosition job2 = new JobPosition();
            job2.setTitle("Delivery");
            job2.setCity("Piraeus");
            job2.setHourlyRate(7.00);
            job2.setDescription("We are seeking a reliable and efficient delivery driver to join our team at Acropolis Group. The ideal candidate will have a valid driver's license, a clean driving record, and excellent time management skills. Responsibilities include delivering food orders to customers in a timely manner while ensuring the safety of the food during transit. If you are punctual, customer-oriented, and have a passion for delivering great service, we encourage you to apply!");
            job2.setImageUrl("restaurant.jfif");
            job2.setCompany(myCompany); // Σύνδεση με την Εταιρεία
            jobRepository.save(job2);

            System.out.println("✓ Database initialized with Company, Subscription and Jobs!");
        };
    }
}