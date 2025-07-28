import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// A simple class to hold our decoded (x, y) points using BigInteger
class Point {
    BigInteger x;
    BigInteger y;

    public Point(BigInteger x, BigInteger y) {
        this.x = x;
        this.y = y;
    }
}

public class Main {

    public static void main(String[] args) {
        // Process both test cases as required
        solveForFile("testcase1.json");
        solveForFile("testcase2.json");
    }

    /**
     * Orchestrates the process for a single file.
     */
    public static void solveForFile(String filePath) {
        try {
            // 1. Read the entire file into a single string
            String jsonContent = new String(Files.readAllBytes(Paths.get(filePath)));
            
            // --- THIS IS THE FIX ---
            // Remove all whitespace (spaces, tabs, newlines) to make parsing reliable.
            jsonContent = jsonContent.replaceAll("\\s", "");
            
            // 2. Manually parse the JSON to get the necessary data
            int k = parseK(jsonContent);
            List<Point> points = new ArrayList<>();

            // We only need k points to solve the polynomial
            for (int i = 1; i <= k; i++) {
                // Find the JSON object for the current point
                String pointBlock = getJsonObject(jsonContent, String.valueOf(i));
                
                if (pointBlock != null) {
                    // Extract the base and value from the point's JSON object
                    String baseStr = getJsonValue(pointBlock, "base");
                    String valueStr = getJsonValue(pointBlock, "value");

                    int base = Integer.parseInt(baseStr);
                    BigInteger x = new BigInteger(String.valueOf(i));
                    // BigInteger can parse a string in any base, which is perfect for this
                    BigInteger y = new BigInteger(valueStr, base);
                    
                    points.add(new Point(x, y));
                }
            }
            
            // 3. Calculate the secret using Lagrange Interpolation
            BigInteger secret = findSecret(points);
            
            // 4. Print the final result
            System.out.println("The secret for " + filePath + " is: " + secret);

        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath);
            e.printStackTrace();
        }
    }
    // --- Core Calculation Logic ---

    /**
     * Finds the secret c = f(0) using Lagrange Interpolation.
     * f(0) = SUM { y_j * L_j(0) } for j = 0 to k-1
     * L_j(0) = PRODUCT { -x_i / (x_j - x_i) } for i = 0 to k-1, i != j
     */
    public static BigInteger findSecret(List<Point> points) {
        BigInteger secret = BigInteger.ZERO;
        int k = points.size();

        for (int j = 0; j < k; j++) {
            BigInteger y_j = points.get(j).y;
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (int i = 0; i < k; i++) {
                if (i == j) {
                    continue;
                }
                
                BigInteger x_i = points.get(i).x;
                BigInteger x_j = points.get(j).x;

                // Numerator part: multiply by -x_i
                numerator = numerator.multiply(x_i.negate());

                // Denominator part: multiply by (x_j - x_i)
                denominator = denominator.multiply(x_j.subtract(x_i));
            }
            
            // Calculate the full term for y_j
            BigInteger term = y_j.multiply(numerator).divide(denominator);
            
            // Add it to our total sum
            secret = secret.add(term);
        }
        
        return secret;
    }
    
    // --- Manual JSON Parsing Helpers ---

    /**
     * A very simple parser to get a value for a key from a JSON string.
     * Assumes the value is a string enclosed in double quotes.
     * Example: getJsonValue("{\"key\":\"value\"}", "key") returns "value"
     */
    private static String getJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\":\"";
        int keyIndex = json.indexOf(searchKey);
        if (keyIndex == -1) return null;

        int valueStartIndex = keyIndex + searchKey.length();
        int valueEndIndex = json.indexOf('"', valueStartIndex);
        
        return json.substring(valueStartIndex, valueEndIndex);
    }
    
    /**
     * A simple parser to extract an integer value for k.
     * This version correctly handles whitespace between the colon and the number.
     */
    private static int parseK(String json) {
        String searchKey = "\"k\":";
        int keyIndex = json.indexOf(searchKey);
        if (keyIndex == -1) throw new RuntimeException("Key 'k' not found in JSON");

        int currentIndex = keyIndex + searchKey.length();

        // Step 1: Skip any whitespace after the colon
        while (currentIndex < json.length() && Character.isWhitespace(json.charAt(currentIndex))) {
            currentIndex++;
        }

        // Step 2: Find the end of the number
        int valueStartIndex = currentIndex;
        while (currentIndex < json.length() && Character.isDigit(json.charAt(currentIndex))) {
            currentIndex++;
        }
        int valueEndIndex = currentIndex;
        
        // Extract the number string and parse it
        String numberStr = json.substring(valueStartIndex, valueEndIndex);
        if (numberStr.isEmpty()) {
            throw new RuntimeException("Could not find a number for key 'k'");
        }
        return Integer.parseInt(numberStr);
    }
    
    /**
     * Finds the string representing a JSON object for a given key.
     * Example: getJsonObject(..., "1") returns "{\"base\":\"10\",\"value\":\"4\"}"
     */
    private static String getJsonObject(String json, String key) {
        String searchKey = "\"" + key + "\":{";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) return null;
        
        // Find the matching closing brace '}' for the object
        int braceCount = 1;
        int endIndex = startIndex + searchKey.length();
        while (endIndex < json.length() && braceCount > 0) {
            char c = json.charAt(endIndex);
            if (c == '{') braceCount++;
            if (c == '}') braceCount--;
            endIndex++;
        }
        
        return json.substring(startIndex + searchKey.length() - 1, endIndex);
    }
}
