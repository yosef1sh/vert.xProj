import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ATest {
  Vertx vertx = Vertx.vertx();

  @Test
  @ExtendWith(VertxExtension.class)
  void httpRequest(VertxTestContext testContext) {
    WebClient webClient = WebClient.create(vertx);
    vertx.deployVerticle((Verticle) new App(), testContext.succeeding(id -> {
      webClient.get(8080, "localhost", "/healthcheck")
        .as(BodyCodec.string())
        .send(testContext.succeeding(resp -> {
          testContext.verify(() -> {
            assertThat(resp.statusCode()).isEqualTo(200);
            assertThat(resp.body()).contains("I'm alive!!!");
            testContext.completeNow();
          });
        }));
    }));
  }
  @Test
  @ExtendWith(VertxExtension.class)
  void httpRequest2(VertxTestContext testContext) {
    WebClient webClient = WebClient.create(vertx);
    vertx.deployVerticle((Verticle) new App(), testContext.succeeding(id -> {
      webClient.get(8080, "localhost", "/hello")
        .as(BodyCodec.string())
        .addQueryParam("name","XXX")
        .send(testContext.succeeding(resp -> {
          testContext.verify(() -> {
            assertThat(resp.statusCode()).isEqualTo(200);
            System.out.println(resp.body()+"j");
            assertThat(resp.body()).contains("Hi XXX");
            testContext.completeNow();
          });
        }));
    }));
  }
@Test
@ExtendWith(VertxExtension.class)
void httpRequest3(VertxTestContext testContext) {
  WebClient webClient = WebClient.create(vertx);
  vertx.deployVerticle((Verticle) new App(), testContext.succeeding(id -> {
    webClient.get(8080, "localhost", "/currentforecasts")
      .as(BodyCodec.string())
      .addQueryParam("city","XXX")
      .addQueryParam("country","XXX")
      .send(testContext.succeeding(resp -> {
        testContext.verify(() -> {
          assertThat(resp.statusCode()).isEqualTo(200);
          System.out.println(resp.body()+"j");
          assertThat(resp.body()).contains("Country and City does not exist");
          testContext.completeNow();
        });
      }));
  }));
}
  @Test
  @ExtendWith(VertxExtension.class)
  void httpRequest4(VertxTestContext testContext) {
    WebClient webClient = WebClient.create(vertx);
    vertx.deployVerticle((Verticle) new App(), testContext.succeeding(id -> {
      webClient.get(8080, "localhost", "/currentforecasts")
        .as(BodyCodec.string())
        .addQueryParam("city","Cairns")
        .addQueryParam("country","AU")
        .send(testContext.succeeding(resp -> {
          testContext.verify(() -> {
            assertThat(resp.statusCode()).isEqualTo(200);
            assertThat(resp.body()).contains("{\"country\":\"au\",\"city\":\"cairns\",\"temp\":22.629999999999995,\"humidity\":84,\"date\":\"2021-10-10\"}");
            testContext.completeNow();
          });
        }));
    }));
  }
@Test
@ExtendWith(VertxExtension.class)
void httpRequest5(VertxTestContext testContext) {
  WebClient webClient = WebClient.create(vertx);
  vertx.deployVerticle((Verticle) new App(), testContext.succeeding(id -> {
    webClient.get(8080, "localhost", "/forecasts")
      .as(BodyCodec.string())
      .addQueryParam("city","Begichevo")
      .addQueryParam("country","RU")
      .addQueryParam("days","3")
      .send(testContext.succeeding(resp -> {
        testContext.verify(() -> {
          assertThat(resp.statusCode()).isEqualTo(200);
          assertThat(resp.body()).contains("{\"forecasts\":[{\"date\":\"2021-10-10\",\"dayTemp\":6.706666666666649,\"minTemp\":5.03000000000003,\"maxTemp\":6.706666666666649},{\"date\":\"2021-10-11\",\"dayTemp\":6.246250000000032,\"minTemp\":6.246250000000032,\"maxTemp\":6.246250000000032},{\"date\":\"2021-10-12\",\"dayTemp\":7.746250000000032,\"minTemp\":7.746250000000032,\"maxTemp\":7.746250000000032}]}");
          testContext.completeNow();
        });
      }));
  }));
}
}
