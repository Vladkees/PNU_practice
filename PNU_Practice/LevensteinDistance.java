import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class LevensteinDistance {

        public int calculation(String s1, String s2) {
            int len1 = s1.length();
            int len2 = s2.length();
            int[][] dp = new int[len1 + 1][len2 + 1];

            for (int i = 0; i <= len1; i++) {
                for (int j = 0; j <= len2; j++) {
                    if (i == 0) {
                        dp[i][j] = j;
                    } else if (j == 0) {
                        dp[i][j] = i;
                    } else {
                        int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                        dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
                    }
                }
            }
            return dp[len1][len2];
        }

        public String readFileContent(String filePath) {
            try {
                return new String(java.nio.file.Files.readAllBytes(new File(filePath).toPath()));
            } catch (IOException e) {
                System.out.println("Error reading file: " + e.getMessage());
                return "";
            }
        }

        public void printResult() {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Press '0' to upload text || Press '1' to enter text");
            int code = scanner.nextInt();
            scanner.nextLine();

            switch (code) {
                case 0:
                    System.out.println("Enter path to the first file:");
                    String file1Path = scanner.nextLine().trim();
                    System.out.println("Enter path to the second file:");
                    String file2Path = scanner.nextLine().trim();

                    if (file1Path.isEmpty() || file2Path.isEmpty()) {
                        System.out.println("Files paths cannot be empty.");
                        return;
                    }

                    String firstWord = readFileContent(file1Path);
                    String secondWord = readFileContent(file2Path);
                    System.out.println("The distance between \n'" + firstWord + "' and \n'" + secondWord + "'\n is " + calculation(firstWord, secondWord));
                    break;

                case 1:
                    System.out.println("Print first word:");
                    String firstInput = scanner.nextLine();
                    System.out.println("Print second word:");
                    String secondInput = scanner.nextLine();
                    System.out.println("The distance between \n'" + firstInput + "' and \n'" + secondInput + "'\n is " + calculation(firstInput, secondInput));
                    break;

                default:
                    System.out.println("Invalid input.");
                    break;
            }
            scanner.close();
        }
    }


