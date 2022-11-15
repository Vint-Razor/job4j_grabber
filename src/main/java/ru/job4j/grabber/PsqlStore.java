package ru.job4j.grabber;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {

    private final Connection cnn;

    public PsqlStore(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("grab.jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalArgumentException("Некорректное значения jdbc драйвера"
                    + " в конфигурационном файле");
        }
        try {
            cnn = DriverManager.getConnection(
                    cfg.getProperty("grab.jdbc.url"),
                    cfg.getProperty("grab.jdbc.username"),
                    cfg.getProperty("grab.jdbc.password")
            );
        } catch (SQLException e) {
            throw new IllegalArgumentException("Неправильные значения в конфигурационном файле,"
                    + "либо сервер БД недоступен, поэтому соединение невозможно");
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement statement = cnn.prepareStatement(
                "INSERT INTO post(name, text, link, created) values (?, ?, ?, ?)"
                        + "ON CONFLICT DO NOTHING",
                Statement.RETURN_GENERATED_KEYS
        )) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getDescription());
            statement.setString(3, post.getLink());
            statement.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            statement.executeUpdate();
            try (ResultSet generatedKey = statement.getGeneratedKeys()) {
                if (generatedKey.next()) {
                    post.setId(generatedKey.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> rsl = new ArrayList();
        try (var statement = cnn.prepareStatement("select * from post");
             var resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                rsl.add(getPost(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rsl;
    }

    @Override
    public Post findById(int id) {
        Post post = null;
        try (PreparedStatement statement = cnn.prepareStatement(
                "select * from post where id = ?"
        )) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    post = getPost(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    private Post getPost(ResultSet resultSet) throws SQLException {
        return new Post(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("text"),
                resultSet.getString("link"),
                resultSet.getTimestamp("created").toLocalDateTime()
        );
    }

    public static void main(String[] args) {
        Properties properties = new Properties();
        try (InputStream in = PsqlStore.class.getClassLoader()
                .getResourceAsStream("grabber.properties")) {
            properties.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Неверно указан файл конфигураций");
        }
        try (PsqlStore psqlStore = new PsqlStore(properties)) {
            Post post1 = new Post(0, "Java", "Sber Java dev",
                    "habr.career.java=1", LocalDateTime.parse("2022-11-14T16:53:32"));
            Post post2 = new Post(0, "Android", "Android game dev",
                    "habr.career.java=2", LocalDateTime.parse("2022-11-14T16:56:00"));
            psqlStore.save(post1);
            psqlStore.save(post2);
            psqlStore.getAll().forEach(System.out::println);
            System.out.println(psqlStore.findById(1));
            System.out.println(psqlStore.findById(3));
            System.out.println(post1.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
