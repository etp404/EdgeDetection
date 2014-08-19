package uk.co.bbc;

import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

/*
Caveat: This code needs a good tidy up if it proceeds to production. As it is, it's more of a spike.
 */

public class EdgeDetectionDemo {
    private static final int HEADLINE_PANEL_WIDTH = 1200;

    public static void main(String[] args) {
        System.loadLibrary("opencv_java249");


        File folder = new File("resources/input");
        File[] listOfFiles = folder.listFiles();

        for (int i = 55; i<60; i++) {
            File inputImageFile = listOfFiles[i];
			Mat inputImage = Highgui.imread(inputImageFile.getAbsolutePath(), Highgui.CV_LOAD_IMAGE_GRAYSCALE);
			System.out.println(i+1 + " of " + listOfFiles.length);

			if (inputImage.height() > inputImage.width()) {
				continue;
			}

            Mat result = getEdgeMap(inputImage);
            int bestLeftEdge = getBestLeftEdge(result);
            Mat scaleMatrix = new Mat(inputImage.height(),inputImage.width(), CvType.CV_64F, new Scalar(Core.minMaxLoc(result).maxVal/255.0));
            Core.divide(result, scaleMatrix, result); //Not ideal: I only want to divide by a scalar.

            String outputFilename = "resources/results/" + FilenameUtils.removeExtension(inputImageFile.getName()) + "_edgeMap.png";
            Highgui.imwrite(outputFilename, result);

			Mat originalImage = Highgui.imread(inputImageFile.getAbsolutePath(), Highgui.CV_LOAD_IMAGE_COLOR);

			String inputFilename = "resources/results/" + FilenameUtils.removeExtension(inputImageFile.getName()) + "_input.png";
			Highgui.imwrite(inputFilename, originalImage);

			String originalImageWithHeadline = "resources/results/" + FilenameUtils.removeExtension(inputImageFile.getName()) +
					"_withHeadline.png";

			Mat imCrop = originalImage.submat(0, originalImage.height(), bestLeftEdge, bestLeftEdge + originalImage.width()
													  - HEADLINE_PANEL_WIDTH).clone();

			Mat output = new Mat();
			Imgproc.copyMakeBorder(imCrop, output, 0, 0, HEADLINE_PANEL_WIDTH, 0, 0);

			Highgui.imwrite(originalImageWithHeadline, output);
        }
    }

    private static int getBestLeftEdge(Mat result) {
        System.out.println("Optimizing position of headline.");
        int bestLeftEdge = 0;
        Double highestInterestScore = 0.0;
        for (int x=0; x<HEADLINE_PANEL_WIDTH; x++) {
            double interestScore = Core.sumElems(result.colRange(x, x + result.width() - HEADLINE_PANEL_WIDTH)).val[0];
            if (interestScore > highestInterestScore){
				highestInterestScore = interestScore;
                bestLeftEdge = x;
            }
        }
        return bestLeftEdge;
    }

    private static Mat getEdgeMap(Mat inputImage) {
        Mat inputImage64 = new Mat(inputImage.height(),inputImage.width(), CvType.CV_64F);
        inputImage.convertTo(inputImage64, CvType.CV_64F);

        Mat result = new Mat(inputImage64.height(), inputImage64.width(), inputImage64.depth());
        for (double lambda = 1; lambda <=5; lambda+=2) {
            double sigma = lambda;
            Size size = new Size(2*sigma, 2*sigma);
            Mat outputForThisScale = new Mat(inputImage64.height(), inputImage64.width(), inputImage64.depth());
            for (double orientation = 0; orientation < 2*Math.PI; orientation+=Math.PI/4) {
				System.out.println("Lamdba :" + lambda + ". Orientation:" + orientation);
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