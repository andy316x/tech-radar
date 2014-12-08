package com.ai.techradar.util;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class MD5HashGenerator {

	public static void main(final String[] args) {
		try {
			for(final String arg : args) {
				System.out.println(TechRadarUtil.MD5(arg.trim()));
			}
		} catch (final NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (final UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

}
