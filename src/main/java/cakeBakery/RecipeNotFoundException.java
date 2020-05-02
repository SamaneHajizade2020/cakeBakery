package cakeBakery;

public class RecipeNotFoundException extends RuntimeException {
    public RecipeNotFoundException(Long id) {
    }

    public RecipeNotFoundException() {
    }

    public RecipeNotFoundException(String message) {
        super(message);
    }

    public RecipeNotFoundException(String message, Throwable cause) {
        super("500", cause);
    }
}
