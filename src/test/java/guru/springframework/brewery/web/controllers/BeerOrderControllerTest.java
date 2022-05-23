package guru.springframework.brewery.web.controllers;

import guru.springframework.brewery.domain.BeerOrder;
import guru.springframework.brewery.services.BeerOrderService;
import guru.springframework.brewery.web.model.BeerDto;
import guru.springframework.brewery.web.model.BeerOrderDto;
import guru.springframework.brewery.web.model.BeerOrderPagedList;
import guru.springframework.brewery.web.model.OrderStatusEnum;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static guru.springframework.brewery.web.model.BeerStyleEnum.PALE_ALE;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {BeerOrderController.class})
class BeerOrderControllerTest {

    @MockBean
    BeerOrderService beerOrderService;
    @Autowired
    MockMvc mockMvc;

    @Captor
    ArgumentCaptor<UUID> uuidCustomerIdCaptor;
    @Captor
    ArgumentCaptor<UUID> uuidOrderIdCaptor;
    @Captor
    ArgumentCaptor<Pageable> pageableCaptor;

    BeerOrderDto beerOrderDto;
    BeerOrderPagedList beerOrderDtos;

    @BeforeEach
    void setUp() {
        beerOrderDto = BeerOrderDto.builder()
                .id(UUID.randomUUID())
                .version(1)
                .createdDate(OffsetDateTime.now())
                .lastModifiedDate(OffsetDateTime.now())
                .customerId(UUID.randomUUID())
                .beerOrderLines(List.of())
                .orderStatus(OrderStatusEnum.NEW)
                .orderStatusCallbackUrl("callback-url")
                .customerRef("123456789012L")
                .build();
        beerOrderDtos = new BeerOrderPagedList(List.of(beerOrderDto), PageRequest.of(1, 1), 1);
        when(beerOrderService.getOrderById(uuidCustomerIdCaptor.capture(), uuidOrderIdCaptor.capture())).thenReturn(beerOrderDto);
        when(beerOrderService.listOrders(uuidCustomerIdCaptor.capture(), pageableCaptor.capture())).thenReturn(beerOrderDtos);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(beerOrderService);
    }

    @Test
    void getOrder() throws Exception {
        mockMvc.perform(get("/api/v1/customers/"+ UUID.randomUUID() + "/orders/" + UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.version", is(1)))
                .andExpect(jsonPath("$.customerRef", is("123456789012L")))
        ;
    }
    @Test
    void listOrders() throws Exception {
        mockMvc.perform(get("/api/v1/customers/" + UUID.randomUUID() + "/orders")
                .param("pageNumber", "1")
                .param("pageSize", "1"))
                .andExpect(status().isOk())
        ;
    }
}