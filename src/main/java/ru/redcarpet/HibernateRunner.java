package ru.redcarpet;

import ru.redcarpet.mapper.UserMapper;

public class HibernateRunner {

    public static void main(String[] args) {
        
        Dispatcher dispatcher = new Dispatcher(new UserMapper());
        dispatcher.run();
    }
}