package ru.vsu.cs.computergraphic.sebeleva.task2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        // Создаем корневой Pane и добавляем в него BezierEditorFX
        BezierEditorFX editor = new BezierEditorFX();
        Scene scene = new Scene(editor, 1000, 700);
        stage.setScene(scene);
        stage.setTitle("Bezier curves");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}