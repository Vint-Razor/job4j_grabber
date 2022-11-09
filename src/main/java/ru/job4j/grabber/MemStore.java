package ru.job4j.grabber;

import java.util.ArrayList;
import java.util.List;

public class MemStore implements Store {
    private int count = 0;
    private List<Post> memory = new ArrayList<>();

    @Override
    public void save(Post post) {
        post.setId(++count);
        memory.add(post);
    }

    @Override
    public List<Post> getAll() {
        return memory;
    }

    @Override
    public Post findById(int id) {
        Post rsl = null;
        for (var post : memory) {
            if (post.getId() == id) {
                rsl = post;
                break;
            }
        }
        return rsl;
    }
}
