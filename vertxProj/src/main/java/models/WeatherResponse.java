package models;

public class WeatherResponse {
  private Integer id;
  private String name;
  private String state;
  private String country;
  Coord CoordObject;


  // Getter Methods

  public Integer getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getState() {
    return state;
  }

  public String getCountry() {
    return country;
  }

  public Coord getCoord() {
    return CoordObject;
  }

  // Setter Methods

  public void setId(Integer id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "WeatherResponse{" +
      "id=" + id +
      ", name='" + name + '\'' +
      ", state='" + state + '\'' +
      ", country='" + country + '\'' +
      ", CoordObject=" + CoordObject +
      '}';
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setState(String state) {
    this.state = state;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public void setCoord(Coord coordObject) {
    this.CoordObject = coordObject;
  }
}
