package ru.redcarpet.dao;

public interface DAOInterface<T> {
    T create(T dto);
    T findById(Long id);
    T delete(Long id);
    T update(Long id, T dto);
}
