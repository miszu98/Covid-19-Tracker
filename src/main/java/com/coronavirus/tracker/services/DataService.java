package com.coronavirus.tracker.services;

import com.coronavirus.tracker.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataService {

    private static String DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    private List<Integer> cases = new ArrayList<>();
    private List<Integer> prevDayCases = new ArrayList<>();
    private List<LocationStats> allStats = new ArrayList<>();

    private int sumOfCases = 0;
    private int sumOfPrevDayCases = 0;

    private NumberFormat nf = NumberFormat.getInstance();

    public String getSumOfCases() {
        nf.setGroupingUsed(true);
        return nf.format(sumOfCases);
    }

    public List<LocationStats> getAllStats() {
        return allStats;
    }

    public String getSumOfPrevDayCases() {
        nf.setGroupingUsed(true);
        return nf.format(sumOfPrevDayCases);
    }

    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public void fetchVirusData() throws IOException, InterruptedException {
        List<LocationStats> newStats = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(DATA_URL)).build();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        StringReader csvBodyReader = new StringReader(httpResponse.body());

        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);

        for (CSVRecord record : records) {
            LocationStats locationStat = new LocationStats();

            locationStat.setState(record.get("Province/State"));
            locationStat.setCountry(record.get("Country/Region"));
            locationStat.setLatestTotalCases(Integer.parseInt(record.get(record.size() - 1)));
            locationStat.setDiffCases(Integer.parseInt(record.get(record.size() - 1)) - Integer.parseInt(record.get(record.size() - 2)));
            cases.add(Integer.parseInt(record.get(record.size() - 1)));
            prevDayCases.add(Integer.parseInt(record.get(record.size() - 2)));
            newStats.add(locationStat);

        }
        this.allStats = newStats;

        for (Integer x : cases) {
            sumOfCases += x;
        }

        for (Integer z : prevDayCases) {
            sumOfPrevDayCases += z;
        }




    }
}
