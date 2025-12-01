package ru.vsu.cs.computergraphic.sebeleva.task2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        BezierEditorFX editor = new BezierEditorFX();

        Scene scene = new Scene(editor, 1000, 700);
        stage.setTitle("Кривые Безье — JavaFX");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
