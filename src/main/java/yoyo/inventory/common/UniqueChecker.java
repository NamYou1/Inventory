package yoyo.inventory.common;


import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import yoyo.inventory.execption.DuplicateResourceException;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UniqueChecker {

    public   <T> void verify(JpaRepository<T, ?> repo, T entity, String fieldName, Object value) {

        List<String> allFields = Arrays.stream(entity.getClass().getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toList());


        List<String> ignoredFields = allFields.stream()
                .filter(f -> !f.equals(fieldName))
                .collect(Collectors.toList());

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths(ignoredFields.toArray(new String[0]))
                .withMatcher(fieldName, ExampleMatcher.GenericPropertyMatchers.exact());

        if (repo.exists(Example.of(entity, matcher))) {
            throw new DuplicateResourceException(
                    fieldName + " '" + value + "' is already in use."
            );
        }
    }
}