package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

public class HabrCareerParse implements Parse {

    private static final String SOURCE_LINK = "http://career.habr.com";
    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);
    private static final int END_PAGE = 5;
    private final DateTimeParser dateTimeParser;
    private static final Logger LOG = LogManager.getLogger(HabrCareerParse.class.getName());

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    private Post parse(Element row) {
        Element titleElement = row.select(".vacancy-card__title").first();
        Element linkElement = titleElement.child(0);
        Element dateElement = row.select(".vacancy-card__date").first().child(0);
        String title = titleElement.text();
        String postLink = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
        String date = dateElement.attr("datetime");
        return new Post(title, postLink, retrieveDescription(postLink),
                dateTimeParser.parse(date));
    }

    private static String retrieveDescription(String link) {
        Connection connection = Jsoup.connect(link);
        Document document;
        try {
            document = connection.get();
        } catch (IOException e) {
            throw new IllegalArgumentException("неправильно указана ссылка на описание вакансии");
        }
        Elements descriptionElements = document.select(".style-ugc");
        return descriptionElements.text();
    }

    @Override
    public List<Post> list(String link) {
        List<Post> list = new ArrayList<>();
        for (int i = 1; i <= END_PAGE; i++) {
            String url = String.format("%s%s%d", link, "?page=", i);
            Connection connection = Jsoup.connect(url);
            Document document;
            try {
                document = connection.get();
            } catch (IOException e) {
                throw new IllegalArgumentException("неправильно указан адрес");
            }
            Elements rows = document.select(".vacancy-card__inner");
            for (var row : rows) {
                list.add(parse(row));
            }
        }
        LOG.info("parsing completed");
        return list;
    }

    public static void main(String[] args) {
        DateTimeParser dateTimeParser = new HabrCareerDateTimeParser();
        HabrCareerParse habrCareerParse = new HabrCareerParse(dateTimeParser);
        System.out.println("Начало парсинга вакансий. Пожалуйста подождите...");
        List<Post> list = habrCareerParse.list(PAGE_LINK);
        System.out.printf("Окончание парсинга. Всего %d вакансий.\n", list.size());
        Store memory = new MemStore();
        System.out.println("Перемещение данных в базу...");
        for (var post : list) {
            memory.save(post);
        }
        System.out.println("Окончание перемещения.");
        System.out.println("Вывод вакансии №3:");
        System.out.println(memory.findById(2));
    }
}
