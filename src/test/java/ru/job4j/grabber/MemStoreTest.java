package ru.job4j.grabber;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class MemStoreTest {

    @Test
    void checkSave() {
        Post post = new Post(0, "Java dev", "habr.career.java.ru",
                "требуется Java разработчик", LocalDateTime.parse("2022-10-14T16:53:32"));
        Store mem = new MemStore();
        mem.save(post);
        assertThat(post.getId()).isNotNull()
                .isPositive()
                .isEqualTo(1);
    }

    @Test
    void checkGetAll() {
        Post post = new Post(0, "Java dev", "habr.career.java.ru=1",
                "требуется Java разработчик", LocalDateTime.parse("2022-10-14T16:53:32"));
        Post post1 = new Post(0, "Android dev", "habr.career.java.ru=2",
                "требуется Android разработчик", LocalDateTime.parse("2022-10-14T16:59:32"));
        Store memory = new MemStore();
        memory.save(post);
        memory.save(post1);
        assertThat(memory.getAll()).isNotNull()
                .hasSize(2);
    }

    @Test
    void whenFind2ByIdThenPost1() {
        Post post = new Post(0, "Java dev", "habr.career.java.ru=1",
                "требуется Java разработчик", LocalDateTime.parse("2022-10-14T16:53:32"));
        Post post1 = new Post(0, "Android dev", "habr.career.java.ru=2",
                "требуется Android разработчик", LocalDateTime.parse("2022-10-14T16:59:32"));
        Store memory = new MemStore();
        memory.save(post);
        memory.save(post1);
        assertThat(memory.findById(2)).isNotNull()
                .isEqualTo(post1);
    }

    @Test
    void whenNotFoundIdThenNull() {
        Post post = new Post(0, "Java dev", "habr.career.java.ru=1",
                "требуется Java разработчик", LocalDateTime.parse("2022-10-14T16:53:32"));
        Post post1 = new Post(0, "Android dev", "habr.career.java.ru=2",
                "требуется Android разработчик", LocalDateTime.parse("2022-10-14T16:59:32"));
        Store memory = new MemStore();
        memory.save(post);
        memory.save(post1);
        assertThat(memory.findById(3)).isNull();
    }
}