package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class DogImageViewer {

    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetches 3 random image URLs from the Dog CEO API.
     * @return list of image URLs
     */
    public java.util.List<String> fetchRandomSubBreedImageUrls() throws IOException {
        String apiUrl = "https://dog.ceo/api/breed/retriever/golden/images/random/9";
        Request req = new Request.Builder().url(apiUrl).build();

        try (Response resp = client.newCall(req).execute()) {
            if (!resp.isSuccessful() || resp.body() == null) {
                throw new IOException("HTTP error: " + resp.code());
            }

            String body = resp.body().string();
            JSONObject json = new JSONObject(body);
            if (!"success".equalsIgnoreCase(json.optString("status"))) {
                throw new IOException("API returned error: " + json.optString("message"));
            }

            JSONArray arr = json.getJSONArray("message");
            java.util.List<String> urls = new java.util.ArrayList<>();
            for (int i = 0; i < arr.length(); i++) {
                urls.add(arr.getString(i));
            }
            return urls;
        }
    }

    /**
     * Displays multiple images in a simple GUI.
     */
    public void showImagesInGui(java.util.List<String> imageUrls) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Dog Image Gallery");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new GridLayout(3, imageUrls.size(), 10, 10)); // 1 row, 3 columns, spacing

            for (String imageUrl : imageUrls) {
                try {
                    URL url = new URL(imageUrl);
                    BufferedImage img = ImageIO.read(url);
                    if (img != null) {
                        ImageIcon icon = new ImageIcon(img);
                        JLabel label = new JLabel(icon);
                        frame.add(label);
                    } else {
                        frame.add(new JLabel("Failed to load image"));
                    }
                } catch (IOException e) {
                    frame.add(new JLabel("Error: " + e.getMessage()));
                }
            }

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    public static void main(String[] args) {
        DogImageViewer viewer = new DogImageViewer();
        try {
            java.util.List<String> urls = viewer.fetchRandomSubBreedImageUrls();
            System.out.println("Fetched URLs: " + urls);
            viewer.showImagesInGui(urls);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
