import { useEffect, useState, useRef } from "react";
import './App.css';
import axios from 'axios';
import { useLocalStorage } from "./useLocalStorage";
import { weatherItem } from "./WeatherItem";
const baseURL = 'https://agile-bayou-81380.herokuapp.com/';
function App() {
  const [currentWeather, setCurrentWeather] = useState({});
  const [weekWeather, setWeekWeather] = useState({ forecasts: [] });
  const [country, setCountry] = useLocalStorage("country", ``);
  const [city, setCity] = useLocalStorage("city", ``);
  const [reset, setReset] = useState(!country || !city);
  useEffect(async () => {
    const result = await axios.get(`currentforecasts`, {
      baseURL,
      params: { city, country }
    });
    setCurrentWeather(result.data);
  }, [])

  useEffect(async () => {
    const result = await axios.get(`forecasts`, {
      baseURL,
      params: { city, country, days: 5 }
    });
    setWeekWeather(result.data);
  }, [])
  const handleSubmit = async (e) => {
    setReset(false);
    e?.preventDefault();
    const result = await axios.get(`currentforecasts`, {
      baseURL,
      params: {
        city,
        country
      }
    })
    const result1 = await axios.get(`forecasts`, {
      baseURL,
      params: { city, country, days: 5 }
    });
    setCurrentWeather(result.data);
    setWeekWeather(result1.data);


    console.log(currentWeather?.temp)
  };
  return (
    <div className="App">
      {reset &&
        <form className="form-data" onSubmit={handleSubmit}>
          <div className="enter-country">
            Enter a country :
          </div>
          <div>
            <input value={country} onChange={(ev) => setCountry(ev.target.value)} type="text" className="country-name" />
          </div>
          <div className="enter-city">
            Enter a city :
          </div>
          <div>
            <input value={city} onChange={(ev) => setCity(ev.target.value)} type="text" className="city-name" />
          </div>
          <button className="search-btn">Search</button>
        </form>}
      {!reset && <>
        <button onClick={() => setReset(true)}>Change location</button>
      </>}
      <div className="result">
        {weatherItem(currentWeather)}
        {weekWeather?.forecasts?.slice(1)?.map(data => weatherItem(data))}
      </div>

    </div>
  );
}

export default App;
