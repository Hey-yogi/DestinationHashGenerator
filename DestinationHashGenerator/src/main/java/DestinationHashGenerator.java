import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class DestinationHashGenerator {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java -jar <jar_name>.jar <roll_number> <json_file_path>");
            return;
        }

        String rollNumber = args[0].toLowerCase();
        String filePath = args[1];
        try {
            JsonObject jsonObject = JsonParser.parseReader(new FileReader(filePath)).getAsJsonObject();
            String destinationValue = findDestination(jsonObject);
            if (destinationValue == null) {
                System.out.println("Key 'destination' not found in the JSON file.");
                return;
            }

            String randomString = generateRandomString(8);
            String concatenated = rollNumber + destinationValue + randomString;
            String hash = generateMD5Hash(concatenated);
            System.out.println(hash + ";" + randomString);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static String findDestination(JsonObject jsonObject) {
        for (String key : jsonObject.keySet()) {
            JsonElement element = jsonObject.get(key);
            if (key.equals("destination")) {
                return element.getAsString();
            } else if (element.isJsonObject()) {
                String result = findDestination(element.getAsJsonObject());
                if (result != null) return result;
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
