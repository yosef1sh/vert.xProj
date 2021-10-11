package app.main;

import app.models.CurrentForecastResponse;
import app.models.DayWeather;
import app.models.WeatherResponse;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


public class App extends AbstractVerticle {
  public static final double KELVIN_TO_CELSIUS = 273.15;

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    HttpServer httpServer = vertx.createHttpServer();
    Router router = Router.router(vertx);

    Set<String> allowedHeaders = new HashSet<>();
    allowedHeaders.add("x-requested-with");
    allowedHeaders.add("Access-Control-Allow-Origin");
    allowedHeaders.add("origin");
    allowedHeaders.add("Content-Type");
    allowedHeaders.add("accept");
    allowedHeaders.add("X-PINGARUNER");
    Set<HttpMethod> allowedMethods = new HashSet<>();
    allowedMethods.add(HttpMethod.GET);
    allowedMethods.add(HttpMethod.POST);
    allowedMethods.add(HttpMethod.OPTIONS);
    allowedMethods.add(HttpMethod.DELETE);
    allowedMethods.add(HttpMethod.PATCH);
    allowedMethods.add(HttpMethod.PUT);

    router.route()
      .handler(
        CorsHandler.create()
          .allowedHeaders(allowedHeaders)
          .allowCredentials(true)
          .allowedMethods(allowedMethods)
      )
      .handler(BodyHandler.create());

    Map < String, Integer > mapCity = new HashMap<>();
    vertx
      .fileSystem()
      .readFile("city.list.json", result -> {
      if(result.succeeded()) {
        String str = String.valueOf(result.result());
        JsonArray jsonArr = new JsonArray(str);
        String strCou = new String();
        String strName = new String();
        Integer id;
        for (int i = 0; i < jsonArr.getList().size(); i++) {
          WeatherResponse temp = jsonArr.getJsonObject(i).mapTo(WeatherResponse.class);
          strCou = temp.getCountry().toLowerCase(Locale.ROOT);
          strName = temp.getName().toLowerCase(Locale.ROOT);
          id = temp.getId();
          mapCity.put(strCou + "," + strName, id);
        }
      } else {
        System.err.println("Oh oh ..." + result.cause());
      }
    });

    router
      .get("/healthcheck")
      .handler(routingContext -> {
        HttpServerResponse response = routingContext.response();
        response.setChunked(true);
        response.write("I'm alive!!!");
        response.end();
      });


    router
      .get("/locations")
      .handler(routingContext -> {
        HttpServerResponse response = routingContext.response();
        response.setChunked(true);
        response.write(mapCity.toString());
        response.end();
      });

    router
      .get("/hello")
      .handler(routingContext -> {
        String name = routingContext.request().getParam("name");
        HttpServerResponse response = routingContext.response();
        response.setChunked(true);
        if(name == null) {
          name = "";
        }
        response.write("Hi " + name + "\n");
        response.end();
      });

    router
      .get("/currentforecasts")
      .handler(routingContext -> {
        String city = routingContext.request().getParam("city").toLowerCase(Locale.ROOT);
        String country = routingContext.request().getParam("country").toLowerCase(Locale.ROOT);
        String url = "api.openweathermap.org";
        WebClient client = WebClient.create(vertx);
        client.get(url, "/data/2.5/weather?q=" + city + "," + country + "&appid=410cba8a690ccfb68c9a969282e7c3d8")
          .as(BodyCodec.string()).send(ar -> {
          HttpServerResponse res = routingContext.response();
          res.setChunked(true);
          String jsonResponse;

          if(ar.succeeded()) {
            try {
              JsonObject jsonObject = new JsonObject(ar.result().body());
              jsonResponse = JsonObject.mapFrom(new CurrentForecastResponse(country, city, jsonObject)).toString();
            } catch (IllegalArgumentException exception) {
              jsonResponse = exception.getMessage();
            }
          }
          else {
            ar.cause().printStackTrace();
            jsonResponse = ar.cause().getMessage();
          }
          res.write(jsonResponse);
          res.end();
        });

      });

