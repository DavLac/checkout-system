package io.davlac.checkoutsystem.product.controller.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ObjectFieldsNotNullValidator implements ConstraintValidator<ObjectFieldsNotNull, Object> {

    @Override
    public boolean isValid(final Object object, ConstraintValidatorContext context) {
        if(object == null) {
            return false;
        }

        Field[] fields = object.getClass().getDeclaredFields();

        List<Object> objectValues = new ArrayList<>();
        for (Field field : fields) {
            objectValues.add(getValueFromObjectField(field, object));
        }

        return !objectValues.stream().allMatch(Objects::isNull);
    }

    private static Object getValueFromObjectField(final Field field, final Object object) {
        field.setAccessible(true);
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error when reading object values");
        } finally {
            field.setAccessible(false);
        }

    }
}
