import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class ImageManager {

    private HashMap<String, Image> images;
    private ArrayList<String> imageNames;
    private File dataFile;

    public ImageManager() {
        images = new HashMap<>();
        imageNames = new ArrayList<>();
        dataFile = new File("data.txt");
        initData();
    }

    private void initData() {
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(dataFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(":");
                    String imageName = data[0];
                    Image image = loadImage(data[1]);
                    images.put(imageName, image);
                    imageNames.add(imageName);
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveData() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile, true));
            for (String imageName : images.keySet()) {
                writer.write(imageName + ":" + getImagePath(images.get(imageName)) + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeData(String imageName) {
        try {
            File tempFile = new File("temp.txt");
            BufferedReader reader = new BufferedReader(new FileReader(dataFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith(imageName + ":")) {
                    writer.write(line + "\n");
                }
            }
            reader.close();
            writer.close();
            dataFile.delete();
            tempFile.renameTo(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Image loadImage(String imagePath) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }

    private String getImagePath(Image image) {
        String imagePath = null;
        for (String imageName : images.keySet()) {
            if (images.get(imageName).equals(image)) {
                imagePath = imageName;
                break;
            }
        }
        return imagePath;
    }

    public void addImage(String imageName, Image image) {
        images.put(imageName, image);
        imageNames.add(imageName);
        saveData();
    }

    public void removeImage(String imageName) {
        images.remove(imageName);
        imageNames.remove(imageName);
        removeData(imageName);
    }

    public void listImages() {
        System.out.println("Image list:");
        for (String imageName : imageNames) {
            System.out.println("- " + imageName);
        }
    }

    public static void main(String[] args) {
        ImageManager manager = new ImageManager();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Enter command: (add, remove, list, exit)");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("add")) {
                System.out.println("Enter image name:");
                String imageName = scanner.nextLine();
                System.out.println("Enter image path:");
                String imagePath = scanner.nextLine();
                Image image = manager.loadImage(imagePath);
                if (image != null) {
                    manager.addImage(imageName, image);
                    System.out.println("Image added successfully.");
                } else {
                    System.out.println("Failed to add image.");
                }
            } else if (input.equalsIgnoreCase("remove")) {
                System.out.println("Enter image name:");
                String imageName = scanner.nextLine();
                manager.removeImage(imageName);
                System.out.println("Image removed successfully.");
            } else if (input.equalsIgnoreCase("list")) {
                manager.listImages();
            } else if (input.equalsIgnoreCase("exit")) {
                System.out.println("Exiting program.");
                break;
            } else {
                System.out.println("Invalid command.");
            }
        }
    }
}