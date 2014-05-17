package com.ae.apps.messagecounter.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import android.content.res.Resources;

import com.ae.apps.common.vo.ContactVo;
import com.ae.apps.messagecounter.vo.ContactMessageVo;

/**
 * Methods and info for Mocking our data
 * 
 * @author MidhunHK
 * 
 */
public class MockDataUtils {

	private static final String	LOCALE_FR		= "fr";
	private static final String	LOCALE_ES		= "es";

	/**
	 * Mock names, TODO read from resources
	 */
	private static String		mockNamesEN[]	= { "Elliot Hobbs", "Aidan Parry", "Daisy Forster", "Luis Gibson",
			"Martin J. Fox", "Catherine", "James" };
	private static String		mockNamesES[]	= { "Bicor Adomo Abrego", "Fortuna Granado Fonseca",
			"Germana Ruvalcaba", "Sotero Jimínez Razo", "Olimpia Campos Curiel", "Folco Vega Girón",
			"Aidee Padrón Cazares"				};
	private static String		mockNamesFR[]	= { "Bicor Adomo Abrego", "Fortuna Granado Fonseca",
			"Germana Mena Ruvalcaba", "Sotero Jimínez Razo", "Olimpia Campos Curiel", "Folco Vega Girón",
			"Aidee Padrón Cazares"				};

	/**
	 * A mock implementation for giving mock results. Used mainly for screenshots
	 * 
	 * @return
	 */
	public static List<ContactMessageVo> getMockContactMessageList(Resources resource) {
		Random random = new Random();
		int startSeed = 180;
		int prevRandom = 0;
		int currRandom = 0;
		ContactVo contactVo = null;
		ContactMessageVo messageVo = null;
		List<ContactMessageVo> mockedList = new ArrayList<ContactMessageVo>();

		// Supply different set of mock names based on current locale, default is EN
		String[] mockNames = mockNamesEN;
		String locale = Locale.getDefault().getLanguage();
		if (LOCALE_ES.equalsIgnoreCase(locale)) {
			mockNames = mockNamesES;

		} else if (LOCALE_FR.equalsIgnoreCase(locale)) {
			mockNames = mockNamesFR;
		}

		for (String name : mockNames) {
			contactVo = new ContactVo();
			contactVo.setName(name);
			messageVo = new ContactMessageVo();
			messageVo.setContactVo(contactVo);
			if (prevRandom == 0) {
				currRandom = startSeed;
			} else {
				currRandom = random.nextInt(startSeed);
			}
			if (currRandom > prevRandom) {
				currRandom = (int) (currRandom * 0.75);
			}
			messageVo.setMessageCount(currRandom);
			prevRandom = currRandom;
			startSeed -= 2;
			mockedList.add(messageVo);
		}
		return mockedList;
	}
}
