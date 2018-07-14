/**
 * Copyright 2017 EMC Corporation. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.emc.ecs.cosbench;

import java.util.Random;

/**
 * @author seibed
 *
 */
public class RandomStringGenerator extends MetadataGenerator {

    private final static String defaultCharacters;
    static {
        StringBuilder stringBuilder = new StringBuilder();
        for (char character = 'a'; character <= 'z'; ++character) {
            stringBuilder.append(character);
        }
        for (char character = 'A'; character <= 'Z'; ++character) {
            stringBuilder.append(character);
        }
        for (char character = '0'; character <= '9'; ++character) {
            stringBuilder.append(character);
        }
        defaultCharacters = stringBuilder.toString();
    }

    private final Random random;
    private final int minimum;
    private final int difference;
    private final char[] characters;

    /**
     * @param random
     * @param valuesArray 
     * @param minimum
     * @param maximum
     * @param characterString
     */
    public RandomStringGenerator(Random random, String[] valuesArray, int minimum, int maximum, String characterString) {
        super(random, valuesArray);
        if ((characterString == null) || "".equals(characterString.trim())) {
            characterString = defaultCharacters;
        }
        this.random = random;
        this.minimum = minimum;
        this.difference = maximum - minimum;
        characters = characterString.toCharArray();
    }

    /* (non-Javadoc)
     * @see com.emc.ecs.cosbench.MetadataGenerator#specificNextString()
     */
    @Override
    protected String specificNextString() {
        StringBuilder stringBuilder = new StringBuilder();
        int length = minimum + random.nextInt(difference);
        for (int i = 0; i < length; ++i) {
            stringBuilder.append(characters[random.nextInt(characters.length)]);
        }
        return stringBuilder.toString();
    }

}
