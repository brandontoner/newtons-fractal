package com.brandontoner.newtons.fractal;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.UnaryOperator;

import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

public class Fractal {
    private final int width;
    private final int height;
    private final int maxIterations = 40;
    private final double xmin;
    private final double xmax;
    private final double ymin;
    private final double ymax;
    private final UnaryOperator<Complex> function = this::f;
    private final UnaryOperator<Complex> derivative = this::df;
    private final int offset;
    private final Complex k;

    public Fractal(int width, int height, double xmin, double xmax, double ymin, double ymax, int offset) {
        this.width = width;
        this.height = height;
        this.xmin = xmin;
        this.xmax = xmax;
        this.ymin = ymin;
        this.ymax = ymax;
        this.offset = offset;
        k = new Complex(StrictMath.cos(toRadians(offset)), StrictMath.sin(toRadians(offset)));
    }

    private Complex f(Complex z) {
        return z.power(5).subtract(k);
    }

    private Complex df(Complex z) {
        return z.power(4).multiply(5);
    }

    private Complex newtons(Complex z, AtomicInteger count) {
        Complex output = z;
        int i = 0;
        while (i < maxIterations) {
            Complex f = function.apply(output);
            if (!(f.length() > 0.0001)) {
                break;
            }
            Complex d = derivative.apply(output);
            if (Objects.equals(d, Complex.ZERO)) {
                break;
            }
            output = output.subtract(f.divide(d));
            i++;
        }
        count.set(i);
        return output;
    }

    private static int fromHSV(double h, double s, double v) {
        assert 0 <= h && h <= 360 : "h must be between 0 and 360";
        assert 0 <= s && s <= 1 : "s must be between 0 and 1";
        assert 0 <= v && v <= 1 : "v bust be between 0 and 1";

        double c = v * s;
        double hprime = h / 60;
        double x = c * (1 - Math.abs(hprime % 2 - 1));

        if (hprime < 1) {
            return fromArgb((int) (255 * c), (int) (255 * x), 0);
        } else if (hprime < 2) {
            return fromArgb((int) (255 * x), (int) (255 * c), 0);
        } else if (hprime < 3) {
            return fromArgb(0, (int) (255 * c), (int) (255 * x));
        } else if (hprime < 4) {
            return fromArgb(0, (int) (255 * x), (int) (255 * c));
        } else if (hprime < 5) {
            return fromArgb((int) (255 * x), 0, (int) (255 * c));
        } else if (hprime < 6) {
            return fromArgb((int) (255 * c), 0, (int) (255 * x));
        } else {
            return fromArgb(0, 0, 0);
        }
    }

    private static int fromArgb(int r, int g, int b) {
        return (b << 16) | (g << 8) | r;
    }

    private int color(Complex z, int iterations) {
        double hue = toDegrees(z.arg());

        double value = 1.0 - (double) iterations / maxIterations;
        double saturation = value; //  0.5 + 0.5 * Math.Cos(z.length());

        while (hue < 0) {
            hue += 360;
        }
        while (hue >= 360) {
            hue -= 360;
        }

        if (saturation < 0 || Double.isNaN(saturation)) {
            saturation = 0;
        }

        return fromHSV(hue, saturation, value);
    }

    public void compute() throws IOException {
        BufferedImage bitmap = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        for (int y = 0; y < height; ++y) {
            if (y % 10 == 0) {
                System.err.println(100.0 * y / height);
            }
            // Need to flip y axis, since (0, 0) is in top left corner, not bottom left
            double imag = -(ymin + (ymax - ymin) * y / height);
            for (int x = 0; x < width; ++x) {
                double real = xmin + (xmax - xmin) * x / width;
                AtomicInteger iterations = new AtomicInteger(0);
                Complex z = new Complex(real, imag);
                Complex z1 = newtons(z, iterations);
                int c = color(z1, iterations.get());
                bitmap.setRGB(x, y, c);
            }
        }
        ImageIO.write(bitmap, "PNG", new File(String.format("D:\\output\\%03d.png", offset)));
    }
}
