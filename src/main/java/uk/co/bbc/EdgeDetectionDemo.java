package uk.co.bbc;

import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

public class EdgeDetectionDemo {
    private static final int HEADLINE_PANEL_WIDTH = 1000;

    public static void main(String[] args) {
        System.loadLibrary("opencv_java249");


        File folder = new File("resources/input");
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i<4; i++) {
            File inputImageFile = listOfFiles[i];
            System.out.println(i + " of " + listOfFiles.length);
            Mat inputImage = Highgui.imread(inputImageFile.getAbsolutePath(), Highgui.CV_LOAD_IMAGE_GRAYSCALE);
            Mat result = getEdgeMap(inputImage);
            int bestLeftEdge = getBestLeftEdge(result);
            Mat scaleMatrix = new Mat(inputImage.height(),inputImage.width(), CvType.CV_64F, new Scalar(Core.minMaxLoc(result).maxVal/255.0));
            Core.divide(result, scaleMatrix, result); //Not ideal: I only want to divide by a scalar.

            String inputFilename = "resources/results/" + FilenameUtils.removeExtension(inputImageFile.getName()) + "_input.png";
            Core.rectangle(inputImage, new Point(bestLeftEdge,0), new Point (bestLeftEdge + HEADLINE_PANEL_WIDTH, inputImage.height()), new Scalar(0), 10);
            Highgui.imwrite(inputFilename, inputImage);


            String outputFilename = "resources/results/" + FilenameUtils.removeExtension(inputImageFile.getName()) + "_output.png";
            Highgui.imwrite(outputFilename, result);
        }
    }

    private static int getBestLeftEdge(Mat result) {
        System.out.println("Optimizing position of headline.");
        int bestLeftEdge = 0;
        Double lowestInterestScore = null;
        for (int x=0; x<result.width()-HEADLINE_PANEL_WIDTH; x++) {
            double interestScore = Core.sumElems(result.colRange(x, x + HEADLINE_PANEL_WIDTH)).val[0];
            if (lowestInterestScore == null || interestScore < lowestInterestScore){
                lowestInterestScore = interestScore;
                bestLeftEdge = x;
            }
        }
        return bestLeftEdge;
    }

    private static Mat getEdgeMap(Mat inputImage) {
        Mat inputImage64 = new Mat(inputImage.height(),inputImage.width(), CvType.CV_64F);
        inputImage.convertTo(inputImage64, CvType.CV_64F);

        Mat result = new Mat(inputImage64.height(), inputImage64.width(), inputImage64.depth());
        for (double lambda = 5; lambda <10; lambda+=10) {
            double sigma = lambda;
            Size size = new Size(2*sigma, 2*sigma);
            Mat outputForThisScale = new Mat(inputImage64.height(), inputImage64.width(), inputImage64.depth());
            for (double orientation = 0; orientation < 2*Math.PI; orientation+=Math.PI/4) {
                Mat kernel = Imgproc.getGaborKernel(size, sigma, orientation, lambda, 1);
                Mat output = new Mat(inputImage64.height(), inputImage64.width(), inputImage64.depth());
                Imgproc.filter2D(inputImage64, output, -1, kernel);
                Core.max(output, outputForThisScale, outputForThisScale);

                Mat scaleMatrix = new Mat(inputImage.height(),inputImage.width(), CvType.CV_64F, new Scalar(Core.minMaxLoc(output).maxVal/255.0));
                Core.divide(output, scaleMatrix, output); //Not ideal: I only want to divide by a scalar.

//                String outputFilename = "resources/separate/wavelength_" + String.valueOf(lambda) + "_orientation_" + String.valueOf(orientation/(Math.PI/8)) + "_output.png";
//                Highgui.imwrite(outputFilename, output);

            }
//            String outputFilename = "resources/wavelength_" + String.valueOf(lambda) + "_output.png";
//            Highgui.imwrite(outputFilename, outputForThisScale);
            Core.add(result, outputForThisScale, result);
        }
        return result;
    }
}