package com.civalue.personalized_data.controller;


 static org.hamcrest.CoreMatchers.is;
 static org.mockito.Mockito.doReturn;
 static org.mockito.Mockito.doThrow;
 static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
 static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
 static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
 static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
 static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
 com.civalue.personalized_data.PersonalizedDataServiceApplication;
 com.civalue.personalized_data.dto.database.ProductDto;
 com.civalue.personalized_data.dto.request.PersonalizedAndProductRequestDto;
 com.civalue.personalized_data.dto.response.ProductResponseDto;
 com.civalue.personalized_data.repository.ProductDao;
 com.civalue.personalized_data.repository.ShopperDao;
 com.civalue.personalized_data.service.ProductAndShopperService;
 com.fasterxml.jackson.core.type.TypeReference;
 com.fasterxml.jackson.databind.ObjectMapper;
 com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
 java.io.File;
 java.text.DateFormat;
 java.text.SimpleDateFormat;
 java.util.ArrayList;
 java.util.List;
 org.junit.jupiter.api.Test;
 org.junit.jupiter.api.extension.ExtendWith;
 org.mockito.InjectMocks;
 org.mockito.Mockito;
 org.springframework.beans.factory.annotation.Autowired;
 org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
 org.springframework.boot.test.context.SpringBootTest;
 org.springframework.boot.test.mock.mockito.MockBean;
 org.springframework.http.MediaType;
 org.springframework.test.context.junit.jupiter.SpringExtension;
 org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {PersonalizedDataServiceApplication.class})
@AutoConfigureMockMvc
public class PersonalizedControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @InjectMocks
  PersonalizedController personalizedController;

  @MockBean
  ShopperDao shopperDao;

  @MockBean
  ProductDao productDao;

  @MockBean
  ProductAndShopperService productAndShopperService;

  @Test
  void test_createProduct_normal() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    mapper.setDateFormat(df);
    File requestFile =
        new File("src/test/java/com/civalue/personalized_data/controller/requestBody.json");
    PersonalizedAndProductRequestDto personalizedAndProductRequestDto =
        mapper.readValue(requestFile, new TypeReference<PersonalizedAndProductRequestDto>() {});
    String strRequestBody = mapper.writeValueAsString(personalizedAndProductRequestDto);
    doReturn("{}").when(productAndShopperService).insertProductAndShopperData(Mockito.any());
    this.mockMvc
        .perform(post("/products").contentType(MediaType.APPLICATION_JSON).content(strRequestBody))
        .andDo(print()).andExpect(status().isOk()).andExpect(content().string(is("{}")));
  }

  @Test
  void test_createProduct_exception() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    mapper.setDateFormat(df);
    File requestFile =
        new File("src/test/java/com/civalue/personalized_data/controller/requestBody.json");
    PersonalizedAndProductRequestDto personalizedAndProductRequestDto =
        mapper.readValue(requestFile, new TypeReference<PersonalizedAndProductRequestDto>() {});
    String strRequestBody = mapper.writeValueAsString(personalizedAndProductRequestDto);
    doThrow(new RuntimeException()).when(productAndShopperService)
        .insertProductAndShopperData(Mockito.any());
    this.mockMvc
        .perform(post("/products").contentType(MediaType.APPLICATION_JSON).content(strRequestBody))
        .andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  void test_getProductsByShopperId_notFound() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    mapper.setDateFormat(df);
    File getProductsResponseFile =
        new File("src/test/java/com/civalue/personalized_data/controller/productsResponse.json");
    List<ProductResponseDto> productResponseDto = new ArrayList<>();
    // mapper.readValue(getProductsResponseFile, new TypeReference<List<ProductResponseDto>>() {});
    String responseBody = mapper.writeValueAsString(productResponseDto);
    doReturn(new ArrayList<>()).when(shopperDao).getProductsByShopperid(Mockito.anyString(),
        Mockito.any(), Mockito.anyString(), Mockito.anyString());
    this.mockMvc
        .perform(get("/getProductsByShopperDetails/S-1004?limit=16&brand=Dairylounge&category=Milk")
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  void test_getProductsByShopperId_normal() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    mapper.setDateFormat(df);
    File getProductsResponseFile =
        new File("src/test/java/com/civalue/personalized_data/controller/productsResponse.json");
    List<ProductResponseDto> productResponseDto =
        mapper.readValue(getProductsResponseFile, new TypeReference<List<ProductResponseDto>>() {});
    List<ProductDto> serviceResponseDto = new ArrayList<>();
    serviceResponseDto.add(new ProductDto("MD-543564696", "Milk", "Dairylounge"));
    serviceResponseDto.add(new ProductDto("MD-543564695", "Milk", "Dairylounge"));
    // mapper.readValue(getProductsResponseFile, new TypeReference<List<ProductDto>>() {});
    String responseBody = mapper.writeValueAsString(productResponseDto);
    doReturn(serviceResponseDto).when(shopperDao).getProductsByShopperid(Mockito.anyString(),
        Mockito.any(), Mockito.anyString(), Mockito.anyString());
    this.mockMvc
        .perform(get("/getProductsByShopperDetails/S-1004?limit=16&brand=Dairylounge&category=Milk")
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk()).andExpect(content().json(responseBody));
  }

  @Test
  void test_getShoppersByProductId_normal() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    mapper.setDateFormat(df);
    List<String> shopperResponseDto = new ArrayList<>();
    shopperResponseDto.add("user1");
    shopperResponseDto.add("user2");
    String responseBody = mapper.writeValueAsString(shopperResponseDto);
    doReturn(shopperResponseDto).when(productDao).getShoppersByProductid(Mockito.anyString(),
        Mockito.any());
    this.mockMvc
        .perform(get("/getShoppersByProductDetails/BB-2144746855?limit=4")
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk()).andExpect(content().json(responseBody));
  }

  @Test
  void test_getShoppersByProductId_notFound() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    mapper.setDateFormat(df);
    List<String> shopperResponseDto = new ArrayList<>();
    String responseBody = mapper.writeValueAsString(shopperResponseDto);
    doReturn(shopperResponseDto).when(productDao).getShoppersByProductid(Mockito.anyString(),
        Mockito.any());
    this.mockMvc
        .perform(get("/getShoppersByProductDetails/BB-2144746855?limit=4")
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isBadRequest());
  }
}
