package frc.sorutil;

public class SorMath {
    public static final double EPISLON = 1e-6;


    public static double[] cartesianToPolar(double x, double y) {
        return new double[] {Math.sqrt(x * x + y * y), (Math.atan2(y, x) * 180) / Math.PI};
    }

    public static double[] polarToCartesian(double r, double theta) {
        theta = (theta / 180) * Math.PI;
        return new double[] {r * Math.cos(theta), r * Math.sin(theta)};
    }

    public static boolean epsilonEquals(double a, double b) {
        double diff = a - b;
        return diff < EPISLON && diff > 0d - EPISLON;
    }
}
