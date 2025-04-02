package dev;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Converter {

    public static void main(String[] args) {
        try {
            // Load the input image
            File inputFile = new File("D:/sign2.png");
            BufferedImage inputImage = ImageIO.read(inputFile);

            // Create a new image with transparency (TYPE_INT_ARGB)
            BufferedImage outputImage = new BufferedImage(
                    inputImage.getWidth(),
                    inputImage.getHeight(),
                    BufferedImage.TYPE_INT_ARGB
            );

            // Define the base colors for the signature (black and blue)
            int blackRGB = new Color(0, 0, 0).getRGB();
            int blueRGB = new Color(0, 0, 255).getRGB();

            // Set a color tolerance (e.g., 30 for slight variations)
            int tolerance = 200;

            for (int y = 0; y < inputImage.getHeight(); y++) {
                for (int x = 0; x < inputImage.getWidth(); x++) {
                    int rgb = inputImage.getRGB(x, y);

                    // Check if the current pixel is similar to black or blue
                    if (isColorSimilar(rgb, blackRGB, tolerance) || isColorSimilar(rgb, blueRGB, tolerance)) {
                        // Preserve the dot if similar dots exist around (5 pixels in all directions)
                        if (hasSimilarDotsAround(inputImage, x, y, blackRGB, blueRGB, tolerance)) {
                            outputImage.setRGB(x, y, rgb); // Preserve the pixel
                        } else {
                            outputImage.setRGB(x, y, 0x00FFFFFF); // Make the pixel transparent
                        }
                    } else {
                        outputImage.setRGB(x, y, 0x00FFFFFF); // Make the pixel transparent
                    }
                }
            }

            // Save the output image as a transparent PNG
            File outputFile = new File("D:/sign2_output.png");
            ImageIO.write(outputImage, "png", outputFile);

            System.out.println("Background removed, preserving signature strokes and dots!");

        } catch (IOException e) {
            System.err.println("Error processing the image: " + e.getMessage());
        }
    }

    // Helper method to check color similarity
    private static boolean isColorSimilar(int rgb1, int rgb2, int tolerance) {
        Color color1 = new Color(rgb1);
        Color color2 = new Color(rgb2);

        int rDiff = Math.abs(color1.getRed() - color2.getRed());
        int gDiff = Math.abs(color1.getGreen() - color2.getGreen());
        int bDiff = Math.abs(color1.getBlue() - color2.getBlue());

        return (rDiff <= tolerance && gDiff <= tolerance && bDiff <= tolerance);
    }

    // Helper method to check surrounding dots
    private static boolean hasSimilarDotsAround(BufferedImage image, int x, int y, int blackRGB, int blueRGB, int tolerance) {
        int dotCount = 0;
        int range = 1; // Look 5 pixels in all directions

        for (int dy = -range; dy <= range; dy++) {
            for (int dx = -range; dx <= range; dx++) {
                int newX = x + dx;
                int newY = y + dy;

                // Ensure within bounds
                if (newX >= 0 && newX < image.getWidth() 
                		
                		
                		
                		&& newY >= 0 && newY < image.getHeight()) {
                    int rgb = image.getRGB(newX, newY);
                    if (isColorSimilar(rgb, blackRGB, tolerance) || isColorSimilar(rgb, blueRGB, tolerance)) {
                        dotCount++;
                    }
                }
            }
        }

        // Return true if there are sufficient dots around the current pixel
        return dotCount > range * range / 2; // Adjust threshold as needed
    }
}
