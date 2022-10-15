package ru.job4j.quartz;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class HabrCareerParse {
    private static final String SOURCE_LINK = "http://career.habr.com";
    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);
    private static final int END_PAGE = 5;

    public static void main(String[] args) throws IOException {
        Connection connection = Jsoup.connect(PAGE_LINK);
        Document document = connection.get();
        Elements rows = document.select(".vacancy-card__inner");
        Element titleElement = rows.select(".vacancy-card__title").first();
        Element linkElement = titleElement.child(0);
        String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
        System.out.println(retrieveDescription(link));
    }

    private static void pageTurner() throws IOException {
        for (int i = 1; i <= END_PAGE; i++) {
            String url = String.format("%s%s%d", PAGE_LINK, "?page=", i);
            parse(url);
        }
    }

    private static void parse(String url) throws IOException {
        Connection connection = Jsoup.connect(url);
        Document document = connection.get();
        Elements rows = document.select(".vacancy-card__inner");
        rows.forEach(row -> {
            Element titleElement = row.select(".vacancy-card__title").first();
            Element linkElement = titleElement.child(0);
            Element dateElement = row.select(".vacancy-card__date").first().child(0);
            String vacancyName = titleElement.text();
            String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
            String date = dateElement.attr("datetime");
            System.out.printf("%s %s %s%n", vacancyName, link, date);
        });
    }

    private static String retrieveDescription(String link) throws IOException {
        Connection connection = Jsoup.connect(link);
        Document document = connection.get();
        Elements descriptionElements = document.select(".style-ugc");
        return descriptionElements.text();
    }
}
