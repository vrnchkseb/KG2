package ru.vsu.cs.computergraphic.sebeleva.task2;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import java.util.ArrayList;

public class BezierEditorFX extends Pane {

    private final Canvas canvas;
    private final ArrayList<Point2> points = new ArrayList<>();
    private Point2 selected = null;

    private static final int R = 6;

    public BezierEditorFX() {
        // Создаем Canvas с начальными размерами
        canvas = new Canvas(1000, 700);
        getChildren().add(canvas);

        // Связываем размеры Canvas с размерами Pane
        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());

        // Рисуем при изменении размеров
        widthProperty().addListener((obs, oldVal, newVal) -> draw());
        heightProperty().addListener((obs, oldVal, newVal) -> draw());

        handleMouse();

        // Первоначальная отрисовка
        draw();
    }

    private void handleMouse() {
        setOnMousePressed(e -> {
            Point2 p = findPoint(e.getX(), e.getY());
            if (p != null) {
                selected = p;
            } else {
                points.add(new Point2(e.getX(), e.getY()));
            }
            draw();
        });

        setOnMouseDragged(e -> {
            if (selected != null) {
                selected.x = e.getX();
                selected.y = e.getY();
                draw();
            }
        });

        setOnMouseReleased(e -> selected = null);
    }

    private Point2 findPoint(double x, double y) {
        for (Point2 p : points) {
            if (Math.hypot(p.x - x, p.y - y) < R * 1.5)
                return p;
        }
        return null;
    }

    private void draw() {
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        // Проверяем валидность размеров
        if (width <= 0 || height <= 0) {
            System.out.println("Canvas has invalid dimensions: " + width + "x" + height);
            return;
        }

        GraphicsContext g = canvas.getGraphicsContext2D();

        // Очищаем canvas
        g.clearRect(0, 0, width, height);
        g.setFill(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // Рисуем сетку и оси с помощью GraphicsContext
        drawGrid(g);
        drawAxes(g);
        drawControlPolygon(g);
        drawBezier(g);
        drawPoints(g);
    }

    private void drawGrid(GraphicsContext g) {
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        double centerX = width / 2;
        double centerY = height / 2;
        int step = 30;

        g.setStroke(Color.rgb(230, 230, 230));
        g.setLineWidth(1);

        // Вертикальные линии
        for (double x = centerX; x < width; x += step) {
            g.strokeLine(x, 0, x, height);
        }
        for (double x = centerX; x > 0; x -= step) {
            g.strokeLine(x, 0, x, height);
        }

        // Горизонтальные линии
        for (double y = centerY; y < height; y += step) {
            g.strokeLine(0, y, width, y);
        }
        for (double y = centerY; y > 0; y -= step) {
            g.strokeLine(0, y, width, y);
        }
    }

    private void drawAxes(GraphicsContext g) {
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        double centerX = width / 2;
        double centerY = height / 2;

        g.setStroke(Color.GRAY);
        g.setLineWidth(2);

        // Вертикальная ось Y
        g.strokeLine(centerX, 0, centerX, height);
        // Горизонтальная ось X
        g.strokeLine(0, centerY, width, centerY);
    }

    private void drawControlPolygon(GraphicsContext g) {
        g.setStroke(Color.LIGHTGRAY);
        g.setLineWidth(1);

        for (int i = 0; i < points.size() - 1; i++) {
            Point2 a = points.get(i);
            Point2 b = points.get(i + 1);
            g.strokeLine(a.x, a.y, b.x, b.y);
        }
    }

    private void drawBezier(GraphicsContext g) {
        if (points.size() < 2) return;

        g.setStroke(Color.RED);
        g.setLineWidth(2);

        ArrayList<Point2> curve = BezierCalculator.generateCurve(points);

        // Рисуем кривую линиями
        for (int i = 0; i < curve.size() - 1; i++) {
            Point2 a = curve.get(i);
            Point2 b = curve.get(i + 1);
            g.strokeLine(a.x, a.y, b.x, b.y);
        }
    }

    private void drawPoints(GraphicsContext g) {
        g.setFill(Color.BLACK);
        for (Point2 p : points) {
            g.fillOval(p.x - R / 2.0, p.y - R / 2.0, R, R);
        }
    }
}