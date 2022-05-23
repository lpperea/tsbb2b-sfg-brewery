package guru.springframework.brewery.web.controllers;

import guru.springframework.brewery.services.BeerService;
import guru.springframework.brewery.web.model.BeerDto;
import guru.springframework.brewery.web.model.BeerPagedList;
import guru.springframework.brewery.web.model.BeerStyleEnum;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static guru.springframework.brewery.web.model.BeerStyleEnum.PALE_ALE;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeerController.class)
class BeerControllerTest {

    @MockBean
    private BeerService beerService;

    BeerDto beerDto;
    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        beerDto = BeerDto.builder()
                .id(UUID.randomUUID())
                .version(1)
                .beerName("Cruzcampo")
                .beerStyle(PALE_ALE)
                .price(new BigDecimal("12.99"))
                .quantityOnHand(4)
                .upc(123456789012L)
                .createdDate(OffsetDateTime.now())
                .lastModifiedDate(OffsetDateTime.now())
                .build();
    }

    @AfterEach
    void tearDown() {
        reset(beerService);
    }

    @Test
    void getBeerById() throws Exception {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
        when(beerService.findBeerById(any(UUID.class))).thenReturn(beerDto);

        var res = mockMvc.perform(get("/api/v1/beer/" + UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(beerDto.getId().toString())))
                .andExpect(jsonPath("$.beerName", is(beerDto.getBeerName())))
                .andExpect(jsonPath("$.createdDate", is(dtf.format(beerDto.getCreatedDate()))))
                .andReturn();
        System.out.println(res.getResponse().getContentAsString());
    }

    @DisplayName("Test list - ")
    @Nested
    public class TestListOperations {

        @Captor
        ArgumentCaptor<String> captorName;
        @Captor
        ArgumentCaptor<BeerStyleEnum> captorStyle;
        @Captor
        ArgumentCaptor<PageRequest> captorPage;

        BeerDto beerDto1;
        BeerPagedList beers;

        @BeforeEach
        void setUp() {
            beerDto1 = BeerDto.builder().id(UUID.randomUUID()).version(1).beerName("Aguila").beerStyle(BeerStyleEnum.PORTER).price(new BigDecimal("13.99")).quantityOnHand(3).upc(323456789012L).createdDate(OffsetDateTime.now()).lastModifiedDate(OffsetDateTime.now()).build();
            beers = new BeerPagedList(List.of(beerDto, beerDto1), PageRequest.of(1, 1), 2);
            given(beerService.listBeers(captorName.capture(), captorStyle.capture(), captorPage.capture())).willReturn(beers);
        }

        @DisplayName("no params")
        @Test
        void listBeersNoParams() throws Exception {
            mockMvc.perform(get("/api/v1/beer"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.content[0].id", is(beerDto.getId().toString())))
                    .andExpect(jsonPath("$.content[1].id", is(beerDto1.getId().toString())))
            ;
            assertNull(captorName.getValue());
            assertNull(captorStyle.getValue());
            assertEquals(PageRequest.of(0, 25), captorPage.getValue());
        }

        @DisplayName("with params")
        @Test
        void listBeersPageNumberAndPageSizePositiveValues() throws Exception {
            mockMvc.perform(get("/api/v1/beer")
                            .param("pageNumber", "2")
                            .param("pageSize", "15")
                            .param("beerName", "Cruzcampo")
                            .param("beerStyle", "PALE_ALE"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.content[0].id", is(beerDto.getId().toString())))
                    .andExpect(jsonPath("$.content[1].id", is(beerDto1.getId().toString())))
            ;
            assertEquals("Cruzcampo", captorName.getValue());
            assertEquals(PALE_ALE, captorStyle.getValue());
            assertEquals(PageRequest.of(2, 15), captorPage.getValue());
        }
    }
}