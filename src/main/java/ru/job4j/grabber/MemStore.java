package ru.job4j.grabber;

import java.util.ArrayList;
import java.util.List;

public class MemStore implements Store {

    private List<Post> memory = new ArrayList<>();

    @Override
    public void save(Post post) {
        memory.add(post);
    }

    @Override
    public List<Post> getAll() {
        return memory;
    }

    @Override
    public Post findById(int id) {
        return memory.get(id);
    }
}
