/*
 * Copyright (c) 2010 - 2016 Norwegian Agency for Public Government and eGovernment (Difi)
 *
 * This file is part of Oxalis.
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission
 * - subsequent versions of the EUPL (the "Licence"); You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl5
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the Licence
 *  is distributed on an "AS IS" basis,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the Licence for the specific language governing permissions and limitations under the Licence.
 *
 */

package eu.peppol.identifier;

import org.testng.annotations.Test;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.testng.Assert.assertEquals;

/**
 * @author steinar
 *         Date: 09.11.2016
 *         Time: 18.09
 */
public class LocaleTest {

    @Test
    public void whatIsDefaultLocale() {
        Locale aDefault = Locale.getDefault();
        System.out.println("Default locale, country: " + aDefault.getCountry());
    }

    @Test
    public void test() {
        String[] strings = {"NO:ORGNR", "DUNS"};
        for (String s : strings) {
            String[] split = s.split(":");
            System.out.println(split[0]);
        }
    }

    @Test
    public void stream() {

        String participantId = "NO976098897";

        List<SchemeId> matchingSchemes = Stream.of(SchemeId.values())
                .filter(schemeId -> participantId.toUpperCase().startsWith(schemeId.getSchemeId().split(":")[0]))
                .collect(toList());

        assertEquals(matchingSchemes.size(), 1);

    }

}
