package ru.vsu.cs.computergraphic.sebeleva.task2;

import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

public class PixelDrawer {

    private final PixelWriter pw;
    private Color color = Color.BLACK;

    public PixelDrawer(PixelWriter pw) {
        this.pw = pw;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void drawPixel(int x, int y) {
        try {
            pw.setColor(x, y, color);
        } catch (Exception ignored) {}
    }

    // Брезенхэм
    public void drawLine(int x0, int y0, int x1, int y1) {
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
            int t;
            t = x0; x0 = y0; y0 = t;
            t = x1; x1 = y1; y1 = t;
            t = dx; dx = dy; dy = t;
        }

        int err = 2 * dy - dx;
        int y = y0;

        for (int x = x0; x != x1; x += sx) {
            if (steep) drawPixel(y, x);
            else drawPixel(x, y);

            if (err > 0) {
                y += sy;
                err -= 2 * dx;
            }
            err += 2 * dy;
        }
    }
}
