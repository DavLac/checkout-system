package io.davlac.checkoutsystem.context.product.validator.objectfieldsnotnull;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class InnerValidatorTestClass {
    private String field1;
    private Integer field2;
    private List<Long> field3;
}
