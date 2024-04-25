package com.example.domain;

import com.example.exception.AuthException;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@NoArgsConstructor
public class UserService {

    private static final List<User> users = new CopyOnWriteArrayList<>(List.of(
            new User("ugug", "123"),
            new User("nick", "123"),
            new User("lerka", "123")
    ));


    public static Optional<User> getByLogin(@NonNull String login) {
        return users.stream()
                .filter(user -> user.getLogin().equalsIgnoreCase(login))
                .findFirst();
    }

    public static void addUser(String login, String password) {
        boolean check = true;
        for (User user : users) {
            if (user.getLogin().equals(login)){
                check = false;
            }
        }
        if (check) {
            users.add(new User(login, password));
        } else{
            throw new AuthException("Пользователь уже существует");
        }
    }
}

