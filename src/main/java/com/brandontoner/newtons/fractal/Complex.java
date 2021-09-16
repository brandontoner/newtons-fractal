package com.brandontoner.newtons.fractal;

record Complex(double real, double imaginary) {
    static final Complex ZERO = new Complex(0, 0);
    static final Complex ONE = new Complex(1, 0);

    Complex power(int n) {
        double outReal = 1;
        double outImaginary = 0;
        for (int i = 0; i < n; ++i) {
            double a = outReal;
            double b = outImaginary;
            double c = real;
            double d = imaginary;
            outReal = (a * c) - (b * d);
            outImaginary = (a * d) + (b * c);
        }
        return new Complex(outReal, outImaginary);
    }

    private Complex multiply(Complex other) {
        double a = real;
        double b = imaginary;
        double c = other.real;
        double d = other.imaginary;
        return new Complex((a * c) - (b * d), (a * d) + (b * c));
    }

    Complex subtract(Complex k) {
        return new Complex(real - k.real, imaginary - k.imaginary);
    }

    Complex multiply(int i) {
        return new Complex(real * i, imaginary * i);
    }

    double arg() {
        return StrictMath.atan2(imaginary, real);
    }

    double length() {
        return Math.sqrt((real * real) + (imaginary * imaginary));
    }

    Complex divide(Complex other) {
        double a = real;
        double b = imaginary;
        double c = other.real;
        double d = other.imaginary;
        return new Complex(((a * c) + (b * d)) / ((c * c) + (d * d)), ((b * c) - (a * d)) / ((c * c) + (d * d)));
    }
}
