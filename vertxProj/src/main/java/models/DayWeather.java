package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DayWeather {
  private String date;
  private Double dayTemp;
  private Double minTemp;
  private Double maxTemp;

  public String getDate() {
    return date;
  }
  public Double getDayTemp() {
    return dayTemp;
  }
  public Double getMinTemp() {
    return minTemp;
  }

  public Double getMaxTemp() {
    return maxTemp;
  }

  // Setter Methods

  public void setDate(String date) {
    this.date = date;
  }

  public void setDayTemp(Double dayTemp) {
    this.dayTemp = dayTemp;
  }

  public void setMinTemp(Double minTemp) {
    this.minTemp = minTemp;
  }

  public void setMaxTemp(Double maxTemp) {
    this.maxTemp = maxTemp;
  }
}

