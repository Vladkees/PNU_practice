package com.example.pnu_fx;

import java.io.*;
import java.util.*;

public class LevensteinDistance {
    public static class EditOperation {
        String operation;
        char original, modified;
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

   public int computeLevenshtein(String s1, String s2) {
        int len1 = s1.length(), len2 = s2.length();
        if (len1 < len2) {
            return computeLevenshtein(s2, s1);
        }
        int[] prev = new int[len2 + 1];
        int[] curr = new int[len2 + 1];

        for (int j = 0; j <= len2; j++) prev[j] = j;

        for (int i = 1; i <= len1; i++) {
            curr[0] = i;
            for (int j = 1; j <= len2; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                curr[j] = Math.min(Math.min(curr[j - 1] + 1, prev[j] + 1), prev[j - 1] + cost);
            }
            int[] temp = prev;
            prev = curr;
            curr = temp;
        }
        return prev[len2];
    }

    public List<EditOperation> findDifferencesOptimized(String s1, String s2) {
        List<EditOperation> edits = new ArrayList<>();
        int len1 = s1.length(), len2 = s2.length();
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
        Collections.reverse(edits);
        return edits;
    }

    public List<String> readLargeFile(String filePath, int maxLines) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null && lines.size() < maxLines) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return lines;
    }
}
