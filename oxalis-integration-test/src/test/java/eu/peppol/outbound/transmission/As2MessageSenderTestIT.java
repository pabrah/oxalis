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

package eu.peppol.outbound.transmission;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import eu.peppol.as2.As2Module;
import eu.peppol.as2.PeppolAs2SystemIdentifier;
import eu.peppol.identifier.MessageId;
import eu.peppol.identifier.ParticipantId;
import eu.peppol.identifier.PeppolDocumentTypeId;
import eu.peppol.identifier.PeppolDocumentTypeIdAcronym;
import eu.peppol.security.KeystoreManager;
import eu.peppol.smp.SmpLookupManager;
import eu.peppol.util.GlobalConfiguration;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.io.InputStream;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author steinar
 *         Date: 29.10.13
 *         Time: 11:35
 */
@Test(groups = {"integration"})
@Guice(modules = {TransmissionTestITModule.class, As2Module.class })
public class As2MessageSenderTestIT {

    @Inject @Named("sample-xml-with-sbdh")InputStream inputStream;

    @Inject @Named("invoice-to-itsligo") InputStream itSligoInputStream;

    @Inject SmpLookupManager fakeSmpLookupManager;

    @Inject As2MessageSender as2MessageSender;

    @Inject
    KeystoreManager keystoreManager;

    @Inject
    GlobalConfiguration globalConfiguration;

    /** Verifies that the Google Guice injection of @Named injections works as expected */
    @Test
    public void testInjection() throws Exception {
        assertNotNull(inputStream);
    }

    /**
     * Requires our AS2 server to be up and running at https://localhost:8080/oxalis/as2
     *
     * @throws Exception
     */
    @Test(groups = {"integration"})
    public void sendSampleMessageAndVerify() throws Exception {

        String receiver = "9908:810017902";
        String sender = "9908:810017902";

        ParticipantId recipient = new ParticipantId(receiver);
        PeppolDocumentTypeId documentTypeIdentifier = PeppolDocumentTypeIdAcronym.INVOICE.getDocumentTypeIdentifier();
        SmpLookupManager.PeppolEndpointData endpointData = fakeSmpLookupManager.getEndpointTransmissionData(recipient, documentTypeIdentifier);
        assertNotNull(endpointData.getCommonName());

        MessageId messageId = new MessageId();

        As2MessageSender.SendResult sendResult = as2MessageSender.send(inputStream, recipient, new ParticipantId(sender),
                messageId,
                documentTypeIdentifier, endpointData,
                PeppolAs2SystemIdentifier.valueOf(keystoreManager.getOurCommonName()));

        assertEquals(sendResult.messageId.toString(), messageId.stringValue(), "A new transmission id has been assigned");

        assertNotNull(sendResult.remEvidenceBytes,"Missing REM evidence in sendResult");
        assertNotNull(sendResult.signedMimeMdnBytes, "Missing native evidence in sendResult");
    }


    @Test(enabled = false)
    public void sendReallyLargeFile() throws Exception {
        String receiver = "9908:810017902";
        String sender = "9908:810017902";

        ParticipantId recipient = new ParticipantId(receiver);
        PeppolDocumentTypeId documentTypeIdentifier = PeppolDocumentTypeIdAcronym.INVOICE.getDocumentTypeIdentifier();
        SmpLookupManager.PeppolEndpointData endpointData = fakeSmpLookupManager.getEndpointTransmissionData(recipient, documentTypeIdentifier);
        assertNotNull(endpointData.getCommonName());

        // TODO: generate a really large file and transmit it.
        as2MessageSender.send(inputStream,
                recipient, new ParticipantId(sender),
                new MessageId(),
                documentTypeIdentifier, endpointData,
                PeppolAs2SystemIdentifier.valueOf(keystoreManager.getOurCommonName()));
    }
}
