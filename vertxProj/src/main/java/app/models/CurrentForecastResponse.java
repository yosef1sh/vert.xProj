package app.models;

import io.vertx.core.json.JsonObject;

import java.time.LocalDateTime;

public class CurrentForecastResponse {
  public static final double KELVIN_TO_CELSIUS = 273.15;
  public String country;
  public String city;
  public Double temp;
  public Integer humidity;
  public String date;
  public CurrentForecastResponse(String countryInput, String cityInput, JsonObject jsonResponse) {
    country = countryInput;
    city = cityInput;

    String strMain = jsonResponse.getString("main");
    if (strMain != null) {
      String strMa[] = strMain.split(",");

      String timeDate = LocalDateTime.now().toString();
      String dateArr[] = timeDate.split("T", 2);
      date = dateArr[0];

      String tempArr[] = strMa[0].split("=");
      temp = Double.parseDouble(tempArr[1]) - KELVIN_TO_CELSIUS;

      String humidity1[] = strMa[5].split("=");
      String humidity2 = humidity1[1].replace("}", "");
      humidity = Integer.parseInt(humidity2);
    } else {
      throw new IllegalArgumentException("Country and City does not exist");
    }
  }
}
