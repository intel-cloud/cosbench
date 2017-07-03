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

package com.emc.vipr.cosbench.ECSStor;

import java.util.PrimitiveIterator.OfInt;
import java.util.Random;

/**
 * @author seibed
 *
 */
public class RandomStringGenerator {

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

    private final OfInt lengthGenerator;
    private final char[] characters;
    private final OfInt characterGenerator;

    /**
     * @param random
     * @param minimum
     * @param maximum
     * @param characterString
     */
    public RandomStringGenerator(Random random, int minimum, int maximum, String characterString) {
        if (characterString == null) {
            characterString = defaultCharacters;
        }
        lengthGenerator = random.ints(minimum, maximum).iterator();
        characters = characterString.toCharArray();
        characterGenerator = random.ints(0, characters.length).iterator();
    }

    /**
     * @return
     */
    public String nextString() {
        StringBuilder stringBuilder = new StringBuilder();
        int length = lengthGenerator.nextInt();
        for (int i = 0; i < length; ++i) {
            stringBuilder.append(characters[characterGenerator.nextInt()]);
        }
        return stringBuilder.toString();
    }

}
