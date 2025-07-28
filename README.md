# Shamir's Secret Sharing Solver

This project is a command-line application written in Java that solves a simplified version of Shamir's Secret Sharing scheme. It calculates the secret (the constant term 'c' of a polynomial) given a set of points on that polynomial, as defined in a JSON input file.

## Features

-   **Pure Java:** Implemented using only standard Java libraries with no external dependencies.
-   **Manual JSON Parsing:** Includes a robust parser for the specific JSON format required by the assignment.
-   **Large Number Support:** Uses `java.math.BigInteger` to handle arbitrarily large coefficients and values, as required by the 256-bit constraint.
-   **Lagrange Interpolation:** Implements the Lagrange Interpolation method to find the secret `f(0)` directly and efficiently.

## How to Run

1.  **Prerequisite:** Ensure you have the Java Development Kit (JDK) installed.
2.  **Compile the code:**
    ```sh
    javac -d . src/Main.java
    ```
3.  **Run the program:**
    ```sh
    java Main
    ```

## Expected Output
