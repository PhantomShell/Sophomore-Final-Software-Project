package com;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Comparator for sorting ClassPeriod objects by Jaro-Winkler
 * distance to a specified search term.
 * <p>
 * Credits to Apache Software Foundation (ASF) for the Jaro-Winkler
 * distance code.
 * @author Jagan Prem, ASF
 * @see Comparator, ClassPeriod
 */
public class ClassPeriodSearchComparator implements Comparator<ClassPeriod> {

	private final double THRESHOLD = 0.7;
	private String searchTerm;
	
	/**
	 * Initializes the class with an empty search term.
	 */
	public ClassPeriodSearchComparator() {
		searchTerm = "";
	}
	
	/**
	 * Sets the search term to the given string.
	 * @param searchTerm The string to set the search term to.
	 */
	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}
	
	/**
	 * Returns information regarding matches within two strings, as per Jaro distance.
	 * @author ASF
	 * @param s1 The search term.
	 * @param s2 The string to find matches in.
	 * @return int[] The information on the matches.
	 */
    private int[] matches(String s1, String s2) {
        String max, min;
        if (s1.length() > s2.length()) {
            max = s1;
            min = s2;
        } else {
            max = s2;
            min = s1;
        }
        int range = Math.max(max.length() / 2 - 1, 0);
        int[] matchIndexes = new int[min.length()];
        Arrays.fill(matchIndexes, -1);
        boolean[] matchFlags = new boolean[max.length()];
        int matches = 0;
        for (int mi = 0; mi < min.length(); mi++) {
            char c1 = min.charAt(mi);
            for (int xi = Math.max(mi - range, 0),
                    xn = Math.min(mi + range + 1, max.length()); xi < xn; xi++) {
                if (!matchFlags[xi] && c1 == max.charAt(xi)) {
                    matchIndexes[mi] = xi;
                    matchFlags[xi] = true;
                    matches++;
                    break;
                }
            }
        }
        char[] ms1 = new char[matches];
        char[] ms2 = new char[matches];
        for (int i = 0, si = 0; i < min.length(); i++) {
            if (matchIndexes[i] != -1) {
                ms1[si] = min.charAt(i);
                si++;
            }
        }
        for (int i = 0, si = 0; i < max.length(); i++) {
            if (matchFlags[i]) {
                ms2[si] = max.charAt(i);
                si++;
            }
        }
        int transpositions = 0;
        for (int mi = 0; mi < ms1.length; mi++) {
            if (ms1[mi] != ms2[mi]) {
                transpositions++;
            }
        }
        int prefix = 0;
        for (int mi = 0; mi < min.length(); mi++) {
            if (s1.charAt(mi) == s2.charAt(mi)) {
                prefix++;
            } else {
                break;
            }
        }
        return new int[] {matches, transpositions / 2, prefix, max.length()};
    }
    
    /**
     * Finds the similarity of two strings based on Jaro-Winkler distance.
     * @author ASF
     * @param s1 The first string to compare.
     * @param s2 The second string to compare.
     * @return double How similar the strings are, from 0-1.
     */
    public double similarity(String s1, String s2) {
        int[] mtp = matches(s1, s2);
        float m = mtp[0];
        if (m == 0) {
            return 0;
        }
        float j = ((m / s1.length() + m / s2.length() + (m - mtp[1]) / m)) / 3;
        float jw = j < THRESHOLD ? j : j + Math.min(0.1f, 1f / mtp[3]) * mtp[2]
                * (1 - j);
        return jw;
    }
    
    /**
     * Returns the distance between a search term and another string.
     * @author ASF
     * @param s1 The search term.
     * @param s2 The other string.
     * @return double The distance between the two strings.
     */
    public double distance(String s1, String s2) {
        return 1.0 - similarity(s1.toLowerCase(), s2.toLowerCase());
    }
	
	@Override
	public int compare(ClassPeriod class1, ClassPeriod class2) {
		String first1 = class1.getFirstName(), last1 = class1.getLastName();
		String first2 = class2.getFirstName(), last2 = class2.getLastName();
		Double distance1 = Math.min(distance(searchTerm, first1), distance(searchTerm, last1) / 2);
		Double distance2 = Math.min(distance(searchTerm, first2), distance(searchTerm, last2) / 2);
		return distance1.compareTo(distance2);
	}

}