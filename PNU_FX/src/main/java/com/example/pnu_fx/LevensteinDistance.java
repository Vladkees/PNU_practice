package com.example.pnu_fx;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
public class LevensteinDistance {
    public static class EditOperation {
        String operation;
        char original;
        char modified;
        int position;

        public EditOperation(String operation, char original, char modified, int position) {
            this.operation = operation;
            this.original = original;
            this.modified = modified;
            this.position = position;
        }

        @Override
        public String toString() {
            return String.format("%s at position %d: '%c' -> '%c'", operation, position, original, modified);
        }
    }

    public List<EditOperation> findDifferences(String s1, String s2) {
        s1 = cleanString(s1);
        s2 = cleanString(s2);

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

        List<EditOperation> edits = new ArrayList<>();
        int i = len1, j = len2;

        while (i > 0 || j > 0) {
            if (i > 0 && dp[i][j] == dp[i - 1][j] + 1) {
                edits.add(new EditOperation("Deletion", s1.charAt(i - 1), '-', i - 1));
                i--;
            } else if (j > 0 && dp[i][j] == dp[i][j - 1] + 1) {
                edits.add(new EditOperation("Insertion", '-', s2.charAt(j - 1), j - 1));
                j--;
            } else if (i > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] + 1) {
                edits.add(new EditOperation("Substitution", s1.charAt(i - 1), s2.charAt(j - 1), i - 1));
                i--;
                j--;
            } else {
                i--;
                j--;
            }
        }

        return edits;
    }

//    public int calculation(String s1, String s2) {
//        s1 = cleanString(s1);
//        s2 = cleanString(s2);
//
//        int len1 = s1.length();
//        int len2 = s2.length();
//        int[][] dp = new int[len1 + 1][len2 + 1];
//
//        for (int i = 0; i <= len1; i++) {
//            for (int j = 0; j <= len2; j++) {
//                if (i == 0) {
//                    dp[i][j] = j;
//                } else if (j == 0) {
//                    dp[i][j] = i;
//                } else {
//                    int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
//                    dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
//                }
//            }
//        }
//        return dp[len1][len2];
//    }

    private String cleanString(String input) {
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                sb.append(Character.toLowerCase(c));
            }
        }
        return sb.toString();
    }

    public String readFileContent(String filePath) {
        try {
            return new String(java.nio.file.Files.readAllBytes(new File(filePath).toPath()));
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return "";
        }
    }


}
