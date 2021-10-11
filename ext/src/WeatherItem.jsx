

export function weatherItem(weatherData) {
    return <div className="result-container">

        <p>
            <section>
                <strong>Date: </strong><span className="cases">{weatherData?.date}</span>
            </section>
            <section>
                <strong>Temp: </strong><span className="cases">{weatherData?.temp || weatherData?.dayTemp}</span>
            </section>

        </p>
    </div>;
}