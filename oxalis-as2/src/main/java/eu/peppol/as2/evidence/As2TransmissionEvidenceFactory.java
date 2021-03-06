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

package eu.peppol.as2.evidence;

import com.google.inject.Inject;
import eu.peppol.PeppolTransmissionMetaData;
import eu.peppol.as2.MdnData;
import eu.peppol.as2.MimeMessageHelper;
import eu.peppol.evidence.TransmissionEvidence;
import eu.peppol.identifier.MessageId;
import eu.peppol.security.KeystoreManager;
import eu.peppol.xsd.ticc.receipt._1.TransmissionRole;
import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.InstanceIdentifier;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.TransportProtocol;
import no.difi.vefa.peppol.evidence.rem.EventCode;
import no.difi.vefa.peppol.evidence.rem.RemEvidenceBuilder;
import no.difi.vefa.peppol.evidence.rem.RemEvidenceService;
import no.difi.vefa.peppol.evidence.rem.SignedRemEvidence;
import org.jetbrains.annotations.NotNull;

import javax.mail.internet.MimeMessage;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Creates instances of TransmissionEvidence based upon the information available when using the AS2 protocol with MDN contained in S/MIME.
 *
 * @author steinar
 *         Date: 17.11.2015
 *         Time: 14.57
 */
public class As2TransmissionEvidenceFactory {

    private final RemEvidenceService remEvidenceService;
    private final KeyStore.PrivateKeyEntry privateKeyEntry;

    @Inject
    As2TransmissionEvidenceFactory(RemEvidenceService remEvidenceService, KeystoreManager keystoreManager) {

        this.remEvidenceService = remEvidenceService;
        privateKeyEntry = new KeyStore.PrivateKeyEntry(keystoreManager.getOurPrivateKey(), new Certificate[]{keystoreManager.getOurCertificate()});
    }


    /**
     * Creates TransmissionEvidence based upon the AS2 MDN and other associated data in addition to the actual S/MIME message, which was
     * returned to the sender of the original message.
     *
     * @param sMimeMesssageHoldingMdn the S/MIME message returned to the sender of the original BIS message
     * @param transmissionRole
     * @return instance of the generic TransmissionEvidence
     */
    public TransmissionEvidence createRemWithMdnEvidence(MdnData mdnData, PeppolTransmissionMetaData peppolTransmissionMetaData, MimeMessage sMimeMesssageHoldingMdn, TransmissionRole transmissionRole) {

        if (remEvidenceService == null || privateKeyEntry == null) {
            throw new IllegalStateException("Seems this factory was not properly initialized.");
        }

        if (peppolTransmissionMetaData == null) {
            throw new IllegalArgumentException("PeppolTransmissionMetaData is required argument");
        }
        if (sMimeMesssageHoldingMdn == null) {
            throw new NullPointerException("Argument sMimeMesssageHoldingMdn is required()");
        }
        if (mdnData == null) {
            throw new NullPointerException("as2ReceiptData.getMdnData()");
        } else {
            if (mdnData.getOriginalPayloadDigest() == null) {
                throw new NullPointerException("as2ReceiptData.getMdnData().getOriginalPayloadDigest() is required");
            }
        }


        // Transforms our Oxalis representation of relevant BusDox identifiers into the generic ones
        if (peppolTransmissionMetaData == null) {
            throw new NullPointerException("as2ReceiptData.getPeppolTransmissionMetaData()");
        } else {
            if (peppolTransmissionMetaData.getRecipientId() == null) {
                throw new NullPointerException("as2ReceiptData.getPeppolTransmissionMetaData().getRecipientId()");
            }
            if (peppolTransmissionMetaData.getSenderId() == null) {
                throw new NullPointerException("as2ReceiptData.getPeppolTransmissionMetaData().getSenderId()");
            }
            if (peppolTransmissionMetaData.getDocumentTypeIdentifier() == null) {
                throw new NullPointerException("as2ReceiptData.getPeppolTransmissionMetaData().getDocumentTypeIdentifier()");
            }
            if (peppolTransmissionMetaData.getMessageId() == null) {
                throw new NullPointerException("as2ReceiptData.getPeppolTransmissionMetaData().getTransmissionId()");
            }
        }
        ParticipantIdentifier recipientId = new ParticipantIdentifier(peppolTransmissionMetaData.getRecipientId().stringValue());
        ParticipantIdentifier senderId = new ParticipantIdentifier(peppolTransmissionMetaData.getSenderId().stringValue());
        DocumentTypeIdentifier documentTypeId = new DocumentTypeIdentifier(peppolTransmissionMetaData.getDocumentTypeIdentifier().toString());
        Date receptionTimeStamp = mdnData.getReceptionTimeStamp();



        byte[] digestBytes = mdnData.getOriginalPayloadDigest().getDigest();
        MessageId messageId = new MessageId(peppolTransmissionMetaData.getMessageId().toString());

        As2RemWithMdnTransmissionEvidenceImpl as2RemWithMdnTransmissionEvidence = createEvidence(EventCode.ACCEPTANCE,
                transmissionRole,
                sMimeMesssageHoldingMdn,
                recipientId,
                senderId,
                documentTypeId,
                receptionTimeStamp,
                digestBytes,
                messageId);

        return as2RemWithMdnTransmissionEvidence;
    }



    @NotNull
    public As2RemWithMdnTransmissionEvidenceImpl createEvidence(EventCode eventCode,
                                                                TransmissionRole transmissionRole,
                                                                MimeMessage mimeMessage,
                                                                ParticipantIdentifier recipientId,
                                                                ParticipantIdentifier senderId,
                                                                DocumentTypeIdentifier documentTypeId,
                                                                Date receptionTimeStamp,
                                                                byte[] digestBytes,
                                                                MessageId messageId) {

        RemEvidenceBuilder remEvidenceBuilder = remEvidenceService.createRelayRemMdAcceptanceRejectionBuilder();
        remEvidenceBuilder
                .eventCode(eventCode)
                // time stamp from the MDN
                .eventTime(receptionTimeStamp)
                // The sender of the BIS message
                .senderIdentifier(senderId)
                // The receiver of the BIS message
                .recipientIdentifer(recipientId)
                // The document type identificator (BIS doc. type id)
                .documentTypeId(documentTypeId)
                // The unique messageId assigned when message was first received here
                .instanceIdentifier(new InstanceIdentifier(messageId.toString()))
                // Digest of the original payload
                .payloadDigest(digestBytes)
                // The bytes of the S/MIME message holding the signed MDN
                .protocolSpecificEvidence(
                        transmissionRole,
                        TransportProtocol.AS2,
                        MimeMessageHelper.toBytes(mimeMessage)
                )
        ;

        // Signs and builds the REMEvidenceType with the S/MIME holding the MDN, included in the Extensions section of the REM
        long start = System.nanoTime();

        SignedRemEvidence signedRemEvidence = remEvidenceBuilder.buildRemEvidenceInstance(privateKeyEntry);

        long elapsed = System.nanoTime() - start;
        System.out.println("RemEvidenceBuilder used: " + TimeUnit.MILLISECONDS.convert(elapsed, TimeUnit.NANOSECONDS));

        // Finally wrap it inside something that realizes the TransmissionEvidence interface
        return new As2RemWithMdnTransmissionEvidenceImpl(signedRemEvidence, mimeMessage);
    }


}
