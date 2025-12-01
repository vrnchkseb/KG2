package ru.vsu.cs.computergraphic.sebeleva.task2;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import java.util.ArrayList;

public class BezierEditorFX extends Pane {

    private final Canvas canvas = new Canvas();
    private final ArrayList<Point2> points = new ArrayList<>();
    private Point2 selected = null;

    private static final int R = 6;

    public BezierEditorFX() {
        getChildren().add(canvas);
        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());

        canvas.widthProperty().addListener(e -> draw());
        canvas.heightProperty().addListener(e -> draw());

        handleMouse();
    }

    private void handleMouse() {
        canvas.setOnMousePressed(e -> {
            Point2 p = findPoint(e.getX(), e.getY());
            if (p != null) selected = p;
            else points.add(new Point2(e.getX(), e.getY()));
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
    }

    private Point2 findPoint(double x, double y) {
        for (Point2 p : points) {
            if (Math.hypot(p.x - x, p.y - y) < R * 1.5)
                return p;
        }
        return null;
    }

    private void draw() {
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setFill(Color.WHITE);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        PixelDrawer pd = new PixelDrawer(g.getPixelWriter());

        drawGrid(pd);
        drawAxes(pd);
        drawControlPolygon(pd);
        drawBezier(pd);

        drawPoints(g);
    }

    private void drawGrid(PixelDrawer pd) {
        pd.setColor(Color.rgb(230, 230, 230));
        double w = canvas.getWidth(), h = canvas.getHeight();
        double cx = w / 2, cy = h / 2;
        int step = 30;

        for (double x = cx; x < w; x += step) pd.drawLine((int)x, 0, (int)x, (int)h);
        for (double x = cx; x > 0; x -= step) pd.drawLine((int)x, 0, (int)x, (int)h);

        for (double y = cy; y < h; y += step) pd.drawLine(0, (int)y, (int)w, (int)y);
        for (double y = cy; y > 0; y -= step) pd.drawLine(0, (int)y, (int)w, (int)y);
    }

    private void drawAxes(PixelDrawer pd) {
        pd.setColor(Color.GRAY);
        double w = canvas.getWidth(), h = canvas.getHeight();
        double cx = w / 2, cy = h / 2;

        pd.drawLine((int)cx, 0, (int)cx, (int)h);
        pd.drawLine(0, (int)cy, (int)w, (int)cy);
    }

    private void drawControlPolygon(PixelDrawer pd) {
        pd.setColor(Color.LIGHTGRAY);
        for (int i = 0; i < points.size() - 1; i++) {
            Point2 a = points.get(i), b = points.get(i + 1);
            pd.drawLine((int)a.x, (int)a.y, (int)b.x, (int)b.y);
        }
    }

    private void drawBezier(PixelDrawer pd) {
        if (points.size() < 2) return;

        pd.setColor(Color.RED);

        ArrayList<Point2> curve = BezierCalculator.generateCurve(points);

        for (int i = 0; i < curve.size() - 1; i++) {
            Point2 a = curve.get(i);
            Point2 b = curve.get(i + 1);
            pd.drawLine((int)a.x, (int)a.y, (int)b.x, (int)b.y);
        }
    }

    private void drawPoints(GraphicsContext g) {
        for (Point2 p : points) {
            g.setFill(Color.BLACK);
            g.fillOval(p.x - R / 2.0, p.y - R / 2.0, R, R);
        }
    }
}
