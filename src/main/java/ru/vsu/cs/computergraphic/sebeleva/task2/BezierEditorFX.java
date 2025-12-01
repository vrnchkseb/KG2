package ru.vsu.cs.computergraphic.sebeleva.task2;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
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

        PixelDrawer pd = new PixelDrawer(g);

        drawGrid(pd);
        drawAxes(pd);
        drawControlPolygon(pd);
        drawBezier(pd);
        drawPoints(g);
    }

    // Рисуем сетку (через PixelDrawer)
    private void drawGrid(PixelDrawer pd) {
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        double cx = w / 2, cy = h / 2;

        int step = 30;

        pd.setColor(Color.rgb(230, 230, 230));
        for (double x = cx; x < w; x += step) pd.drawLine((int)x, 0, (int)x, (int)h);
        for (double x = cx; x > 0; x -= step) pd.drawLine((int)x, 0, (int)x, (int)h);

        for (double y = cy; y < h; y += step) pd.drawLine(0, (int)y, (int)w, (int)y);
        for (double y = cy; y > 0; y -= step) pd.drawLine(0, (int)y, (int)w, (int)y);
    }

    private void drawAxes(PixelDrawer pd) {
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        double cx = w / 2, cy = h / 2;

        pd.setColor(Color.GRAY);
        pd.drawLine((int)cx, 0, (int)cx, (int)h); // Y
        pd.drawLine(0, (int)cy, (int)w, (int)cy); // X

        // подписи можно рисовать встроенным методом (маленький текст) — оставляем для читаемости
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setFill(Color.BLACK);
        g.fillText("X", w - 20, cy - 5);
        g.fillText("Y", cx + 5, 15);
    }

    // Контрольный полигон — через PixelDrawer (собственные прямые)
    private void drawControlPolygon(PixelDrawer pd) {
        pd.setColor(Color.LIGHTGRAY);
        for (int i = 0; i < points.size() - 1; i++) {
            Point2 a = points.get(i), b = points.get(i + 1);
            pd.drawLine((int)Math.round(a.x), (int)Math.round(a.y),
                    (int)Math.round(b.x), (int)Math.round(b.y));
        }
    }

    // Контрольные точки (маленькие кружки) — можем рисовать через GraphicsContext (радиус > 1)
    private void drawPoints(GraphicsContext g) {
        for (Point2 p : points) {
            g.setFill(p == selected ? Color.BLUE : Color.BLACK);
            g.fillOval(p.x - R / 2.0, p.y - R / 2.0, R, R);
        }
    }

    // Рисование кривой Безье — вычисляем точки на double, отрисовываем сегменты нашим drawLine
    private void drawBezier(PixelDrawer pd) {
        if (points.size() < 2) return;

        pd.setColor(Color.RED);

        Point2 prev = bezierPoint(0.0);

        // шаг по t — можно менять динамически в зависимости от числа точек, но 0.001 даёт хорошую гладкость
        for (double t = 0.001; t <= 1.0001; t += 0.001) {
            Point2 cur = bezierPoint(t);
            pd.drawLine(
                    (int)Math.round(prev.x), (int)Math.round(prev.y),
                    (int)Math.round(cur.x),  (int)Math.round(cur.y)
            );
            prev = cur;
        }
    }

    // Устойчивая де Кастельжо на double (возвращает координату кривой в t)
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

    // ----------------- Вложенный PixelDrawer -----------------
    // Рисует пиксели через PixelWriter и содержит реализацию Брезенхэма для drawLine
    private static class PixelDrawer {
        private final PixelWriter pw;
        private Color color = Color.BLACK;

        public PixelDrawer(GraphicsContext gc) {
            this.pw = gc.getPixelWriter();
        }

        public void setColor(Color c) {
            if (c != null) this.color = c;
        }

        public void drawPixel(int x, int y) {
            if (x < 0 || y < 0) return;
            try {
                pw.setColor(x, y, color);
            } catch (IndexOutOfBoundsException ignored) {
                // игнорируем пиксели за пределами canvas
            }
        }

        // Алгоритм Брезенхэма (целочисленный, работает для всех направлений)
        public void drawLine(int x0, int y0, int x1, int y1) {
            // быстрый выход
            if (x0 == x1 && y0 == y1) {
                drawPixel(x0, y0);
                return;
            }

            int dx = Math.abs(x1 - x0);
            int dy = Math.abs(y1 - y0);

            int sx = x0 < x1 ? 1 : -1;
            int sy = y0 < y1 ? 1 : -1;

            boolean steep = dy > dx;
            if (steep) {
                // транспонируем оси, чтобы обеспечить dx >= dy
                int tmp;
                tmp = x0; x0 = y0; y0 = tmp;
                tmp = x1; x1 = y1; y1 = tmp;
                tmp = dx; dx = dy; dy = tmp;
                tmp = sx; sx = sy; sy = tmp; // направление тоже поменяется не критично
            }

            int err = 2 * dy - dx;
            int y = y0;
            int x = x0;

            for (int i = 0; i <= dx; i++) {
                if (steep) {
                    drawPixel(y, x);
                } else {
                    drawPixel(x, y);
                }

                while (err > 0) {
                    y += sy;
                    err -= 2 * dx;
                }

                x += sx;
                err += 2 * dy;
            }
        }
    }
}
