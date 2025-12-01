package ru.vsu.cs.computergraphic.sebeleva.task2;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class BezierEditorFX extends Pane {

    private final Canvas canvas = new Canvas();
    private final ArrayList<Point2> points = new ArrayList<>();
    private Point2 selected = null;

    private final int R = 6;

    public BezierEditorFX() {
        getChildren().add(canvas);

        // Canvas под размер окна
        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());

        canvas.widthProperty().addListener(e -> draw());
        canvas.heightProperty().addListener(e -> draw());

        // Обработка мыши
        canvas.setOnMousePressed(e -> {
            double x = e.getX(), y = e.getY();
            Point2 p = find(x, y);

            if (e.getButton() == MouseButton.PRIMARY) {
                if (p != null) {
                    selected = p;
                } else {
                    points.add(new Point2(x, y));
                }
            } else if (e.getButton() == MouseButton.SECONDARY) {
                if (p != null) points.remove(p);
            }
            draw();
        });

        canvas.setOnMouseDragged(e -> {
            if (selected != null) {
                selected.x = e.getX();
                selected.y = e.getY();
                draw();
            }
        });

        canvas.setOnMouseReleased(e -> selected = null);

        draw();
    }

    // Поиск ближайшей точки
    private Point2 find(double x, double y) {
        for (Point2 p : points) {
            if (Math.hypot(p.x - x, p.y - y) < R * 1.5)
                return p;
        }
        return null;
    }

    // Общий рендеринг
    private void draw() {
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setFill(Color.WHITE);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        drawGrid(g);
        drawControlPolygon(g);
        drawBezier(g);
        drawPoints(g);
    }

    // Сетка
    private void drawGrid(GraphicsContext g) {
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        double cx = w / 2, cy = h / 2;

        g.setStroke(Color.rgb(230, 230, 230));
        g.setLineWidth(1);

        int step = 30;

        for (double x = cx; x < w; x += step) g.strokeLine(x, 0, x, h);
        for (double x = cx; x > 0; x -= step) g.strokeLine(x, 0, x, h);

        for (double y = cy; y < h; y += step) g.strokeLine(0, y, w, y);
        for (double y = cy; y > 0; y -= step) g.strokeLine(0, y, w, y);

        g.setStroke(Color.GRAY);
        g.strokeLine(cx, 0, cx, h);
        g.strokeLine(0, cy, w, cy);
    }

    // Контрольный полигон
    private void drawControlPolygon(GraphicsContext g) {
        g.setStroke(Color.LIGHTGRAY);
        g.setLineWidth(1.2);

        for (int i = 0; i < points.size() - 1; i++)
            g.strokeLine(points.get(i).x, points.get(i).y,
                    points.get(i + 1).x, points.get(i + 1).y);
    }

    // Контрольные точки
    private void drawPoints(GraphicsContext g) {
        for (Point2 p : points) {
            g.setFill(p == selected ? Color.BLUE : Color.BLACK);
            g.fillOval(p.x - R / 2.0, p.y - R / 2.0, R, R);
        }
    }

    // Рисование кривой Безье
    private void drawBezier(GraphicsContext g) {
        if (points.size() < 2) return;

        g.setStroke(Color.RED);
        g.setLineWidth(2);

        Point2 prev = bezierPoint(0);

        for (double t = 0; t <= 1; t += 0.001) {
            Point2 p = bezierPoint(t);
            g.strokeLine(prev.x, prev.y, p.x, p.y);
            prev = p;
        }
    }

    // Устойчивый алгоритм де Кастельжо на double
    private Point2 bezierPoint(double t) {
        int n = points.size();
        double[] bx = new double[n];
        double[] by = new double[n];

        for (int i = 0; i < n; i++) {
            bx[i] = points.get(i).x;
            by[i] = points.get(i).y;
        }

        for (int r = 1; r < n; r++) {
            for (int i = 0; i < n - r; i++) {
                bx[i] = (1 - t) * bx[i] + t * bx[i + 1];
                by[i] = (1 - t) * by[i] + t * by[i + 1];
            }
        }

        return new Point2(bx[0], by[0]);
    }
}
