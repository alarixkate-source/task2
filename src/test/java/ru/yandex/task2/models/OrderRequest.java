package ru.yandex.task2.models;

import java.util.List;

public class OrderRequest {
    private List<String> ingredients;

    public OrderRequest() {
    }

    public OrderRequest(List<String> ingredients) {
        this.ingredients = ingredients;
    }
// ну пусть будут, мне спокойнее, пусть и не используются)))
    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}