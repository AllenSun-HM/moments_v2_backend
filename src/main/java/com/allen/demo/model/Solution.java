package com.allen.demo.model;

import org.junit.Test;

import java.util.*;

public class Solution {
//    public int lengthOfLIS(int[] nums) {
//        //dp记录当前遍历到的位置的LTS
//        int[] dp = new int[nums.length];
//        Arrays.fill(dp, 1);
//        for (int i = 1; i < nums.length; i++) {
//            // 比较前面的数是否小于当前的数
//            for (int j = i -1; j >= 0; j--) {
//                if (nums[j] < nums[i]) {
//                    dp[i] = dp[j] + 1;
//                    break;
//                }
//            }
//        }
//        return  dp[nums.length - 1];
//    }

public String longestPalindrome(String s) {
    if (s.length() < 2) {
        return s;
    }
    boolean[][] dp = new boolean[s.length()][s.length()];
    String result = s.substring(0, 1);
    for (int k = 0; k < s.length(); k++) {
        dp[k][k] = true;
        if (k + 1 < s.length()) {
            dp[k][k + 1] = (s.charAt(k) == s.charAt(k + 1));
            if (dp[k][k + 1]) {
                result = s.substring(k, k + 2);
            }
        }
    }
    for (int i = 0; i < s.length(); i++) {
        for (int j = i + 2; j < s.length(); j++) {
            if (s.charAt(i) == s.charAt(j)) {
                dp[i][j] = dp[i + 1][j - 1];
                if (dp[i][j] && (j - i + 1) > result.length()) {
                    result = s.substring(i, j + 1);
                }
            }
            else {
                dp[i][j] = false;
            }
        }
    }
    return result;
}

    public int lengthOfLongestSubstring(String s) {
        HashMap<Character, Integer> charIndexMap = new HashMap<Character, Integer>();
        int start = 0;
        int result = 0;
        int length = 0;
        for(int i = 0; i < s.length(); i++) {
            if (charIndexMap.keySet().contains(s.charAt(i))) {
                start = Math.max(charIndexMap.get(s.charAt(i)), start - 1);
                length = i - start;
                result = length > result ? length : result;
            }
            else {
                length = i - start + 1;
                result = length > result ? length : result;
            }
            charIndexMap.put(s.charAt(i), i);
        }
        return result;
    }

    @Test
    public void subStringTest() {
        String s =  "aabaab!bb";
        System.out.println(lengthOfLongestSubstring(s));
    }
    @Test
    public void partitionTest() {
        String s = "aaaaa";
        longestPalindrome(s);
    }
}