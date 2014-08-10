package main;

import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;
import java.util.List;

public class EdgeDetectionDemo {
    public static void main(String[] args) {
        System.loadLibrary("opencv_java249");

        Mat inputImage = Highgui.imread("resources/tennis.jpg", Highgui.CV_LOAD_IMAGE_GRAYSCALE);

        Mat inputImage64 = new Mat(inputImage.height(),inputImage.width(), CvType.CV_64F);
        inputImage.convertTo(inputImage64, CvType.CV_64F);

        Mat result = new Mat(inputImage64.height(), inputImage64.width(), inputImage64.depth());
        for (double lambda = 5; lambda <100; lambda+=10) {
            double sigma = lambda;
            Size size = new Size(2*sigma, 2*sigma);
            for (double orientation = 0; orientation < 2*Math.PI; orientation+=Math.PI/4) {
                Mat kernel = Imgproc.getGaborKernel(size, sigma, orientation, lambda, 1);
                Mat output = new Mat();
                Imgproc.filter2D(inputImage64, output, inputImage64.depth(), kernel);
                Core.add(result, output, result);
            }
        }

        String outputFilename = "resources/output.png";
        Highgui.imwrite(outputFilename, result);
    }
}