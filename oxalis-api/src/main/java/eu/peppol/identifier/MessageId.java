/*
 * Copyright (c) 2010 - 2015 Norwegian Agency for Pupblic Government and eGovernment (Difi)
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

import java.io.Serializable;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Unique identification of a message which has been received for further processing.
 * Should not be mixed with {@link TransmissionId}, which represents the transmission of a message.
 * <p>
 * <p>
 * Holds any immutable MessageId, which in the PEPPOL world most probably
 * will be a globally unique UUID with or without a prefix of "uuid:".
 *
 * @author Steinar Overbeck Cook
 * @author Thore Johnsen
 */
public class MessageId implements Serializable {

    private static final long serialVersionUID = -7667237415379267745L;

    private static final String REGEXP = "\\b(uuid:){0,1}\\s*([a-f0-9\\-]*){1}\\s*";
    private static final Pattern pattern = Pattern.compile(REGEXP);

    private String value;

    /**
     * Creates a new instance with a unique UUID
     */
    public MessageId() {
        value = UUID.randomUUID().toString();
    }

    /**
     * Create a new MessageId using the supplied UUID
     *
     * @param messageId any messageid represented as text
     */
    public MessageId(String messageId) {
        if (messageId == null) {
            throw new IllegalArgumentException("MessageId requires a non-null string");
        }
        value = messageId;
    }


    public MessageId(UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("A UUID value required for MessageId");
        }
        value = uuid.toString();
    }

    public String stringValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    /**
     * Returns an UUID instance of the MessageId or throws exception if the contained format is wrong.
     * Note that the UUID instance will not have any "uuid:" prefix, if you need to preserve
     * the exact MessageId you should use the @see stringValue()
     *
     * @throws IllegalStateException, IllegalArgumentException
     */
    public UUID toUUID() {
        return uuidFromStringWithOptionalPrefix(value);
    }

    private UUID uuidFromStringWithOptionalPrefix(String s) {
        Matcher matcher = pattern.matcher(s);
        if (!matcher.matches()) {
            throw new IllegalStateException("Internal error in regexp. Unable to determine UUID of '" + s + "' using regexp " + REGEXP);
        } else {
            return UUID.fromString(matcher.group(2));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessageId messageId = (MessageId) o;

        return value.equals(messageId.value);

    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
