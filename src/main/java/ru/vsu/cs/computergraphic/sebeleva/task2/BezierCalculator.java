package ru.vsu.cs.computergraphic.sebeleva.task2;

import java.util.ArrayList;

public class BezierCalculator {

    // Де Кастельжо
    public static Point2 computePoint(ArrayList<Point2> control, double t) {
        int n = control.size();
        double[] bx = new double[n];
        double[] by = new double[n];

        for (int i = 0; i < n; i++) {
            bx[i] = control.get(i).x;
            by[i] = control.get(i).y;
        }

        for (int r = 1; r < n; r++) {
            for (int i = 0; i < n - r; i++) {
                bx[i] = (1 - t) * bx[i] + t * bx[i + 1];
                by[i] = (1 - t) * by[i] + t * by[i + 1];
            }
        }

        return new Point2(bx[0], by[0]);
    }

    // Генерация всех сегментов линии
    public static ArrayList<Point2> generateCurve(ArrayList<Point2> control) {
        ArrayList<Point2> result = new ArrayList<>();
        if (control.size() < 2) return result;

        for (double t = 0; t <= 1.0; t += 0.001) {
            result.add(computePoint(control, t));
        }

        return result;
    }
}
