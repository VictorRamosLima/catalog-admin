package com.fullcycle.admin.catalog.application.category.delete;

import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DeleteCategoryUseCaseTest {
    @Mock
    private CategoryGateway categoryGateway;

    @InjectMocks
    private DefaultDeleteCategoryUseCase useCase;

    @BeforeEach
    void cleanUp() {
        Mockito.reset(categoryGateway);
    }

    @Test
    public void givenValidId_whenCallCategoryDeletion_shouldBeOK() {
        final CategoryID id = CategoryID.unique();

        doNothing().when(categoryGateway).deleteById(eq(id));

        assertDoesNotThrow(() -> useCase.execute(id.toString()));

        verify(categoryGateway, times(1)).deleteById(eq(id));
    }

    @Test
    public void givenInvalidId_whenCallCategoryDeletion_shouldBeOK() {
        final CategoryID id = CategoryID.unique();

        doNothing().when(categoryGateway).deleteById(eq(id));

        assertDoesNotThrow(() -> useCase.execute(id.toString()));

        verify(categoryGateway, times(1)).deleteById(eq(id));
    }

    @Test
    public void givenValidId_whenGatewayThrowsAnError_shouldReturnException() {
        final CategoryID id = CategoryID.unique();

        doThrow(new IllegalStateException("Gateway error")).when(categoryGateway).deleteById(eq(id));

        assertThrows(IllegalStateException.class, () -> useCase.execute(id.toString()));

        verify(categoryGateway, times(1)).deleteById(eq(id));
    }
}
