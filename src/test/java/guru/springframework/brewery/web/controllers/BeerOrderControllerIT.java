package guru.springframework.brewery.web.controllers;

import guru.springframework.brewery.domain.Customer;
import guru.springframework.brewery.repositories.CustomerRepository;
import guru.springframework.brewery.web.model.BeerOrderPagedList;
import guru.springframework.brewery.web.model.BeerPagedList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BeerOrderControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    CustomerRepository customerRepository;
    Customer customer;


    @BeforeEach
    void setup() {
        customer = customerRepository.findAll().get(0);
    }

    @Test
    void testListBeers() {
        BeerOrderPagedList beerPagedList = restTemplate.getForObject("/api/v1/customers/" + customer.getId().toString() + "/orders", BeerOrderPagedList.class);

        assertThat(beerPagedList.getContent()).hasSize(1);
    }
}