    router
      .get("/forecasts")
      .handler(routingContext -> {
        HttpServerResponse res = routingContext.response();
        try {
          String city = routingContext.request().getParam("city").toLowerCase(Locale.ROOT);
          String country = routingContext.request().getParam("country").toLowerCase(Locale.ROOT);
          String days = routingContext.request().getParam("days");
          LocalDate finalDay = LocalDate.now().plusDays(Long.parseLong(days));
          if (Long.parseLong(days) > 5) throw new IllegalArgumentException("Cannot get more than 5 days");
          String url = "api.openweathermap.org";
          WebClient client = WebClient.create(vertx);
          Integer cityId = mapCity.get(country + "," + city);
          if (cityId == null) throw new IllegalArgumentException("Invalid country and\\or city");
          client.get(url, "/data/2.5/forecast?id=" + cityId + "&appid=410cba8a690ccfb68c9a969282e7c3d8")
            .as(BodyCodec.string()).send(ar -> {
            if(ar.succeeded())
            {
              JsonObject json = new JsonObject(ar.result().body());
              JsonArray jsonArr = json.getJsonArray("list");
              Integer jsonArrSize = jsonArr.getList().size();
              List<DayWeather> dayWeathers = new ArrayList<>();
              for (int i = 0; i < jsonArrSize; i++) {
                JsonObject current = jsonArr.getJsonObject(i);
                JsonObject currentMain = current.getJsonObject("main");
                JsonObject obj = new JsonObject()
                  .put("date", current.getString("dt_txt").split(" ")[0])
                  .put("dayTemp", currentMain.getDouble("temp"))
                  .put("minTemp", currentMain.getDouble("temp_min"))
                  .put("maxTemp", currentMain.getDouble("temp_max"));
                dayWeathers.add(obj.mapTo(DayWeather.class));
              }

              Map<String, List<DayWeather>> grouped = dayWeathers
                .stream()
                .filter(dayWeather -> finalDay.isAfter(LocalDate.parse(dayWeather.getDate())))
                .collect(Collectors.groupingBy(DayWeather::getDate));
              Map<String, DayWeather> averaged = new HashMap<>();
              grouped.forEach((key, value) -> {
                DayWeather dayAverage = new DayWeather();
                dayAverage.setDate(key);
                dayAverage.setDayTemp(value
                  .stream()
                  .mapToDouble(DayWeather::getDayTemp)
                  .average()
                  .orElse(Double.NaN) - KELVIN_TO_CELSIUS);
                dayAverage.setMaxTemp(value
                  .stream()
                  .mapToDouble(DayWeather::getMaxTemp)
                  .average()
                  .orElse(Double.NaN) - KELVIN_TO_CELSIUS);
                dayAverage.setMinTemp(value
                  .stream()
                  .mapToDouble(DayWeather::getMinTemp)
                  .average()
                  .orElse(Double.NaN) - KELVIN_TO_CELSIUS);

                averaged.put(key, dayAverage);
              });
              JsonObject responseJson = new JsonObject();
              List<DayWeather> weatherResponse = new ArrayList<>(averaged.values());
              weatherResponse.sort((a, b) -> {
                LocalDate firstDate = LocalDate.parse(a.getDate());
                LocalDate secondDate = LocalDate.parse(b.getDate());
                return firstDate.isEqual(secondDate) ? 0 : firstDate.isAfter(secondDate) ? 1 : -1;
              });
              responseJson.put("forecasts", weatherResponse);
              res.setChunked(true);
              res.write(responseJson.toString());
              res.end();
            }
            else {
              ar.cause().printStackTrace();
            }
          });
        } catch (Exception e) {
          res.setChunked(true);
          res.write(e.getMessage());
          res.end();
        }
      });

    httpServer.requestHandler(router).listen(Integer.parseInt(args[1]));
  }
}
