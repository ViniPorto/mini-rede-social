package com.univille.mini_rede_social.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class ResponseHandlerTest {
    
    ResponseHandler responseHandler = new ResponseHandler();

    @Test
    void deveGerarMapCorretamente() {
        var response = this.responseHandler.generateMap("teste", Boolean.TRUE, HttpStatus.OK, null);

        Assertions.assertEquals(4, response.size());

        response = this.responseHandler.generateMap("teste", Boolean.TRUE, HttpStatus.OK, "teste");

        Assertions.assertEquals(5, response.size());
    }

    @Test
    void deveGerarResponseCorretamente() {
        var response = this.responseHandler.generateResponse("teste", Boolean.TRUE, HttpStatus.OK, "teste");

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
    }

}
