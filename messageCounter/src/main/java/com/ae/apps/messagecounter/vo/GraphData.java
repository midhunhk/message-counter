/*
 * Copyright 2013 Midhun Harikumar
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ae.apps.messagecounter.vo;

/**
 * A data holder class that holds the list of labels and value in degrees
 * 
 * @author midhun_harikumar
 * 
 */
public class GraphData {

	private float[]		valueInDegrees;
	private String[]	labels;

	/**
	 * @return the valueInDegrees
	 */
	public float[] getValueInDegrees() {
		return valueInDegrees;
	}

	/**
	 * @param valueInDegrees
	 *            the valueInDegrees to set
	 */
	public void setValueInDegrees(float[] valueInDegrees) {
		this.valueInDegrees = valueInDegrees;
	}

	/**
	 * @return the labels
	 */
	public String[] getLabels() {
		return labels;
	}

	/**
	 * @param labels
	 *            the labels to set
	 */
	public void setLabels(String[] labels) {
		this.labels = labels;
	}

}